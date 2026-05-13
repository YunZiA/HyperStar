import kotlin.reflect.KClass

@Target(AnnotationTarget.FUNCTION)
@Retention(AnnotationRetention.SOURCE)
annotation class SearchRoute(val route: KClass<*>, val tabIndex: Int = -1)