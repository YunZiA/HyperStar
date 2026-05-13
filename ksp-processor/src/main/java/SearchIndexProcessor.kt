// SearchIndexProcessor.kt

import com.google.devtools.ksp.processing.*
import com.google.devtools.ksp.symbol.*
import java.io.File

class SearchIndexProcessor(
    private val codeGenerator: CodeGenerator,
    private val logger: KSPLogger
) : SymbolProcessor {

    private val allResults = mutableListOf<SearchResult>()
    private var callRegex: Regex = Regex("(?!)")
    private val searchRouteMap = mutableMapOf<String, SearchRouteInfo>() // pageFuncName -> SearchRouteInfo
    private val routeTitleResults = linkedMapOf<String, RouteTitleResult>()
    private val routeTabTitleResults = linkedMapOf<String, RouteTabTitleResult>()
    private val sourceFiles = linkedSetOf<KSFile>()
    private val routeEntranceMap = mutableMapOf<String, SearchRouteInfo>()
    private val ignoredFuncParamNames = mutableMapOf<String, List<String>>()

    private val stringResRegex = Regex("""\b(R\.string\.[A-Za-z_][A-Za-z0-9_]*)\b""")

    private val routeOrderList = listOf(
        "MainRoutes.SystemUI",
        "MainRoutes.Home",
        "MainRoutes.ThemeManager",
        "MainRoutes.MMS",
        "MainRoutes.Barrage",
        "MainRoutes.Screenshot"
    )

    private fun getTopLevelRoute(routeClass: String): String? {
        return when {
            routeClass.startsWith("SystemUIRoutes") -> "MainRoutes.SystemUI"
            routeClass.startsWith("ColorEditRoutes") -> "MainRoutes.SystemUI"
            routeClass.startsWith("MediaRoutes") -> "MainRoutes.SystemUI"
            routeClass.startsWith("PowerMenuRoutes") -> "MainRoutes.SystemUI"
            routeClass.startsWith("MainRoutes.") -> routeClass
            else -> null
        }
    }

    // Functions that wrap a group of preferences — we count them to store
    // the 0-based group index for each preference.
    private val groupFunctionNames = setOf("preferenceGroup", "itemGroup", "itemEffectGroup")

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val targetFiles = buildSearchRouteMap(resolver)
        buildCallRegex(resolver, targetFiles)

        val fileToRouteMap = searchRouteMap.values.associateBy { it.filePath }

        targetFiles.forEach { filePath ->

            val raw = File(filePath).readText()
            val content = removeIgnoredFunctions(raw)

            val pageFuncName = fileToRouteMap[filePath]?.pageFunc
            val searchInfo = if (pageFuncName != null) searchRouteMap[pageFuncName] else null
            val routeClass = searchInfo?.routeClass ?: "null"
            val tabIndex = searchInfo?.tabIndex ?: -1
            if (pageFuncName != null && searchInfo != null) {
                recordPageMetadata(raw, pageFuncName, searchInfo)
            }

            val events = parseCompose(content)
            val stack = ArrayDeque<TempNode>()
            val groupVisibleMap = mutableMapOf<Int, String>()
            val groupTitleMap = mutableMapOf<Int, String>()
            var currentGroupIndex = -1

            events.forEach { event ->
                when (event) {
                    is UiEvent.StartCall -> {
                        val isGroup = event.name in groupFunctionNames
                        if (isGroup) currentGroupIndex++
                        stack.addLast(TempNode(componentName = event.name, isGroup = isGroup))
                    }

                    is UiEvent.Param -> {
                        val node = stack.lastOrNull() ?: return@forEach
                        when (event.name) {
                            "key" -> if (!node.isGroup) node.key = event.value
                            "title" -> {
                                if (!node.isGroup) node.title = event.value
                                else node.groupTitle = event.value
                            }
                            "_pos0" -> {
                                if (node.isGroup) node.groupTitle = event.value
                            }
                            "summary" -> if (!node.isGroup) node.summary = event.value
                            "visible" -> node.visible = event.value
                            "onClick" -> if (node.componentName == "SearchableNavPreference") {
                                node.targetRouteClass = extractNavigateRoute(event.value)
                            }
                        }
                    }

                    is UiEvent.EndCall -> {
                        val node = stack.removeLastOrNull() ?: return@forEach
                        if (node.isGroup) {
                            if (node.visible != null) {
                                groupVisibleMap[currentGroupIndex] = node.visible!!
                            }
                            if (node.groupTitle != null) {
                                groupTitleMap[currentGroupIndex] = node.groupTitle!!
                            }
                        } else if (node.key != null && node.title != null) {
                            val effectiveRoute = node.targetRouteClass ?: routeClass
                            val topLevel = getTopLevelRoute(effectiveRoute)
                            val routeOrder = topLevel?.let { routeOrderList.indexOf(it) }?.takeIf { it >= 0 } ?: Int.MAX_VALUE
                            allResults += SearchResult(
                                key = node.key!!,
                                title = node.title!!,
                                summary = node.summary,
                                visible = node.visible,
                                groupVisible = groupVisibleMap[currentGroupIndex],
                                groupIndex = currentGroupIndex,
                                routeClass = routeClass,
                                targetRouteClass = node.targetRouteClass,
                                pageFunc = pageFuncName ?: "null",
                                tabIndex = tabIndex,
                                routeOrder = routeOrder,
                                groupTitleRes = extractStringRes(groupTitleMap[currentGroupIndex] ?: "")
                            )
                        }
                        val targetRoute = node.targetRouteClass
                        if (targetRoute != null) {
                            routeEntranceMap[targetRoute] = SearchRouteInfo(routeClass, tabIndex, pageFuncName ?: "null", filePath)
                        }
                    }
                }
            }
        }

        return emptyList()
    }

    override fun finish() {
        super.finish()
        if (allResults.isNotEmpty() || routeTitleResults.isNotEmpty() || routeTabTitleResults.isNotEmpty()) {
            generateFile(
                allResults,
                routeTitleResults.values.toList(),
                routeTabTitleResults.values.toList(),
                sourceFiles.toList()
            )
        }
    }

    // ==============================
    // 构建动态正则
    // ==============================

    private fun buildCallRegex(resolver: Resolver, targetFiles: List<String>) {
        val names = linkedSetOf<String>()

        resolver.getSymbolsWithAnnotation(Searchable::class.qualifiedName!!)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach { function ->
                names.add(function.simpleName.asString())
                function.containingFile?.let(sourceFiles::add)
            }

        resolver.getSymbolsWithAnnotation(IgnoreSearchIndex::class.qualifiedName!!)
            .filterIsInstance<KSFunctionDeclaration>()
            .forEach { function ->
                val funcName = function.simpleName.asString()
                names.add(funcName)
                ignoredFuncParamNames[funcName] = function.parameters.map { it.name?.asString() ?: "" }
            }

        names.addAll(groupFunctionNames)

        if (names.isNotEmpty()) {
            val alternatives = names
                .sortedByDescending { it.length }
                .joinToString("|") { Regex.escape(it) }
            callRegex = Regex("""(?<![A-Za-z0-9_])($alternatives)\s*\(""")
        } else {
            callRegex = Regex("(?!)")
        }
    }

    // ==============================
    // 解析 @SearchRoute 注解构建路由映射
    // ==============================

    private fun buildSearchRouteMap(resolver: Resolver): List<String> {
        val annotated = resolver.getSymbolsWithAnnotation(SearchRoute::class.qualifiedName!!)
            .filterIsInstance<KSFunctionDeclaration>()

        val filePaths = mutableSetOf<String>()

        for (func in annotated) {
            val annotation = func.annotations.first {
                it.shortName.asString() == "SearchRoute"
            }
            val routeArg = annotation.arguments.firstOrNull { it.name?.asString() == "route" }
            val tabIndexArg = annotation.arguments.firstOrNull { it.name?.asString() == "tabIndex" }

            val routeType = (routeArg?.value as? KSType)
                ?.declaration?.qualifiedName?.asString()
                ?.removePrefix("com.yunzia.hyperstar.ui.navigation.")

            val tabIndex = (tabIndexArg?.value as? Int) ?: -1
            val funcName = func.simpleName.asString()

            val filePath = (func.containingFile?.location as? FileLocation)?.filePath
                ?.replace("\\", "/")
                ?.takeIf { !it.contains("/build/") }
            if (routeType != null && filePath != null) {
                searchRouteMap[funcName] = SearchRouteInfo(routeType, tabIndex, funcName, filePath)
                filePaths.add(filePath)
                func.containingFile?.let(sourceFiles::add)
            }
        }

        return filePaths.toList()
    }

    private fun recordPageMetadata(content: String, pageFuncName: String, searchInfo: SearchRouteInfo) {
        val body = findFunctionBody(content, pageFuncName) ?: return
        val pageTitleRes = parsePageTitleRes(body)

        if (pageTitleRes != null) {
            if (searchInfo.tabIndex >= 0) {
                val key = "${searchInfo.routeClass}#${searchInfo.tabIndex}"
                routeTabTitleResults.putIfAbsent(
                    key,
                    RouteTabTitleResult(
                        routeClass = searchInfo.routeClass,
                        tabIndex = searchInfo.tabIndex,
                        titleRes = pageTitleRes,
                        pageFunc = pageFuncName
                    )
                )
            } else {
                routeTitleResults[searchInfo.routeClass] = RouteTitleResult(
                    routeClass = searchInfo.routeClass,
                    titleRes = pageTitleRes,
                    pageFunc = pageFuncName
                )
            }
        }

        parseTabTitleResources(body).forEachIndexed { index, titleRes ->
            val key = "${searchInfo.routeClass}#$index"
            routeTabTitleResults[key] = RouteTabTitleResult(
                routeClass = searchInfo.routeClass,
                tabIndex = index,
                titleRes = titleRes,
                pageFunc = pageFuncName
            )
        }
    }

    private fun parsePageTitleRes(body: String): String? {
        val titleCalls = listOf(
            PageTitleCall("PreferenceScreen", "title"),
            PageTitleCall("ModuleNavPager", "activityTitle"),
            PageTitleCall("SearchModuleNavPager", "activityTitle"),
            PageTitleCall("NavPager", "activityTitle"),
            PageTitleCall("ActivityPager", "activityTitle")
        )

        for (call in titleCalls) {
            val block = findCallArgumentBlock(body, call.name) ?: continue
            val titleValue = splitParams(block).firstNotNullOfOrNull { param ->
                paramValue(param, call.titleParam)
            }
            val titleRes = titleValue?.let(::extractStringRes)
            if (titleRes != null) return titleRes
        }

        return null
    }

    private fun parseTabTitleResources(body: String): List<String> {
        val tabsRegex = Regex("""\b(?:val|var)\s+tabs\s*=\s*listOf\s*\(""")
        val match = tabsRegex.find(body) ?: return emptyList()
        val block = extractParenthesizedBlock(body, match.range.last + 1) ?: return emptyList()
        return splitParams(block).mapNotNull(::extractStringRes)
    }

    private fun paramValue(param: String, name: String): String? {
        val parts = param.split("=", limit = 2)
        if (parts.size != 2) return null
        return parts[1].trim().removeSuffix(",").takeIf {
            parts[0].trim() == name
        }
    }

    private fun extractStringRes(expression: String): String? {
        return stringResRegex.find(expression)?.groupValues?.get(1)
    }

    private fun safeVisibilityExpression(expression: String?): String {
        return when (expression?.trim()) {
            null, "", "true", "{ true }" -> "{ true }"
            "false", "{ false }" -> "{ false }"
            else -> "{ true }"
        }
    }

    private fun extractNavigateRoute(expression: String): String? {
        val match = Regex("""\.navigate\s*\(\s*([A-Za-z_][A-Za-z0-9_]*(?:\.[A-Za-z_][A-Za-z0-9_]*)+)""")
            .find(expression)
            ?: return null
        return match.groupValues[1]
            .removePrefix("com.yunzia.hyperstar.ui.navigation.")
    }

    private fun findCallArgumentBlock(content: String, callName: String): String? {
        val regex = Regex("""(?<![A-Za-z0-9_])${Regex.escape(callName)}\s*\(""")
        val match = regex.find(content) ?: return null
        return extractParenthesizedBlock(content, match.range.last + 1)
    }

    private fun findFunctionBody(content: String, functionName: String): String? {
        val functionRegex = Regex("""\bfun\s+${Regex.escape(functionName)}\s*\(""")
        val match = functionRegex.find(content) ?: return null
        var index = findMatchingDelimiter(content, match.range.last + 1, '(', ')')?.plus(1) ?: return null

        while (index < content.length && content[index].isWhitespace()) {
            index++
        }
        if (index >= content.length || content[index] != '{') return null

        val start = index + 1
        val end = findMatchingDelimiter(content, start, '{', '}') ?: return null
        return content.substring(start, end)
    }

    private fun extractParenthesizedBlock(content: String, start: Int): String? {
        val end = findMatchingDelimiter(content, start, '(', ')') ?: return null
        return content.substring(start, end)
    }

    // ==============================
    // 解析 Compose
    // ==============================

    fun parseCompose(content: String): List<UiEvent> {
        val events = mutableListOf<UiEvent>()
        val matches = callRegex.findAll(content)

        for (match in matches) {

            val before = content.substring(maxOf(0, match.range.first - 100), match.range.first)
            if (before.contains("@IgnoreSearchIndex")) continue

            val name = match.groupValues[1]
            val start = match.range.last + 1

            val blockEnd = findMatchingDelimiter(content, start, '(', ')') ?: continue
            val block = content.substring(start, blockEnd)
            val params = splitParams(block)
            val paramNameList = ignoredFuncParamNames[name]

            events += UiEvent.StartCall(name)

            params.forEachIndexed { index, param ->
                val parts = param.split("=", limit = 2)
                if (parts.size == 2) {
                    val key = parts[0].trim()
                    val value = parts[1].trim().removeSuffix(",")
                    events += UiEvent.Param(key, value)
                } else {
                    val trimmed = param.trim().removeSuffix(",")
                    if (trimmed.isNotEmpty()) {
                        val resolvedName = paramNameList?.getOrNull(index) ?: "_pos$index"
                        events += UiEvent.Param(resolvedName, trimmed)
                    }
                }
            }

            events += UiEvent.EndCall(name)
        }

        return events
    }

    fun splitParams(block: String): List<String> {
        val result = mutableListOf<String>()
        var start = 0
        var parenDepth = 0
        var braceDepth = 0
        var bracketDepth = 0
        var index = 0

        while (index < block.length) {
            val nextIndex = skipIgnorable(block, index)
            if (nextIndex != index) {
                index = nextIndex
                continue
            }

            when (block[index]) {
                '(' -> parenDepth++
                ')' -> if (parenDepth > 0) parenDepth--
                '{' -> braceDepth++
                '}' -> if (braceDepth > 0) braceDepth--
                '[' -> bracketDepth++
                ']' -> if (bracketDepth > 0) bracketDepth--
                ',' -> if (parenDepth == 0 && braceDepth == 0 && bracketDepth == 0) {
                    result += block.substring(start, index)
                    start = index + 1
                }
            }
            index++
        }

        if (start < block.length) {
            result += block.substring(start)
        }

        return result
    }

    private fun findMatchingDelimiter(
        content: String,
        start: Int,
        open: Char,
        close: Char
    ): Int? {
        var index = start
        var depth = 1

        while (index < content.length) {
            val nextIndex = skipIgnorable(content, index)
            if (nextIndex != index) {
                index = nextIndex
                continue
            }

            when (content[index]) {
                open -> depth++
                close -> {
                    depth--
                    if (depth == 0) return index
                }
            }
            index++
        }

        return null
    }

    private fun skipIgnorable(content: String, start: Int): Int {
        if (start >= content.length) return start

        if (content.startsWith("//", start)) {
            val end = content.indexOf('\n', start + 2)
            return if (end == -1) content.length else end + 1
        }

        if (content.startsWith("/*", start)) {
            val end = content.indexOf("*/", start + 2)
            return if (end == -1) content.length else end + 2
        }

        if (content.startsWith("\"\"\"", start)) {
            val end = content.indexOf("\"\"\"", start + 3)
            return if (end == -1) content.length else end + 3
        }

        val quote = content[start]
        if (quote != '"' && quote != '\'') return start

        var index = start + 1
        while (index < content.length) {
            if (content[index] == '\\') {
                index += 2
                continue
            }
            if (content[index] == quote) return index + 1
            index++
        }

        return content.length
    }

    // ==============================
    // 忽略函数
    // ==============================

    fun removeIgnoredFunctions(content: String): String {
        val regex = Regex("""@IgnoreSearchIndex[\s\S]*?fun\s+[\w.]+\s*\([^)]*\)\s*\{""")

        val result = StringBuilder(content)
        var offset = 0

        regex.findAll(content).forEach { match ->
            val start = match.range.first

            var index = match.range.last + 1
            var depth = 1

            while (index < content.length && depth > 0) {
                val nextIndex = skipIgnorable(content, index)
                if (nextIndex != index) {
                    index = nextIndex
                    continue
                }
                when (content[index]) {
                    '{' -> depth++
                    '}' -> depth--
                }
                index++
            }

            val end = index
            result.replace(start - offset, end - offset, "")
            offset += (end - start)
        }

        return result.toString()
    }

    // ==============================
    // 生成文件（关键）
    // ==============================

    private fun generateFile(
        results: List<SearchResult>,
        routeTitles: List<RouteTitleResult>,
        routeTabTitles: List<RouteTabTitleResult>,
        sourceFiles: List<KSFile>
    ) {

        val file = codeGenerator.createNewFile(
            Dependencies(aggregating = true, *sourceFiles.toTypedArray()),
            "generated",
            "SearchIndex"
        )

        file.bufferedWriter().use { writer ->

            writer.appendLine("package generated")
            writer.appendLine()
            writer.appendLine("import androidx.annotation.StringRes")
            writer.appendLine("import com.yunzia.hyperstar.R")
            writer.appendLine("import com.yunzia.hyperstar.ui.navigation.Route")

            val routeClassImports = linkedSetOf<String>()
            results.forEach { r ->
                r.routeClass.split(".").firstOrNull()?.let { routeClassImports.add(it) }
                r.targetRouteClass?.split(".")?.firstOrNull()?.let { routeClassImports.add(it) }
            }
            routeTitles.forEach { it.routeClass.split(".").firstOrNull()?.let { r -> routeClassImports.add(r) } }
            routeTabTitles.forEach { it.routeClass.split(".").firstOrNull()?.let { r -> routeClassImports.add(r) } }
            routeEntranceMap.keys.forEach { k ->
                k.split(".").firstOrNull()?.let { routeClassImports.add(it) }
            }
            routeEntranceMap.values.forEach { v ->
                v.routeClass.split(".").firstOrNull()?.let { routeClassImports.add(it) }
            }
            routeClassImports.sorted().forEach { className ->
                writer.appendLine("import com.yunzia.hyperstar.ui.navigation.$className")
            }
            writer.appendLine()
            writer.appendLine("data class SearchEntry(")
            writer.appendLine("    val key: String,")
            writer.appendLine("    @StringRes val titleRes: Int?,")
            writer.appendLine("    @StringRes val summaryRes: Int? = null,")
            writer.appendLine("    val visible: () -> Boolean = { true },")
            writer.appendLine("    val groupVisible: () -> Boolean = { true },")
            writer.appendLine("    val groupIndex: Int = -1,")
            writer.appendLine("    val routeClass: Route?,")
            writer.appendLine("    val targetRouteClass: Route? = null,")
            writer.appendLine("    val tabIndex: Int = -1,")
            writer.appendLine("    val routeOrder: Int = Int.MAX_VALUE,")
            writer.appendLine("    @StringRes val groupTitleRes: Int? = null")
            writer.appendLine(")")
            writer.appendLine()
            writer.appendLine("data class RouteTitleEntry(")
            writer.appendLine("    val routeClass: Route,")
            writer.appendLine("    @StringRes val titleRes: Int")
            writer.appendLine(")")
            writer.appendLine()
            writer.appendLine("data class RouteTabTitleEntry(")
            writer.appendLine("    val routeClass: Route,")
            writer.appendLine("    val tabIndex: Int,")
            writer.appendLine("    @StringRes val titleRes: Int")
            writer.appendLine(")")
            writer.appendLine()

            writer.appendLine("object SearchIndex {")
            if (results.isEmpty()) {
                writer.appendLine("    val entries = emptyList<SearchEntry>()")
            } else {
                writer.appendLine("    val entries: List<SearchEntry> = listOf(")

                results.forEachIndexed { index, r ->
                    val comma = if (index != results.lastIndex) "," else ""

                    val visibleExpr = safeVisibilityExpression(r.visible)
                    val groupVisibleExpr = safeVisibilityExpression(r.groupVisible)

                    writer.appendLine(
                        """
                        |        SearchEntry(
                        |            key = ${r.key},
                        |            titleRes = ${extractStringRes(r.title)},
                        |            summaryRes = ${extractStringRes(r.summary ?: "") ?: "null"},
                        |            visible = $visibleExpr,
                        |            groupVisible = $groupVisibleExpr,
                        |            groupIndex = ${r.groupIndex},
                        |            routeClass = ${r.routeClass},
                        |            targetRouteClass = ${r.targetRouteClass ?: "null"},
                        |            tabIndex = ${r.tabIndex},
                        |            routeOrder = ${r.routeOrder},
                        |            groupTitleRes = ${r.groupTitleRes ?: "null"}
                        |        )$comma
                        """.trimMargin()
                    )
                }

                writer.appendLine("    )")
            }

            writer.appendLine()
            if (routeTitles.isEmpty()) {
                writer.appendLine("    val routeTitleEntries = emptyList<RouteTitleEntry>()")
            } else {
                writer.appendLine("    val routeTitleEntries = listOf(")
                routeTitles.forEachIndexed { index, title ->
                    val comma = if (index != routeTitles.lastIndex) "," else ""
                    writer.appendLine(
                        """
                        |        RouteTitleEntry(
                        |            routeClass = ${title.routeClass},
                        |            titleRes = ${title.titleRes}
                        |        )$comma
                        """.trimMargin()
                    )
                }
                writer.appendLine("    )")
            }

            writer.appendLine()
            if (routeTabTitles.isEmpty()) {
                writer.appendLine("    val routeTabTitleEntries = emptyList<RouteTabTitleEntry>()")
            } else {
                writer.appendLine("    val routeTabTitleEntries = listOf(")
                routeTabTitles.forEachIndexed { index, title ->
                    val comma = if (index != routeTabTitles.lastIndex) "," else ""
                    writer.appendLine(
                        """
                        |        RouteTabTitleEntry(
                        |            routeClass = ${title.routeClass},
                        |            tabIndex = ${title.tabIndex},
                        |            titleRes = ${title.titleRes}
                        |        )$comma
                        """.trimMargin()
                    )
                }
                writer.appendLine("    )")
            }

            // key -> groupIndex map for direct scroll-to-key lookup
            val indexedResults = results.filter { it.groupIndex >= 0 }
            writer.appendLine()
            if (indexedResults.isEmpty()) {
                writer.appendLine("    val keyGroupMap = emptyMap<String, Int>()")
            } else {
                writer.appendLine("    val keyGroupMap = mapOf(")
                indexedResults.forEachIndexed { index, r ->
                    val comma = if (index != indexedResults.lastIndex) "," else ""
                    writer.appendLine("""        ${r.key} to ${r.groupIndex}$comma""")
                }
                writer.appendLine("    )")
            }

            writer.appendLine()
            if (routeEntranceMap.isEmpty()) {
                writer.appendLine("    val routeEntranceMap = emptyMap<Route, Pair<Route, Int>>()")
            } else {
                writer.appendLine("    val routeEntranceMap: Map<Route, Pair<Route, Int>> = mapOf(")
                routeEntranceMap.entries.forEachIndexed { index, entry ->
                    val comma = if (index != routeEntranceMap.size - 1) "," else ""
                    writer.appendLine("""        ${entry.key} to (${entry.value.routeClass} to ${entry.value.tabIndex})$comma""")
                }
                writer.appendLine("    )")
            }

            writer.appendLine("}")
        }
    }

    data class SearchResult(
        val key: String,
        val title: String,
        val summary: String? = null,
        val visible: String? = null,
        val groupVisible: String? = null,
        val groupIndex: Int = -1,
        val routeClass: String,
        val targetRouteClass: String? = null,
        val pageFunc: String,
        val tabIndex: Int = -1,
        val routeOrder: Int = Int.MAX_VALUE,
        val groupTitleRes: String? = null
    )

    data class RouteTitleResult(
        val routeClass: String,
        val titleRes: String,
        val pageFunc: String
    )

    data class RouteTabTitleResult(
        val routeClass: String,
        val tabIndex: Int,
        val titleRes: String,
        val pageFunc: String
    )

    data class TempNode(
        val componentName: String,
        var key: String? = null,
        var title: String? = null,
        var summary: String? = null,
        var visible: String? = null,
        var targetRouteClass: String? = null,
        val isGroup: Boolean = false,
        var groupTitle: String? = null
    )

    data class SearchRouteInfo(
        val routeClass: String,
        val tabIndex: Int,
        val pageFunc: String,
        val filePath: String
    )

    data class PageTitleCall(val name: String, val titleParam: String)

    sealed class UiEvent {
        data class StartCall(val name: String) : UiEvent()
        data class EndCall(val name: String) : UiEvent()
        data class Param(val name: String, val value: String) : UiEvent()
    }
}
