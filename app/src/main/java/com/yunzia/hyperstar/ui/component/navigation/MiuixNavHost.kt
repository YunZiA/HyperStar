package com.yunzia.hyperstar.ui.component.navigation

import androidx.activity.compose.PredictiveBackHandler
import androidx.collection.mutableObjectFloatMapOf
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.AnimatedContentTransitionScope
import androidx.compose.animation.ContentTransform
import androidx.compose.animation.EnterExitState
import androidx.compose.animation.EnterTransition
import androidx.compose.animation.ExitTransition
import androidx.compose.animation.SizeTransform
import androidx.compose.animation.core.SeekableTransitionState
import androidx.compose.animation.core.animate
import androidx.compose.animation.core.rememberTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.derivedStateOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveableStateHolder
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.viewmodel.compose.LocalViewModelStoreOwner
import androidx.navigation.NavBackStackEntry
import androidx.navigation.NavDestination
import androidx.navigation.NavDestination.Companion.hierarchy
import androidx.navigation.NavGraph
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.Navigator
import androidx.navigation.compose.LocalOwnersProvider
import androidx.navigation.createGraph
import androidx.navigation.get
import kotlin.coroutines.cancellation.CancellationException
import kotlin.jvm.JvmSuppressWildcards
import kotlin.reflect.KClass
import kotlin.reflect.KType
import kotlinx.coroutines.launch
import top.yukonga.miuix.kmp.utils.G2RoundedCornerShape

/**
 * Provides a place in the Compose hierarchy for self contained navigation to occur.
 *
 * Once this is called, any Composable within the given [NavGraphBuilder] can be navigated to from
 * the provided [navController].
 *
 * The builder passed into this method is [remember]ed. This means that for this NavHost, the
 * contents of the builder cannot be changed.
 *
 * @param navController the navController for this host
 * @param startDestination the route for the start destination
 * @param modifier The modifier to be applied to the layout.
 * @param contentAlignment The [Alignment] of the [AnimatedContent]
 * @param route the route for the graph
 * @param enterTransition callback to define enter transitions for destination in this host
 * @param exitTransition callback to define exit transitions for destination in this host
 * @param popEnterTransition callback to define popEnter transitions for destination in this host
 * @param popExitTransition callback to define popExit transitions for destination in this host
 * @param sizeTransform callback to define the size transform for destinations in this host
 * @param builder the builder used to construct the graph
 */
@Composable
fun MiuixNavHost(
    navController: NavHostController,
    startDestination: String,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    cornerRadius: Dp = 0.dp,
    route: String? = null,
    enterTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            miuixEnterTransition()
        },
    exitTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            miuixExitTransition()
        },
    popEnterTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            miuixPopEnterTransition()
        },
    popExitTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            miuixPopExitTransition()
        },
    sizeTransform:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
        null,
    builder: NavGraphBuilder.() -> Unit
) {
    MiuixNavHost(
        navController,
        remember(route, startDestination, builder) {
            navController.createGraph(startDestination, route, builder)
        },
        modifier,
        contentAlignment,
        cornerRadius,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition,
        sizeTransform
    )
}

/**
 * Provides a place in the Compose hierarchy for self contained navigation to occur.
 *
 * Once this is called, any Composable within the given [NavGraphBuilder] can be navigated to from
 * the provided [navController].
 *
 * The builder passed into this method is [remember]ed. This means that for this NavHost, the
 * contents of the builder cannot be changed.
 *
 * @param navController the navController for this host
 * @param startDestination the route from a [KClass] for the start destination
 * @param modifier The modifier to be applied to the layout.
 * @param contentAlignment The [Alignment] of the [AnimatedContent]
 * @param route the route from a [KClass] for the graph
 * @param typeMap map of destination arguments' kotlin type [KType] to its respective custom
 *   [NavType]. May be empty if [route] does not use custom NavTypes.
 * @param enterTransition callback to define enter transitions for destination in this host
 * @param exitTransition callback to define exit transitions for destination in this host
 * @param popEnterTransition callback to define popEnter transitions for destination in this host
 * @param popExitTransition callback to define popExit transitions for destination in this host
 * @param sizeTransform callback to define the size transform for destinations in this host
 * @param builder the builder used to construct the graph
 */
@Composable
fun MiuixNavHost(
    navController: NavHostController,
    startDestination: KClass<*>,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    cornerRadius: Dp = 0.dp,
    route: KClass<*>? = null,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    enterTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            miuixEnterTransition()
        },
    exitTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            miuixExitTransition()
        },
    popEnterTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            miuixPopEnterTransition()
        },
    popExitTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            miuixPopExitTransition()
        },
    sizeTransform:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
        null,
    builder: NavGraphBuilder.() -> Unit
) {
    MiuixNavHost(
        navController,
        remember(route, startDestination, builder) {
            navController.createGraph(startDestination, route, typeMap, builder)
        },
        modifier,
        contentAlignment,
        cornerRadius,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition,
        sizeTransform
    )
}

