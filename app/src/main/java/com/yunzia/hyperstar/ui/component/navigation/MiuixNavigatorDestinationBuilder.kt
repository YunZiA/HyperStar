package dev.lackluster.hyperx.compose.navigation


import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.runtime.Composable
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestinationBuilder
import androidx.navigation.NavDestinationDsl
import androidx.navigation.NavType
import androidx.navigation.compose.ComposeNavigator
import com.yunzia.hyperstar.ui.component.navigation.MiuixNavigator
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass
import kotlin.reflect.KType

/** DSL for constructing a new [com.yunzia.hyperstar.ui.component.navigation.MiuixNavigator.Destination] */
@NavDestinationDsl
class MiuixNavigatorDestinationBuilder :
    NavDestinationBuilder<MiuixNavigator.Destination> {

    private val miuixNavigator: MiuixNavigator
    private val content: @Composable (AnimatedContentScope.(NavBackStackEntry) -> Unit)

    var enterTransition:
            (@JvmSuppressWildcards
            AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        null

    var exitTransition:
            (@JvmSuppressWildcards
            AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        null

    var popEnterTransition:
            (@JvmSuppressWildcards
            AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        null

    var popExitTransition:
            (@JvmSuppressWildcards
            AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        null

    var sizeTransform:
            (@JvmSuppressWildcards
            AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
        null

    /**
     * DSL for constructing a new [ComposeNavigator.Destination]
     *
     * @param navigator navigator used to create the destination
     * @param route the destination's unique route
     * @param content composable for the destination
     */
    constructor(
        navigator: MiuixNavigator,
        route: String,
        content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
    ) : super(navigator, route) {
        this.miuixNavigator = navigator
        this.content = content
    }

    /**
     * DSL for constructing a new [ComposeNavigator.Destination]
     *
     * @param navigator navigator used to create the destination
     * @param route the destination's unique route from a [KClass]
     * @param typeMap map of destination arguments' kotlin type [KType] to its respective custom
     *   [NavType]. May be empty if [route] does not use custom NavTypes.
     * @param content composable for the destination
     */
    constructor(
        navigator: MiuixNavigator,
        route: KClass<*>,
        typeMap: Map<KType, @JvmSuppressWildcards NavType<*>>,
        content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
    ) : super(navigator, route, typeMap) {
        this.miuixNavigator = navigator
        this.content = content
    }

    override fun instantiateDestination(): MiuixNavigator.Destination {
        return MiuixNavigator.Destination(miuixNavigator, content)
    }

    override fun build(): MiuixNavigator.Destination {
        return super.build().also { destination ->
            destination.enterTransition = enterTransition
            destination.exitTransition = exitTransition
            destination.popEnterTransition = popEnterTransition
            destination.popExitTransition = popExitTransition
            destination.sizeTransform = sizeTransform
        }
    }
}
