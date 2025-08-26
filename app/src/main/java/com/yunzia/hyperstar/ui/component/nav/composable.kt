package com.yunzia.hyperstar.ui.component.nav

import android.util.Log
import androidx.compose.animation.AnimatedContentScope
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.InternalAnimationApi
import androidx.compose.animation.core.animateDp
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.clipToBounds
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.navigation.NamedNavArgument
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDeepLink
import androidx.navigation.NavGraphBuilder
import androidx.navigation.compose.ComposeNavigator
import androidx.navigation.compose.ComposeNavigatorDestinationBuilder
import androidx.navigation.get
import com.yunzia.hyperstar.ui.component.helper.getSystemCornerRadius
import kotlinx.coroutines.delay
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape
import top.yukonga.miuix.kmp.utils.getRoundedCorner
import kotlin.collections.forEach
import kotlin.math.log

@OptIn(InternalAnimationApi::class)
public fun NavGraphBuilder.composable(
    route: String,
    arguments: List<NamedNavArgument> = emptyList(),
    deepLinks: List<NavDeepLink> = emptyList(),
    enterTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        null,
    exitTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        null,
    popEnterTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition?)? =
        enterTransition,
    popExitTransition:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition?)? =
        exitTransition,
    sizeTransform:
    (@JvmSuppressWildcards
    AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
        null,
    content: @Composable AnimatedContentScope.(NavBackStackEntry) -> Unit
) {
    destination(
        ComposeNavigatorDestinationBuilder(
            provider[ComposeNavigator::class],
            route,
            { navBackStackEntry ->
                val sysCorner = getSystemCornerRadius().value
                val transition = this@ComposeNavigatorDestinationBuilder.transition
                val corner = rememberSaveable { mutableStateOf(sysCorner) }



                Log.d(
                    "ggccom",
                    "composable: ${navBackStackEntry.destination.route} + ${route}" +
                            "\n ${transition.currentState} + " +
                            "\n ${transition.targetState} + " +
                            "\n ${route}" +
                            "$sysCorner" + transition.isRunning + transition.hasInitialValueAnimations
                )
                LaunchedEffect(transition.currentState,transition.targetState,transition.isRunning) {
                    val currentState = transition.currentState
                    val targetState = transition.targetState
                    if (navBackStackEntry.destination.route != route) {
                        corner.value = 0f
                        return@LaunchedEffect
                    }
                        if ( currentState == EnterExitState.Visible && targetState == EnterExitState.Visible){
                            corner.value = 0f
                        } else if (transition.isRunning) {
                            corner.value = sysCorner
                        } else{
                            corner.value = 0f

                        }

                }

                Box(
                    Modifier
                        .fillMaxSize()
                        .clip(G2RoundedCornerShape(corner.value.dp))
                ){
                    content(navBackStackEntry)
                }

            }
        )
            .apply {
                arguments.forEach { (argumentName, argument) -> argument(argumentName, argument) }
                deepLinks.forEach { deepLink -> deepLink(deepLink) }
                this.enterTransition = enterTransition
                this.exitTransition = exitTransition
                this.popEnterTransition = popEnterTransition
                this.popExitTransition = popExitTransition
                this.sizeTransform = sizeTransform
            }
    )
}