/**
 * Provides in place in the Compose hierarchy for self contained navigation to occur.
 *
 * Once this is called, any Composable within the given [NavGraphBuilder] can be navigated to from
 * the provided [navController].
 *
 * The builder passed into this method is [remember]ed. This means that for this NavHost, the
 * contents of the builder cannot be changed.
 *
 * @param navController the navController for this host
 * @param startDestination the route from a an Object for the start destination
 * @param modifier The modifier to be applied to the layout.
 * @param contentAlignment The [Alignment] of the [AnimatedContent]
 * @param route the route from a [KClass] for the graph
 * @param typeMap map of destination arguments' kotlin type [KType] to its respective custom
 *   [NavType]. May be empty if [route] does not use custom NavTypes.
 * @param enterTransition callback to define enter transitions for destination in this host
 * @param exitTransition callback to define exit transitions for destination in this host
 * @param popEnterTransition callback to define popEnter transitions for destination in this host
 * @param popExitTransition callback to define popExit transitions for destination in this host
 * @param sizeTransform callback to define the size transform for destinations in this host
 * @param builder the builder used to construct the graph
 */
@Composable
fun MiuixNavHost(
    navController: NavHostController,
    startDestination: Any,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    cornerRadius: Dp = 0.dp,
    route: KClass<*>? = null,
    typeMap: Map<KType, @JvmSuppressWildcards NavType<*>> = emptyMap(),
    enterTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            miuixEnterTransition()
        },
    exitTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            miuixExitTransition()
        },
    popEnterTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            miuixPopEnterTransition()
        },
    popExitTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            miuixPopExitTransition()
        },
    sizeTransform:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
        null,
    builder: NavGraphBuilder.() -> Unit
) {
    MiuixNavHost(
        navController,
        remember(route, startDestination, builder) {
            navController.createGraph(startDestination, route, typeMap, builder)
        },
        modifier,
        contentAlignment,
        cornerRadius,
        enterTransition,
        exitTransition,
        popEnterTransition,
        popExitTransition,
        sizeTransform
    )
}

/**
 * Provides a place in the Compose hierarchy for self contained navigation to occur.
 *
 * Once this is called, any Composable within the given [NavGraphBuilder] can be navigated to from
 * the provided [navController].
 *
 * @param navController the navController for this host
 * @param graph the graph for this host
 * @param modifier The modifier to be applied to the layout.
 * @param contentAlignment The [Alignment] of the [AnimatedContent]
 * @param enterTransition callback to define enter transitions for destination in this host
 * @param exitTransition callback to define exit transitions for destination in this host
 * @param popEnterTransition callback to define popEnter transitions for destination in this host
 * @param popExitTransition callback to define popExit transitions for destination in this host
 * @param sizeTransform callback to define the size transform for destinations in this host
 */
