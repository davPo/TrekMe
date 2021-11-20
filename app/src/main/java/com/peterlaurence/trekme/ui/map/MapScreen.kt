package com.peterlaurence.trekme.ui.map

import android.content.Context
import android.content.ContextWrapper
import android.os.Build
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Menu
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalLifecycleOwner
import androidx.compose.ui.res.stringResource
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.peterlaurence.trekme.R
import com.peterlaurence.trekme.viewmodel.map.*
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import ovh.plrapps.mapcompose.ui.MapUI
import android.view.Surface
import androidx.appcompat.app.AppCompatActivity
import com.peterlaurence.trekme.ui.map.components.LandmarkLines

@Composable
fun MapScreen(
    viewModel: MapViewModel = viewModel(),
    lifecycleOwner: LifecycleOwner = LocalLifecycleOwner.current
) {
    LaunchedEffect(lifecycleOwner) {
        launch {
            lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                viewModel.locationFlow.collect {
                    viewModel.onLocationReceived(it)
                }
            }
        }
    }

    val uiState by viewModel.uiState.collectAsState()
    val topBarState by viewModel.topBarState.collectAsState()
    val snackBarEvents = viewModel.snackBarController.snackBarEvents.toList()

    if (uiState is MapUiState) {
        val displayRotation = getDisplayRotation()
        LaunchedEffect(lifecycleOwner, (uiState as MapUiState).isShowingOrientation) {
            if (!(uiState as MapUiState).isShowingOrientation) return@LaunchedEffect
            launch {
                lifecycleOwner.repeatOnLifecycle(Lifecycle.State.RESUMED) {
                    viewModel.orientationFlow.collect {
                        viewModel.setOrientation(it, displayRotation)
                    }
                }
            }
        }
    }

    MapScaffold(
        uiState,
        topBarState,
        snackBarEvents,
        onSnackBarShown = viewModel.snackBarController::onSnackBarShown,
        onMainMenuClick = viewModel::onMainMenuClick,
        onToggleShowOrientation = viewModel::toggleShowOrientation
    )
}

@Composable
fun MapScaffold(
    uiState: UiState,
    topBarState: TopBarState,
    snackBarEvents: List<SnackBarEvent>,
    onSnackBarShown: () -> Unit,
    onMainMenuClick: () -> Unit,
    onToggleShowOrientation: () -> Unit
) {
    val scaffoldState: ScaffoldState = rememberScaffoldState()
    val scope = rememberCoroutineScope()

    if (snackBarEvents.isNotEmpty()) {
        val ok = stringResource(id = R.string.ok_dialog)
        val message = when (snackBarEvents.first()) {
            SnackBarEvent.CURRENT_LOCATION_OUT_OF_BOUNDS -> stringResource(id = R.string.map_screen_loc_outside_map)
        }

        SideEffect {
            scope.launch {
                /* Dismiss the currently showing snackbar, if any */
                scaffoldState.snackbarHostState.currentSnackbarData?.dismiss()

                scaffoldState.snackbarHostState
                    .showSnackbar(message, actionLabel = ok)
            }
            onSnackBarShown()
        }
    }

    Scaffold(
        Modifier.fillMaxSize(),
        scaffoldState = scaffoldState,
        topBar = {
            if (uiState is MapUiState) {
                MapTopAppBar(
                    uiState.isShowingOrientation,
                    onMenuClick = onMainMenuClick,
                    onToggleShowOrientation = onToggleShowOrientation
                )
            } else {
                /* In case of error, we only show the main menu button */
                TopAppBar(
                    title = {},
                    navigationIcon = {
                        IconButton(onClick = onMainMenuClick) {
                            Icon(Icons.Filled.Menu, contentDescription = "")
                        }
                    }
                )
            }
        },
        floatingActionButton = {

        }

    ) {
        when (uiState) {
            Error.LicenseError -> Text(text = "license error")
            Error.EmptyMap -> Text(text = "empty map")
            Loading -> Text(text = "loading")
            is MapUiState -> MapUi(uiState)
        }
    }
}

@Composable
fun MapUi(mapUiState: MapUiState) {
    MapUI(state = mapUiState.mapState) {
        LandmarkLines(
            mapState = mapUiState.mapState,
            positionMarker = mapUiState.landmarkLinesState.positionMarkerSnapshot,
            landmarkPositions = mapUiState.landmarkLinesState.landmarksSnapshot,
            distanceForIdFlow = mapUiState.landmarkLinesState.distanceForLandmark
        )
    }
}

/**
 * We need to know the display rotation (either 0, 90°, 180°, or 270°) - and not just the
 * portrait / landscape mode.
 * To get that information, we only need a [Context] for Android 11 and up. However, on Android 10
 * and below, we need the [AppCompatActivity].
 *
 * @return The angle in decimal degrees
 */
@Composable
private fun getDisplayRotation(): Int {
    val surfaceRotation: Int = if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
        @Suppress("DEPRECATION")
        LocalContext.current.getActivity()?.windowManager?.defaultDisplay?.rotation
            ?: Surface.ROTATION_0
    } else {
        LocalContext.current.display?.rotation ?: Surface.ROTATION_0
    }

    return when (surfaceRotation) {
        Surface.ROTATION_90 -> 90
        Surface.ROTATION_180 -> 180
        Surface.ROTATION_270 -> 270
        else -> 0
    }
}

/**
 * Depending on where the compose tree was originally created, we might have a [ContextWrapper].
 */
private tailrec fun Context.getActivity(): AppCompatActivity? = when (this) {
    is AppCompatActivity -> this
    is ContextWrapper -> baseContext.getActivity()
    else -> null
}