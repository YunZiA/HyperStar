package com.yunzia.processor

import com.google.devtools.ksp.processing.CodeGenerator
import com.google.devtools.ksp.processing.Dependencies
import com.google.devtools.ksp.processing.Resolver
import com.google.devtools.ksp.processing.SymbolProcessor
import com.google.devtools.ksp.symbol.KSAnnotated
import com.google.devtools.ksp.symbol.KSClassDeclaration

class InitProcessor(
    val codeGenerator: CodeGenerator
) : SymbolProcessor {

    override fun process(resolver: Resolver): List<KSAnnotated> {
        val symbols = resolver.getSymbolsWithAnnotation("your.pkg.Init")
        symbols
            .filterIsInstance<KSClassDeclaration>()
            .forEach { classDecl ->
                val annotation = classDecl.annotations.first { it.shortName.asString() == "Init" }
                val pkgName = annotation.arguments.first { it.name?.asString() == "packageName" }.value as String
                val className = classDecl.simpleName.asString()
                val packageName = classDecl.packageName.asString()

                val file = codeGenerator.createNewFile(
                    Dependencies(false, classDecl.containingFile!!),
                    packageName,
                    "${className}KspImpl"
                )
                file.writer().use {
                    it.write(
                        """
                        |package $packageName
                        |
                        |class ${className}KspImpl : $className() {
                        |    override fun getPackageName(): String = "$pkgName"
                        |    
                        |}
                        """.trimMargin()
                    )
                }
            }
        return emptyList()
    }
}
