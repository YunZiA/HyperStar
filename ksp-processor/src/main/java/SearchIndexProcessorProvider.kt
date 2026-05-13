import com.google.devtools.ksp.processing.*

class SearchIndexProcessorProvider : SymbolProcessorProvider {
    override fun create(environment: SymbolProcessorEnvironment): SymbolProcessor {
        return SearchIndexProcessor(
            environment.codeGenerator,
            environment.logger
        )
    }
}