@Composable
fun MiuixNavHost(
    navController: NavHostController,
    graph: NavGraph,
    modifier: Modifier = Modifier,
    contentAlignment: Alignment = Alignment.TopStart,
    cornerRadius: Dp = 0.dp,
    enterTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            miuixEnterTransition()
        },
    exitTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            miuixExitTransition()
        },
    popEnterTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition) =
        {
            miuixPopEnterTransition()
        },
    popExitTransition:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition) =
        {
            miuixPopExitTransition()
        },
    sizeTransform:
        (@JvmSuppressWildcards
        AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform?)? =
        null
) {

    val lifecycleOwner = LocalLifecycleOwner.current
    val viewModelStoreOwner =
        checkNotNull(LocalViewModelStoreOwner.current) {
            "NavHost requires a ViewModelStoreOwner to be provided via LocalViewModelStoreOwner"
        }

    navController.setViewModelStore(viewModelStoreOwner.viewModelStore)

    // Then set the graph
    navController.graph = graph

    // Find the ComposeNavigator, returning early if it isn't found
    // (such as is the case when using TestNavHostController)
    val composeNavigator =
        navController.navigatorProvider.get<Navigator<out NavDestination>>(MiuixNavigator.Companion.NAME)
                as? MiuixNavigator ?: return

    val currentBackStack by composeNavigator.backStack.collectAsState()

    var progress by remember { mutableFloatStateOf(0f) }
    var inPredictiveBack by remember { mutableStateOf(false) }
    PredictiveBackHandler(currentBackStack.size > 1) { backEvent ->
        var currentBackStackEntry: NavBackStackEntry? = null
        if (currentBackStack.size > 1) {
            progress = 0f
            currentBackStackEntry = currentBackStack.lastOrNull()
            composeNavigator.prepareForTransition(currentBackStackEntry!!)
            val previousEntry = currentBackStack[currentBackStack.size - 2]
            composeNavigator.prepareForTransition(previousEntry)
        }
        try {
            backEvent.collect {
                if (currentBackStack.size > 1) {
                    inPredictiveBack = true
                    progress = it.progress
                }
            }
            if (currentBackStack.size > 1) {
                inPredictiveBack = false
                composeNavigator.popBackStack(currentBackStackEntry!!, false)
            }
        } catch (_: CancellationException) {
            if (currentBackStack.size > 1) {
                inPredictiveBack = false
            }
        }
    }

    DisposableEffect(lifecycleOwner) {
        // Setup the navController with proper owners
        navController.setLifecycleOwner(lifecycleOwner)
        onDispose {}
    }

    val saveableStateHolder = rememberSaveableStateHolder()

    val allVisibleEntries by navController.visibleEntries.collectAsState()

    // Intercept back only when there's a destination to pop
    val visibleEntries by remember {
        derivedStateOf {
            allVisibleEntries.filter { entry ->
                entry.destination.navigatorName == MiuixNavigator.Companion.NAME
            }
        }
    }

    val backStackEntry: NavBackStackEntry? = visibleEntries.lastOrNull()

    val zIndices = remember { mutableObjectFloatMapOf<String>() }

    if (backStackEntry != null) {
        val finalEnter: AnimatedContentTransitionScope<NavBackStackEntry>.() -> EnterTransition = {
            val targetDestination = targetState.destination as MiuixNavigator.Destination

            if (composeNavigator.isPop.value || inPredictiveBack) {
                targetDestination.hierarchy.firstNotNullOfOrNull { destination ->
                    destination.createPopEnterTransition(this)
                } ?: popEnterTransition.invoke(this)
            } else {
                targetDestination.hierarchy.firstNotNullOfOrNull { destination ->
                    destination.createEnterTransition(this)
                } ?: enterTransition.invoke(this)
            }
        }

        val finalExit: AnimatedContentTransitionScope<NavBackStackEntry>.() -> ExitTransition = {
            val initialDestination = initialState.destination as MiuixNavigator.Destination

            if (composeNavigator.isPop.value || inPredictiveBack) {
                initialDestination.hierarchy.firstNotNullOfOrNull { destination ->
                    destination.createPopExitTransition(this)
                } ?: popExitTransition.invoke(this)
            } else {
                initialDestination.hierarchy.firstNotNullOfOrNull { destination ->
                    destination.createExitTransition(this)
                } ?: exitTransition.invoke(this)
            }
        }

        val finalSizeTransform:
                AnimatedContentTransitionScope<NavBackStackEntry>.() -> SizeTransform? =
            {
                val targetDestination = targetState.destination as MiuixNavigator.Destination

                targetDestination.hierarchy.firstNotNullOfOrNull { destination ->
                    destination.createSizeTransform(this)
                } ?: sizeTransform?.invoke(this)
            }

        DisposableEffect(true) {
            onDispose {
                visibleEntries.forEach { entry -> composeNavigator.onTransitionComplete(entry) }
            }
        }

        val transitionState = remember {
            // The state returned here cannot be nullable cause it produces the input of the
            // transitionSpec passed into the AnimatedContent and that must match the non-nullable
            // scope exposed by the transitions on the NavHost and composable APIs.
            SeekableTransitionState(backStackEntry)
        }

        val transition = rememberTransition(transitionState, label = "entry")

        if (inPredictiveBack) {
            LaunchedEffect(progress) {
                val previousEntry = currentBackStack[currentBackStack.size - 2]
                transitionState.seekTo(progress, previousEntry)
            }
        } else {
            LaunchedEffect(backStackEntry) {
                // This ensures we don't animate after the back gesture is cancelled and we
                // are already on the current state
                if (transitionState.currentState != backStackEntry) {
                    transitionState.animateTo(backStackEntry)
                } else {
                    // convert from nanoseconds to milliseconds
                    val totalDuration = transition.totalDurationNanos / 1000000
                    // When the predictive back gesture is cancel, we need to manually animate
                    // the SeekableTransitionState from where it left off, to zero and then
                    // snapTo the final position.
                    animate(
                        transitionState.fraction,
                        0f,
                        animationSpec = tween((transitionState.fraction * totalDuration).toInt())
                    ) { value, _ ->
                        this@LaunchedEffect.launch {
                            if (value > 0) {
                                // Seek the original transition back to the currentState
                                transitionState.seekTo(value)
                            }
                            if (value == 0f) {
                                // Once we animate to the start, we need to snap to the right state.
                                transitionState.snapTo(backStackEntry)
                            }
                        }
                    }
                }
            }
        }

        val clipShape = remember(cornerRadius) {
            G2RoundedCornerShape(
                topStart = cornerRadius,
                topEnd = cornerRadius,
                bottomEnd = cornerRadius,
                bottomStart = cornerRadius,
            )
        }

        transition.AnimatedContent(
            modifier,
            transitionSpec = {
                // If the initialState of the AnimatedContent is not in visibleEntries, we are in
                // a case where visible has cleared the old state for some reason, so instead of
                // attempting to animate away from the initialState, we skip the animation.
                if (initialState in visibleEntries) {
                    val initialZIndex = zIndices.getOrPut(initialState.id) { 0f }
                    val targetZIndex =
                        when {
                            targetState.id == initialState.id -> initialZIndex
                            composeNavigator.isPop.value || inPredictiveBack -> initialZIndex - 1f
                            else -> initialZIndex + 1f
                        }
                    zIndices[targetState.id] = targetZIndex

                    ContentTransform(
                        finalEnter(this),
                        finalExit(this),
                        targetZIndex,
                        finalSizeTransform(this)
                    )
                } else {
                    EnterTransition.None togetherWith ExitTransition.None
                }
            },
            contentAlignment,
            contentKey = { it.id }
        ) {
            // In some specific cases, such as clearing your back stack by changing your
            // start destination, AnimatedContent can contain an entry that is no longer
            // part of visible entries since it was cleared from the back stack and is not
            // animating. In these cases the currentEntry will be null, and in those cases,
            // AnimatedContent will just skip attempting to transition the old entry.
            // See https://issuetracker.google.com/238686802
            val isPredictiveBackCancelAnimation = transitionState.currentState == backStackEntry
            val currentEntry =
                if (inPredictiveBack || isPredictiveBackCancelAnimation) {
                    // We have to do this because the previous entry does not show up in
                    // visibleEntries
                    // even if we prepare it above as part of onBackStackChangeStarted
                    it
                } else {
                    visibleEntries.lastOrNull { entry -> it == entry }
                }

            // while in the scope of the composable, we provide the navBackStackEntry as the
            // ViewModelStoreOwner and LifecycleOwner

            currentEntry?.LocalOwnersProvider(saveableStateHolder) {
                val needClip by remember { derivedStateOf {
                    this.transition.isRunning && (
                            (composeNavigator.isPop.value && this.transition.targetState == EnterExitState.PostExit) ||
                            (!composeNavigator.isPop.value && this.transition.targetState == EnterExitState.Visible)
                            )
                } }
                Box(
                    modifier =
                        if (needClip) Modifier.clip(clipShape)
                        else Modifier
                ) {
                    (currentEntry.destination as MiuixNavigator.Destination).content(
                        this@AnimatedContent,
                        currentEntry
                    )
                }
            }
        }
        LaunchedEffect(transition.currentState, transition.targetState) {
            if (
                transition.currentState == transition.targetState &&
                // There is a race condition where previous animation has completed the new
                // animation has yet to start and there is a navigate call before this effect.
                // We need to make sure we are completing only when the start is settled on the
                // actual entry.
                (navController.currentBackStackEntry == null ||
                        transition.targetState == backStackEntry)
            ) {
                visibleEntries.forEach { entry -> composeNavigator.onTransitionComplete(entry) }
                zIndices.removeIf { key, _ -> key != transition.targetState.id }
            }
        }
    }
}

private fun NavDestination.createEnterTransition(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): EnterTransition? =
    when (this) {
        is MiuixNavigator.Destination -> this.enterTransition?.invoke(scope)
        else -> null
    }

private fun NavDestination.createExitTransition(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): ExitTransition? =
    when (this) {
        is MiuixNavigator.Destination -> this.exitTransition?.invoke(scope)
        else -> null
    }

private fun NavDestination.createPopEnterTransition(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): EnterTransition? =
    when (this) {
        is MiuixNavigator.Destination -> this.popEnterTransition?.invoke(scope)
        else -> null
    }

private fun NavDestination.createPopExitTransition(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): ExitTransition? =
    when (this) {
        is MiuixNavigator.Destination -> this.popExitTransition?.invoke(scope)
        else -> null
    }

private fun NavDestination.createSizeTransform(
    scope: AnimatedContentTransitionScope<NavBackStackEntry>
): SizeTransform? =
    when (this) {
        is MiuixNavigator.Destination -> this.sizeTransform?.invoke(scope)
        else -> null
    }

object MiuixNavHostDefaults {
    const val TRANSITION_DURATION = 500

    val NavAnimationEasing = NavTransitionEasing(0.8f, 0.95f)
}