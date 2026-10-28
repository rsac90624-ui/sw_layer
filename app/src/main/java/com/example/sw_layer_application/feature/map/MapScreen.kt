package com.example.sw_layer_application.feature.map

import android.Manifest
import android.content.pm.PackageManager
import android.graphics.Color as AndroidColor
import android.graphics.PointF
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.content.ContextCompat
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.LifecycleEventObserver
import androidx.lifecycle.compose.LocalLifecycleOwner
import com.example.sw_layer_application.domain.model.Feature
import com.example.sw_layer_application.domain.model.GeometryType
import com.example.sw_layer_application.feature.map.component.FeatureAttributePanel
import com.example.sw_layer_application.feature.map.component.FeatureDetailsPanel
import com.example.sw_layer_application.feature.map.component.LayerPanel
import com.example.sw_layer_application.feature.map.component.ManageFieldsPanel
import org.json.JSONObject
import org.maplibre.android.camera.CameraPosition
import org.maplibre.android.geometry.LatLng
import org.maplibre.android.maps.MapLibreMap
import org.maplibre.android.maps.MapView
import org.maplibre.android.maps.Style
import org.maplibre.android.style.layers.CircleLayer
import org.maplibre.android.style.layers.FillLayer
import org.maplibre.android.style.layers.LineLayer
import org.maplibre.android.style.layers.PropertyFactory
import org.maplibre.android.style.sources.GeoJsonSource
import org.maplibre.geojson.FeatureCollection
import org.maplibre.geojson.LineString
import org.maplibre.geojson.Point
import org.maplibre.geojson.Polygon
import kotlin.math.roundToInt

private const val PENDING_POINT_SOURCE_ID = "pending-point-source"
private const val PENDING_POINT_LAYER_ID = "pending-point-layer"

private const val PENDING_LINE_SOURCE_ID = "pending-line-source"
private const val PENDING_LINE_LAYER_ID = "pending-line-layer"
private const val PENDING_LINE_VERTICES_SOURCE_ID = "pending-line-vertices-source"
private const val PENDING_LINE_VERTICES_LAYER_ID = "pending-line-vertices-layer"

private const val PENDING_POLYGON_SOURCE_ID = "pending-polygon-source"
private const val PENDING_POLYGON_LAYER_ID = "pending-polygon-layer"
private const val PENDING_POLYGON_OUTLINE_LAYER_ID = "pending-polygon-outline-layer"
private const val PENDING_POLYGON_VERTICES_SOURCE_ID = "pending-polygon-vertices-source"
private const val PENDING_POLYGON_VERTICES_LAYER_ID = "pending-polygon-vertices-layer"

private const val FEATURE_SOURCE_ID = "feature-source"
private const val FEATURE_LAYER_ID = "feature-layer"
private const val FEATURE_OUTLINE_LAYER_ID = "feature-outline-layer"

private const val EDITING_LINE_SOURCE_ID = "editing-line-source"
private const val EDITING_LINE_LAYER_ID = "editing-line-layer"

private const val EDITING_POLYGON_SOURCE_ID = "editing-polygon-source"
private const val EDITING_POLYGON_LAYER_ID = "editing-polygon-layer"
private const val EDITING_POLYGON_OUTLINE_LAYER_ID = "editing-polygon-outline-layer"

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapScreen(
    onCreateProjectClick: () -> Unit,
    viewModel: MapViewModel = hiltViewModel(),
    layerViewModel: LayerViewModel = hiltViewModel(),
    layerFieldViewModel: LayerFieldViewModel = hiltViewModel(),
    activeLayerViewModel: ActiveLayerViewModel = hiltViewModel(),
    featureViewModel: FeatureViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    val activeLayerUiState by activeLayerViewModel.uiState.collectAsState()
    val attributeUiState by featureViewModel.attributeUiState.collectAsState()
    val features by featureViewModel.features.collectAsState()
    val layerUiState by layerViewModel.uiState.collectAsState()
    val layerFieldUiState by layerFieldViewModel.uiState.collectAsState()

    var pointCaptureMode by remember { mutableStateOf(false) }
    var editingFeature by remember { mutableStateOf<Feature?>(null) }
    var lineCaptureMode by remember { mutableStateOf(false) }
    var pendingLinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    var polygonCaptureMode by remember { mutableStateOf(false) }
    var pendingPolygonPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    var lineGeometryEditMode by remember { mutableStateOf(false) }
    var editingLinePoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    var polygonGeometryEditMode by remember { mutableStateOf(false) }
    var editingPolygonPoints by remember { mutableStateOf<List<LatLng>>(emptyList()) }

    var pendingPoint by remember { mutableStateOf<LatLng?>(null) }

    var showAttributePanel by remember { mutableStateOf(false) }
    var showFeatureDetails by remember { mutableStateOf(false) }
    var showDeleteConfirmation by remember { mutableStateOf(false) }
    var showLayerPanel by remember { mutableStateOf(false) }
    var showFieldPanel by remember { mutableStateOf(false) }
    var newlyCreatedLayerId by remember { mutableStateOf<Long?>(null) }

    val currentPointCaptureMode by rememberUpdatedState(pointCaptureMode)
    val currentLineCaptureMode by rememberUpdatedState(lineCaptureMode)
    val currentPolygonCaptureMode by rememberUpdatedState(polygonCaptureMode)
    val currentActiveLayer by rememberUpdatedState(activeLayerUiState.activeLayer)

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    var mapLibreMap by remember { mutableStateOf<MapLibreMap?>(null) }
    var mapStyle by remember { mutableStateOf<Style?>(null) }

    // LOCATION PERMISSION LAUNCHER
    val locationPermissionLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.RequestMultiplePermissions()
    ) { permissions ->
        val fineLocationGranted = permissions[Manifest.permission.ACCESS_FINE_LOCATION] == true
        val coarseLocationGranted = permissions[Manifest.permission.ACCESS_COARSE_LOCATION] == true
        if (fineLocationGranted || coarseLocationGranted) {
            viewModel.loadCurrentLocation()
        }
    }

    // Remember MapView instance and attach lifecycle observer
    val mapView = remember(context) {
        MapView(context).apply {
            getMapAsync { map ->
                mapLibreMap = map
                map.setStyle("https://tiles.openfreemap.org/styles/liberty") { style ->
                    android.util.Log.d("MapStyle", "MAP STYLE LOADED")
                    mapStyle = style
                }

                map.cameraPosition = CameraPosition.Builder()
                    .target(LatLng(26.8467, 80.9462))
                    .zoom(11.0)
                    .build()

                map.addOnMapClickListener { point ->
                    // 1. ADD POINT MODE
                    if (currentPointCaptureMode) {
                        val activeLayer = currentActiveLayer
                        if (activeLayer == null || activeLayer.geometryType != GeometryType.POINT) {
                            return@addOnMapClickListener false
                        }
                        pendingPoint = point
                        pointCaptureMode = false
                        android.util.Log.d("PointCapture", "Point added: ${point.latitude}, ${point.longitude}")
                        showAttributePanel = layerFieldUiState.fields.isNotEmpty()
                        return@addOnMapClickListener true
                    }

                    // 2. ADD LINE MODE
                    if (currentLineCaptureMode) {
                        val activeLayer = currentActiveLayer
                        if (activeLayer == null || activeLayer.geometryType != GeometryType.LINE) {
                            return@addOnMapClickListener false
                        }
                        pendingLinePoints = pendingLinePoints + point
                        android.util.Log.d(
                            "LineCapture",
                            "Line point added: ${point.latitude}, ${point.longitude}"
                        )
                        return@addOnMapClickListener true
                    }

                    // 3. ADD POLYGON MODE
                    if (currentPolygonCaptureMode) {
                        val activeLayer = currentActiveLayer
                        if (activeLayer == null || activeLayer.geometryType != GeometryType.POLYGON) {
                            return@addOnMapClickListener false
                        }
                        pendingPolygonPoints = pendingPolygonPoints + point
                        android.util.Log.d(
                            "PolygonCapture",
                            "Polygon point added: ${point.latitude}, ${point.longitude}"
                        )
                        return@addOnMapClickListener true
                    }

                    // 4. CLICK EXISTING FEATURE
                    val screenPoint = map.projection.toScreenLocation(point)
                    val renderedFeatures = map.queryRenderedFeatures(screenPoint, FEATURE_LAYER_ID)
                    val clickedFeature = renderedFeatures.firstOrNull()

                    if (clickedFeature != null) {
                        val featureId = clickedFeature.getNumberProperty("featureId")?.toLong()
                        if (featureId != null) {
                            android.util.Log.d("FeatureClick", "Clicked feature ID = $featureId")
                            featureViewModel.selectFeature(featureId)
                            showFeatureDetails = true
                        }
                        return@addOnMapClickListener true
                    }

                    false
                }
            }
        }
    }

    DisposableEffect(lifecycleOwner, mapView) {
        val observer = LifecycleEventObserver { _, event ->
            when (event) {
                Lifecycle.Event.ON_CREATE -> mapView.onCreate(null)
                Lifecycle.Event.ON_START -> mapView.onStart()
                Lifecycle.Event.ON_RESUME -> mapView.onResume()
                Lifecycle.Event.ON_PAUSE -> mapView.onPause()
                Lifecycle.Event.ON_STOP -> mapView.onStop()
                Lifecycle.Event.ON_DESTROY -> mapView.onDestroy()
                else -> {}
            }
        }
        lifecycleOwner.lifecycle.addObserver(observer)
        onDispose {
            lifecycleOwner.lifecycle.removeObserver(observer)
            mapView.onDestroy()
        }
    }

    LaunchedEffect(activeLayerUiState.activeLayer?.id) {
        pendingPoint = null
        pointCaptureMode = false
        lineCaptureMode = false
        pendingLinePoints = emptyList()
        polygonCaptureMode = false
        pendingPolygonPoints = emptyList()
        editingLinePoints = emptyList()
        lineGeometryEditMode = false
        editingPolygonPoints = emptyList()
        polygonGeometryEditMode = false
        showAttributePanel = false
        featureViewModel.clearAttributeValues()

        val layer = activeLayerUiState.activeLayer
        if (layer != null) {
            layerFieldViewModel.openLayer(layer)
            featureViewModel.loadFeaturesForLayer(layer.id)
        } else {
            layerFieldViewModel.closeLayer()
            featureViewModel.clearFeatures()
        }
    }

    LaunchedEffect(uiState.latitude, uiState.longitude, mapLibreMap) {
        val latitude = uiState.latitude
        val longitude = uiState.longitude
        val map = mapLibreMap

        if (latitude != null && longitude != null && map != null) {
            map.cameraPosition = CameraPosition.Builder()
                .target(LatLng(latitude, longitude))
                .zoom(16.0)
                .build()
        }
    }

    LaunchedEffect(uiState.activeProject?.id) {
        val projectId = uiState.activeProject?.id
        activeLayerViewModel.clearActiveLayer()
        if (projectId != null) {
            layerViewModel.loadLayers(projectId = projectId)
        }
    }

    Box(modifier = Modifier.fillMaxSize()) {
        AndroidView(
            factory = { mapView },
            modifier = Modifier.fillMaxSize()
        )

        // LINE GEOMETRY EDIT VERTICES OVERLAY
        if (showAttributePanel && lineGeometryEditMode && editingLinePoints.isNotEmpty() && mapLibreMap != null) {
            editingLinePoints.forEachIndexed { index, point ->
                val screenPoint = mapLibreMap!!.projection.toScreenLocation(point)

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                screenPoint.x.roundToInt() - 16,
                                screenPoint.y.roundToInt() - 16
                            )
                        }
                        .size(32.dp)
                        .background(color = Color.Blue, shape = CircleShape)
                        .border(width = 2.dp, color = Color.White, shape = CircleShape)
                        .pointerInput(index) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val map = mapLibreMap ?: return@detectDragGestures
                                val currentPoint = editingLinePoints.getOrNull(index) ?: return@detectDragGestures
                                val currentScreenPoint = map.projection.toScreenLocation(currentPoint)
                                val newScreenPoint = PointF(
                                    currentScreenPoint.x + dragAmount.x,
                                    currentScreenPoint.y + dragAmount.y
                                )
                                val newLatLng = map.projection.fromScreenLocation(newScreenPoint)
                                if (newLatLng != null) {
                                    editingLinePoints = editingLinePoints.toMutableList().apply {
                                        this[index] = newLatLng
                                    }
                                }
                            }
                        }
                )
            }
        }

        // POLYGON GEOMETRY EDIT VERTICES OVERLAY
        if (showAttributePanel && polygonGeometryEditMode && editingPolygonPoints.isNotEmpty() && mapLibreMap != null) {
            editingPolygonPoints.forEachIndexed { index, point ->
                val screenPoint = mapLibreMap!!.projection.toScreenLocation(point)

                Box(
                    modifier = Modifier
                        .offset {
                            IntOffset(
                                screenPoint.x.roundToInt() - 16,
                                screenPoint.y.roundToInt() - 16
                            )
                        }
                        .size(32.dp)
                        .background(color = Color.Blue, shape = CircleShape)
                        .border(width = 2.dp, color = Color.White, shape = CircleShape)
                        .pointerInput(index) {
                            detectDragGestures { change, dragAmount ->
                                change.consume()
                                val map = mapLibreMap ?: return@detectDragGestures
                                val currentPoint = editingPolygonPoints.getOrNull(index) ?: return@detectDragGestures
                                val currentScreenPoint = map.projection.toScreenLocation(currentPoint)
                                val newScreenPoint = PointF(
                                    currentScreenPoint.x + dragAmount.x,
                                    currentScreenPoint.y + dragAmount.y
                                )
                                val newLatLng = map.projection.fromScreenLocation(newScreenPoint)
                                if (newLatLng != null) {
                                    editingPolygonPoints = editingPolygonPoints.toMutableList().apply {
                                        this[index] = newLatLng
                                    }
                                }
                            }
                        }
                )
            }
        }

        // LINE CAPTURE CONTROL BAR
        if (lineCaptureMode && pendingLinePoints.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        lineCaptureMode = false
                        pendingLinePoints = emptyList()
                    }
                ) {
                    Text("Cancel")
                }

                Button(
                    enabled = pendingLinePoints.size >= 2,
                    onClick = {
                        lineCaptureMode = false
                        showAttributePanel = layerFieldUiState.fields.isNotEmpty()
                    }
                ) {
                    Text("Finish Line (${pendingLinePoints.size} pts)")
                }
            }
        }

        // POLYGON CAPTURE CONTROL BAR
        if (polygonCaptureMode && pendingPolygonPoints.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 32.dp),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                OutlinedButton(
                    onClick = {
                        polygonCaptureMode = false
                        pendingPolygonPoints = emptyList()
                    }
                ) {
                    Text("Cancel")
                }

                Button(
                    enabled = pendingPolygonPoints.size >= 3,
                    onClick = {
                        polygonCaptureMode = false
                        showAttributePanel = layerFieldUiState.fields.isNotEmpty()
                    }
                ) {
                    Text("Finish Polygon (${pendingPolygonPoints.size} pts)")
                }
            }
        }

        // ACTIVE PROJECT HEADER
        Column(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .padding(top = 20.dp)
                .background(Color.White)
                .padding(horizontal = 16.dp, vertical = 8.dp)
        ) {
            if (uiState.isLoading) {
                Text(text = "Loading project...")
            } else {
                val activeProject = uiState.activeProject
                if (activeProject != null) {
                    Text(text = "Active Project")
                    Text(text = activeProject.name)
                } else {
                    Text(text = "No Project Created")
                }
            }
        }

        activeLayerUiState.activeLayer?.let { layer ->
            Surface(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 90.dp),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 4.dp
            ) {
                Text(
                    text = "Active Layer: ${layer.name}",
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 10.dp)
                )
            }
        }

        // MY LOCATION BUTTON
        Button(
            onClick = {
                val fineLocationGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_FINE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                val coarseLocationGranted = ContextCompat.checkSelfPermission(
                    context,
                    Manifest.permission.ACCESS_COARSE_LOCATION
                ) == PackageManager.PERMISSION_GRANTED

                if (fineLocationGranted || coarseLocationGranted) {
                    viewModel.loadCurrentLocation()
                } else {
                    locationPermissionLauncher.launch(
                        arrayOf(
                            Manifest.permission.ACCESS_FINE_LOCATION,
                            Manifest.permission.ACCESS_COARSE_LOCATION
                        )
                    )
                }
            },
            modifier = Modifier
                .align(Alignment.CenterEnd)
                .padding(end = 16.dp)
        ) {
            Text(text = "My Location")
        }

        // LAYERS BUTTON
        Button(
            onClick = { showLayerPanel = true },
            modifier = Modifier
                .align(Alignment.CenterStart)
                .padding(start = 16.dp)
        ) {
            Text(text = "Layers")
        }

        // BOTTOM ACTION BUTTONS
        Column(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .padding(bottom = 30.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            if (activeLayerUiState.activeLayer?.geometryType == GeometryType.POINT &&
                pendingPoint == null &&
                !pointCaptureMode &&
                !showAttributePanel
            ) {
                Button(
                    onClick = {
                        pointCaptureMode = true
                        editingLinePoints = emptyList()
                        lineGeometryEditMode = false
                        editingPolygonPoints = emptyList()
                        polygonGeometryEditMode = false
                        editingFeature = null
                    }
                ) {
                    Text("Add Point")
                }
            }

            if (activeLayerUiState.activeLayer?.geometryType == GeometryType.LINE &&
                !lineCaptureMode &&
                pendingLinePoints.isEmpty() &&
                !showAttributePanel
            ) {
                Button(
                    onClick = {
                        android.util.Log.d("LineCapture", "ADD LINE BUTTON CLICKED")
                        lineCaptureMode = true
                        pendingLinePoints = emptyList()
                        editingLinePoints = emptyList()
                        lineGeometryEditMode = false
                        editingPolygonPoints = emptyList()
                        polygonGeometryEditMode = false
                        editingFeature = null
                    }
                ) {
                    Text("Add Line")
                }
            }

            if (activeLayerUiState.activeLayer?.geometryType == GeometryType.POLYGON &&
                !polygonCaptureMode &&
                pendingPolygonPoints.isEmpty() &&
                !showAttributePanel
            ) {
                Button(
                    onClick = {
                        android.util.Log.d("PolygonCapture", "ADD POLYGON BUTTON CLICKED")
                        polygonCaptureMode = true
                        pendingPolygonPoints = emptyList()
                        editingLinePoints = emptyList()
                        lineGeometryEditMode = false
                        editingPolygonPoints = emptyList()
                        polygonGeometryEditMode = false
                        editingFeature = null
                    }
                ) {
                    Text("Add Polygon")
                }
            }

            if (pendingPoint == null &&
                !pointCaptureMode &&
                !lineCaptureMode &&
                pendingLinePoints.isEmpty() &&
                !polygonCaptureMode &&
                pendingPolygonPoints.isEmpty() &&
                !showAttributePanel
            ) {
                Button(
                    onClick = onCreateProjectClick
                ) {
                    Text("Create Project")
                }
            }
        }

        // LOCATION ERROR
        uiState.locationError?.let { error ->
            Text(
                text = error,
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(bottom = 100.dp)
                    .background(Color.White)
                    .padding(8.dp)
            )
        }

        // PENDING POINT DISPLAY CARD
        pendingPoint?.let { point ->
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .padding(16.dp),
                shape = MaterialTheme.shapes.medium,
                tonalElevation = 6.dp
            ) {
                Text(
                    text = String.format("Point: %.6f, %.6f", point.latitude, point.longitude),
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                )
            }
        }

        // NON-MODAL ATTRIBUTE / GEOMETRY EDIT PANEL (INSET PADDING PUSHES PANEL ABOVE SOFT KEYBOARD)
        if (showAttributePanel && activeLayerUiState.activeLayer != null) {
            Surface(
                modifier = Modifier
                    .align(Alignment.BottomCenter)
                    .fillMaxWidth()
                    .navigationBarsPadding()
                    .imePadding(),
                shape = RoundedCornerShape(topStart = 24.dp, topEnd = 24.dp),
                tonalElevation = 8.dp,
                shadowElevation = 8.dp
            ) {
                Column(
                    modifier = Modifier
                        .padding(16.dp)
                        .verticalScroll(rememberScrollState())
                ) {
                    FeatureAttributePanel(
                        fields = layerFieldUiState.fields,
                        values = attributeUiState.values,
                        onValueChange = { fieldName, value ->
                            featureViewModel.onAttributeValueChange(
                                fieldName = fieldName,
                                value = value
                            )
                        }
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(top = 16.dp),
                        horizontalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        OutlinedButton(
                            onClick = {
                                showAttributePanel = false
                                pendingPoint = null
                                editingFeature = null
                                pendingPolygonPoints = emptyList()
                                polygonCaptureMode = false
                                editingLinePoints = emptyList()
                                lineGeometryEditMode = false
                                editingPolygonPoints = emptyList()
                                polygonGeometryEditMode = false
                                featureViewModel.clearAttributeValues()
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text("Cancel")
                        }

                        Button(
                            onClick = {
                                val activeLayer = activeLayerUiState.activeLayer ?: return@Button
                                val attributesJson = featureViewModel.attributesToJson()
                                val existingFeature = editingFeature

                                if (existingFeature != null) {
                                    if (existingFeature.geometryType == GeometryType.LINE && editingLinePoints.size >= 2) {
                                        val coordinates = editingLinePoints.joinToString(
                                            prefix = "[",
                                            postfix = "]"
                                        ) { point -> "[${point.longitude},${point.latitude}]" }

                                        val updatedGeometryJson = """
                                            {
                                                "type": "LineString",
                                                "coordinates": $coordinates
                                            }
                                        """.trimIndent()

                                        val updatedFeature = existingFeature.copy(
                                            geometryJson = updatedGeometryJson,
                                            attributesJson = attributesJson
                                        )

                                        featureViewModel.updateFeature(
                                            feature = updatedFeature,
                                            onSuccess = {
                                                android.util.Log.d("FeatureUpdate", "LINE GEOMETRY + ATTRIBUTES UPDATED")
                                                showAttributePanel = false
                                                editingFeature = null
                                                editingLinePoints = emptyList()
                                                lineGeometryEditMode = false
                                                editingPolygonPoints = emptyList()
                                                polygonGeometryEditMode = false
                                                featureViewModel.clearAttributeValues()
                                                featureViewModel.loadFeaturesForLayer(activeLayer.id)
                                            },
                                            onError = { error ->
                                                android.util.Log.e("FeatureUpdate", "LINE UPDATE FAILED", error)
                                            }
                                        )
                                    } else if (existingFeature.geometryType == GeometryType.POLYGON && editingPolygonPoints.size >= 3) {
                                        val closedPoints = editingPolygonPoints + editingPolygonPoints.first()
                                        val coordinates = closedPoints.joinToString(
                                            prefix = "[[",
                                            postfix = "]]"
                                        ) { point -> "[${point.longitude},${point.latitude}]" }

                                        val updatedGeometryJson = """
                                            {
                                                "type": "Polygon",
                                                "coordinates": $coordinates
                                            }
                                        """.trimIndent()

                                        val updatedFeature = existingFeature.copy(
                                            geometryJson = updatedGeometryJson,
                                            attributesJson = attributesJson
                                        )

                                        featureViewModel.updateFeature(
                                            feature = updatedFeature,
                                            onSuccess = {
                                                android.util.Log.d("FeatureUpdate", "POLYGON GEOMETRY + ATTRIBUTES UPDATED")
                                                showAttributePanel = false
                                                editingFeature = null
                                                editingLinePoints = emptyList()
                                                lineGeometryEditMode = false
                                                editingPolygonPoints = emptyList()
                                                polygonGeometryEditMode = false
                                                featureViewModel.clearAttributeValues()
                                                featureViewModel.loadFeaturesForLayer(activeLayer.id)
                                            },
                                            onError = { error ->
                                                android.util.Log.e("FeatureUpdate", "POLYGON UPDATE FAILED", error)
                                            }
                                        )
                                    } else {
                                        featureViewModel.updateFeatureAttributes(
                                            featureId = existingFeature.id,
                                            attributesJson = attributesJson,
                                            onSuccess = {
                                                android.util.Log.d("FeatureUpdate", "POINT ATTRIBUTES UPDATED")
                                                showAttributePanel = false
                                                editingFeature = null
                                                editingLinePoints = emptyList()
                                                lineGeometryEditMode = false
                                                editingPolygonPoints = emptyList()
                                                polygonGeometryEditMode = false
                                                featureViewModel.clearAttributeValues()
                                                featureViewModel.loadFeaturesForLayer(activeLayer.id)
                                            },
                                            onError = { error ->
                                                android.util.Log.e("FeatureUpdate", "POINT UPDATE FAILED", error)
                                            }
                                        )
                                    }
                                    return@Button
                                }

                                // CREATE NEW POLYGON
                                if (activeLayer.geometryType == GeometryType.POLYGON) {
                                    if (pendingPolygonPoints.size < 3) {
                                        android.util.Log.e("FeatureCapture", "At least 3 points are required for Polygon")
                                        return@Button
                                    }

                                    val closedPoints = pendingPolygonPoints + pendingPolygonPoints.first()
                                    val coordinates = closedPoints.joinToString(
                                        prefix = "[[",
                                        postfix = "]]"
                                    ) { p -> "[${p.longitude},${p.latitude}]" }

                                    val geometryJson = """
                                        {
                                            "type": "Polygon",
                                            "coordinates": $coordinates
                                        }
                                    """.trimIndent()

                                    val firstPoint = pendingPolygonPoints.first()
                                    val feature = Feature(
                                        layerId = activeLayer.id,
                                        geometryType = GeometryType.POLYGON,
                                        latitude = firstPoint.latitude,
                                        longitude = firstPoint.longitude,
                                        geometryJson = geometryJson,
                                        attributesJson = attributesJson
                                    )

                                    featureViewModel.createFeature(
                                        feature = feature,
                                        onSuccess = {
                                            android.util.Log.d("FeatureCapture", "POLYGON SAVED SUCCESSFULLY")
                                            showAttributePanel = false
                                            pendingPolygonPoints = emptyList()
                                            polygonCaptureMode = false
                                            pendingPoint = null
                                            editingFeature = null
                                            editingLinePoints = emptyList()
                                            lineGeometryEditMode = false
                                            editingPolygonPoints = emptyList()
                                            polygonGeometryEditMode = false
                                            featureViewModel.clearAttributeValues()
                                            featureViewModel.loadFeaturesForLayer(activeLayer.id)
                                        },
                                        onError = { error ->
                                            android.util.Log.e("FeatureCapture", "POLYGON SAVE FAILED", error)
                                        }
                                    )
                                    return@Button
                                }

                                // CREATE NEW LINE
                                if (activeLayer.geometryType == GeometryType.LINE) {
                                    if (pendingLinePoints.size < 2) {
                                        android.util.Log.e("FeatureCapture", "At least 2 points are required")
                                        return@Button
                                    }

                                    val coordinates = pendingLinePoints.joinToString(
                                        prefix = "[",
                                        postfix = "]"
                                    ) { point -> "[${point.longitude},${point.latitude}]" }

                                    val geometryJson = """
                                        {
                                            "type": "LineString",
                                            "coordinates": $coordinates
                                        }
                                    """.trimIndent()

                                    val firstPoint = pendingLinePoints.first()
                                    val feature = Feature(
                                        layerId = activeLayer.id,
                                        geometryType = GeometryType.LINE,
                                        latitude = firstPoint.latitude,
                                        longitude = firstPoint.longitude,
                                        geometryJson = geometryJson,
                                        attributesJson = attributesJson
                                    )

                                    featureViewModel.createFeature(
                                        feature = feature,
                                        onSuccess = {
                                            android.util.Log.d("FeatureCapture", "LINE SAVED SUCCESSFULLY")
                                            showAttributePanel = false
                                            pendingLinePoints = emptyList()
                                            lineCaptureMode = false
                                            pendingPoint = null
                                            editingFeature = null
                                            editingLinePoints = emptyList()
                                            lineGeometryEditMode = false
                                            editingPolygonPoints = emptyList()
                                            polygonGeometryEditMode = false
                                            featureViewModel.clearAttributeValues()
                                            featureViewModel.loadFeaturesForLayer(activeLayer.id)
                                        },
                                        onError = { error ->
                                            android.util.Log.e("FeatureCapture", "LINE SAVE FAILED", error)
                                        }
                                    )
                                    return@Button
                                }

                                // CREATE NEW POINT
                                val point = pendingPoint ?: return@Button
                                val feature = Feature(
                                    layerId = activeLayer.id,
                                    geometryType = GeometryType.POINT,
                                    latitude = point.latitude,
                                    longitude = point.longitude,
                                    geometryJson = null,
                                    attributesJson = attributesJson
                                )

                                featureViewModel.createFeature(
                                    feature = feature,
                                    onSuccess = {
                                        android.util.Log.d("FeatureCapture", "POINT SAVED SUCCESSFULLY")
                                        showAttributePanel = false
                                        pendingPoint = null
                                        editingFeature = null
                                        editingLinePoints = emptyList()
                                        lineGeometryEditMode = false
                                        editingPolygonPoints = emptyList()
                                        polygonGeometryEditMode = false
                                        featureViewModel.clearAttributeValues()
                                        featureViewModel.loadFeaturesForLayer(activeLayer.id)
                                    },
                                    onError = { error ->
                                        android.util.Log.e("FeatureCapture", "POINT SAVE FAILED", error)
                                    }
                                )
                            },
                            modifier = Modifier.weight(1f)
                        ) {
                            Text(
                                if (editingFeature != null) {
                                    "Save Changes"
                                } else if (activeLayerUiState.activeLayer?.geometryType == GeometryType.LINE) {
                                    "Save Line"
                                } else if (activeLayerUiState.activeLayer?.geometryType == GeometryType.POLYGON) {
                                    "Save Polygon"
                                } else {
                                    "Save Point"
                                }
                            )
                        }
                    }
                }
            }
        }
    }

    // SHOW LAYERS PANEL
    if (showLayerPanel) {
        ModalBottomSheet(
            onDismissRequest = { showLayerPanel = false }
        ) {
            val activeProject = uiState.activeProject
            if (activeProject != null) {
                LayerPanel(
                    uiState = layerUiState,
                    onLayerNameChange = layerViewModel::onLayerNameChange,
                    onGeometryTypeChange = layerViewModel::onGeometryTypeChange,
                    onCreateLayer = {
                        layerViewModel.createLayer(
                            projectId = activeProject.id,
                            onSuccess = { layerId -> newlyCreatedLayerId = layerId }
                        )
                    },
                    onLayerClick = { layer ->
                        activeLayerViewModel.setActiveLayer(layer)
                        layerFieldViewModel.openLayer(layer)
                        showLayerPanel = false
                        showFieldPanel = true
                    }
                )
            } else {
                Text(
                    text = "Create a project before adding layers",
                    modifier = Modifier.padding(20.dp)
                )
            }
        }
    }

    LaunchedEffect(newlyCreatedLayerId, layerUiState.layers) {
        val layerId = newlyCreatedLayerId ?: return@LaunchedEffect
        val newLayer = layerUiState.layers.firstOrNull { it.id == layerId } ?: return@LaunchedEffect

        activeLayerViewModel.setActiveLayer(newLayer)
        showLayerPanel = false
        showFieldPanel = true
        newlyCreatedLayerId = null
    }

    // SHOW FIELD PANEL
    if (showFieldPanel) {
        ModalBottomSheet(
            onDismissRequest = {
                showFieldPanel = false
                pendingPoint = null
                editingFeature = null
                pendingPolygonPoints = emptyList()
                polygonCaptureMode = false
                editingLinePoints = emptyList()
                lineGeometryEditMode = false
                editingPolygonPoints = emptyList()
                polygonGeometryEditMode = false
                featureViewModel.clearAttributeValues()
            }
        ) {
            ManageFieldsPanel(
                uiState = layerFieldUiState,
                onFieldNameChange = layerFieldViewModel::onFieldNameChange,
                onFieldTypeChange = layerFieldViewModel::onFieldTypeChange,
                onRequiredChange = layerFieldViewModel::onRequiredChange,
                onCreateField = layerFieldViewModel::createField,
                onDeleteField = layerFieldViewModel::deleteField
            )
        }
    }

    // SHOW FEATURE DETAILS
    if (showFeatureDetails) {
        val selectedFeature by featureViewModel.selectedFeature.collectAsState()
        selectedFeature?.let { feature ->
            ModalBottomSheet(
                onDismissRequest = {
                    showFeatureDetails = false
                    editingLinePoints = emptyList()
                    lineGeometryEditMode = false
                    editingPolygonPoints = emptyList()
                    polygonGeometryEditMode = false
                    featureViewModel.clearSelectedFeature()
                }
            ) {
                FeatureDetailsPanel(
                    feature = feature,
                    onClose = {
                        showFeatureDetails = false
                        editingLinePoints = emptyList()
                        lineGeometryEditMode = false
                        editingPolygonPoints = emptyList()
                        polygonGeometryEditMode = false
                        featureViewModel.clearSelectedFeature()
                    },
                    onEdit = {
                        editingFeature = feature
                        featureViewModel.loadAttributeValues(feature.attributesJson)

                        if (feature.geometryType == GeometryType.LINE) {
                            editingLinePoints = parseLineGeometry(feature.geometryJson)
                            lineGeometryEditMode = editingLinePoints.size >= 2
                            editingPolygonPoints = emptyList()
                            polygonGeometryEditMode = false
                        } else if (feature.geometryType == GeometryType.POLYGON) {
                            editingPolygonPoints = parsePolygonGeometry(feature.geometryJson)
                            polygonGeometryEditMode = editingPolygonPoints.size >= 3
                            editingLinePoints = emptyList()
                            lineGeometryEditMode = false
                        } else {
                            editingLinePoints = emptyList()
                            lineGeometryEditMode = false
                            editingPolygonPoints = emptyList()
                            polygonGeometryEditMode = false
                        }

                        showFeatureDetails = false
                        showAttributePanel = true
                    },
                    onDelete = {
                        showDeleteConfirmation = true
                    }
                )
            }
        }
    }

    // DELETE FEATURE CONFIRMATION
    if (showDeleteConfirmation) {
        AlertDialog(
            onDismissRequest = { showDeleteConfirmation = false },
            title = { Text("Delete Feature") },
            text = { Text("Are you sure you want to delete this feature?") },
            confirmButton = {
                TextButton(
                    onClick = {
                        val feature = featureViewModel.selectedFeature.value
                        if (feature != null) {
                            featureViewModel.deleteFeature(
                                featureId = feature.id,
                                onSuccess = {
                                    showDeleteConfirmation = false
                                    showFeatureDetails = false
                                    editingLinePoints = emptyList()
                                    lineGeometryEditMode = false
                                    editingPolygonPoints = emptyList()
                                    polygonGeometryEditMode = false
                                    featureViewModel.clearSelectedFeature()
                                    featureViewModel.loadFeaturesForLayer(feature.layerId)
                                },
                                onError = { error ->
                                    android.util.Log.e("FeatureDelete", "DELETE FAILED", error)
                                }
                            )
                        }
                    }
                ) {
                    Text("Delete")
                }
            },
            dismissButton = {
                TextButton(
                    onClick = { showDeleteConfirmation = false }
                ) {
                    Text("Cancel")
                }
            }
        )
    }

    // MAP RENDERING LAUNCHED EFFECTS
    LaunchedEffect(pendingPoint, mapStyle) {
        val style = mapStyle ?: return@LaunchedEffect
        addPendingPointLayer(style = style, point = pendingPoint)
    }

    LaunchedEffect(pendingLinePoints, mapStyle) {
        val style = mapStyle ?: return@LaunchedEffect
        android.util.Log.d("LineRender", "Rendering pending line. Points = ${pendingLinePoints.size}")
        addPendingLineLayer(style = style, points = pendingLinePoints)
    }

    LaunchedEffect(pendingPolygonPoints, mapStyle) {
        val style = mapStyle ?: return@LaunchedEffect
        android.util.Log.d("PolygonRender", "Rendering pending polygon. Points = ${pendingPolygonPoints.size}")
        addPendingPolygonLayer(style = style, points = pendingPolygonPoints)
    }

    LaunchedEffect(editingLinePoints, lineGeometryEditMode, mapStyle) {
        val style = mapStyle ?: return@LaunchedEffect
        if (lineGeometryEditMode) {
            addEditingLineLayer(style = style, points = editingLinePoints)
        } else {
            addEditingLineLayer(style = style, points = emptyList())
        }
    }

    LaunchedEffect(editingPolygonPoints, polygonGeometryEditMode, mapStyle) {
        val style = mapStyle ?: return@LaunchedEffect
        if (polygonGeometryEditMode) {
            addEditingPolygonLayer(style = style, points = editingPolygonPoints)
        } else {
            addEditingPolygonLayer(style = style, points = emptyList())
        }
    }

    LaunchedEffect(features, mapStyle) {
        val style = mapStyle ?: return@LaunchedEffect
        renderFeatures(style = style, features = features)
    }
}

private fun addPendingPointLayer(
    style: Style,
    point: LatLng?
) {
    val featureCollection = if (point != null) {
        val geoJsonPoint = Point.fromLngLat(point.longitude, point.latitude)
        FeatureCollection.fromFeatures(arrayOf(org.maplibre.geojson.Feature.fromGeometry(geoJsonPoint)))
    } else {
        FeatureCollection.fromFeatures(emptyArray())
    }

    val source = style.getSourceAs<GeoJsonSource>(PENDING_POINT_SOURCE_ID)
    if (source != null) {
        source.setGeoJson(featureCollection)
    } else if (point != null) {
        val newSource = GeoJsonSource(PENDING_POINT_SOURCE_ID, featureCollection)
        style.addSource(newSource)

        val circleLayer = CircleLayer(PENDING_POINT_LAYER_ID, PENDING_POINT_SOURCE_ID)
        circleLayer.setProperties(
            PropertyFactory.circleRadius(8f),
            PropertyFactory.circleColor(AndroidColor.RED),
            PropertyFactory.circleStrokeColor(AndroidColor.WHITE),
            PropertyFactory.circleStrokeWidth(2f)
        )
        style.addLayer(circleLayer)
    }
}

private fun addPendingLineLayer(
    style: Style,
    points: List<LatLng>
) {
    android.util.Log.d("LineRender", "addPendingLineLayer() points=${points.size}")

    // 1. Render Vertices (Points) so even the very first point tapped is immediately visible
    val vertexFeatures = points.map { point ->
        org.maplibre.geojson.Feature.fromGeometry(Point.fromLngLat(point.longitude, point.latitude))
    }
    val vertexCollection = FeatureCollection.fromFeatures(vertexFeatures.toTypedArray())

    val vertexSource = style.getSourceAs<GeoJsonSource>(PENDING_LINE_VERTICES_SOURCE_ID)
    if (vertexSource != null) {
        vertexSource.setGeoJson(vertexCollection)
    } else if (points.isNotEmpty()) {
        val newVertexSource = GeoJsonSource(PENDING_LINE_VERTICES_SOURCE_ID, vertexCollection)
        style.addSource(newVertexSource)

        val circleLayer = CircleLayer(PENDING_LINE_VERTICES_LAYER_ID, PENDING_LINE_VERTICES_SOURCE_ID)
        circleLayer.setProperties(
            PropertyFactory.circleRadius(8f),
            PropertyFactory.circleColor(AndroidColor.RED),
            PropertyFactory.circleStrokeColor(AndroidColor.WHITE),
            PropertyFactory.circleStrokeWidth(2f)
        )
        style.addLayer(circleLayer)
    }

    // 2. Render Line Segments (requires at least 2 points)
    val lineCollection = if (points.size >= 2) {
        val coordinates = points.map { Point.fromLngLat(it.longitude, it.latitude) }
        val lineString = LineString.fromLngLats(coordinates)
        FeatureCollection.fromFeatures(arrayOf(org.maplibre.geojson.Feature.fromGeometry(lineString)))
    } else {
        FeatureCollection.fromFeatures(emptyArray())
    }

    val lineSource = style.getSourceAs<GeoJsonSource>(PENDING_LINE_SOURCE_ID)
    if (lineSource != null) {
        lineSource.setGeoJson(lineCollection)
    } else if (points.size >= 2) {
        val newLineSource = GeoJsonSource(PENDING_LINE_SOURCE_ID, lineCollection)
        style.addSource(newLineSource)

        val lineLayer = LineLayer(PENDING_LINE_LAYER_ID, PENDING_LINE_SOURCE_ID)
        lineLayer.setProperties(
            PropertyFactory.lineColor(AndroidColor.RED),
            PropertyFactory.lineWidth(6f),
            PropertyFactory.lineOpacity(1.0f)
        )
        style.addLayer(lineLayer)
    }

    android.util.Log.d("LineRender", "LINE DRAWN/UPDATED SUCCESSFULLY")
}

private fun addPendingPolygonLayer(
    style: Style,
    points: List<LatLng>
) {
    android.util.Log.d("PolygonRender", "addPendingPolygonLayer() points=${points.size}")

    // 1. Render Vertices (Points) so every tapped point is immediately visible
    val vertexFeatures = points.map { point ->
        org.maplibre.geojson.Feature.fromGeometry(Point.fromLngLat(point.longitude, point.latitude))
    }
    val vertexCollection = FeatureCollection.fromFeatures(vertexFeatures.toTypedArray())

    val vertexSource = style.getSourceAs<GeoJsonSource>(PENDING_POLYGON_VERTICES_SOURCE_ID)
    if (vertexSource != null) {
        vertexSource.setGeoJson(vertexCollection)
    } else if (points.isNotEmpty()) {
        val newVertexSource = GeoJsonSource(PENDING_POLYGON_VERTICES_SOURCE_ID, vertexCollection)
        style.addSource(newVertexSource)

        val circleLayer = CircleLayer(PENDING_POLYGON_VERTICES_LAYER_ID, PENDING_POLYGON_VERTICES_SOURCE_ID)
        circleLayer.setProperties(
            PropertyFactory.circleRadius(8f),
            PropertyFactory.circleColor(AndroidColor.RED),
            PropertyFactory.circleStrokeColor(AndroidColor.WHITE),
            PropertyFactory.circleStrokeWidth(2f)
        )
        style.addLayer(circleLayer)
    }

    // 2. Render Polygon Fill & Outline (requires at least 3 points)
    val polygonCollection = if (points.size >= 3) {
        val closedPoints = points + points.first()
        val coordinates = closedPoints.map { Point.fromLngLat(it.longitude, it.latitude) }
        val polygon = Polygon.fromLngLats(listOf(coordinates))
        FeatureCollection.fromFeatures(arrayOf(org.maplibre.geojson.Feature.fromGeometry(polygon)))
    } else {
        FeatureCollection.fromFeatures(emptyArray())
    }

    val polygonSource = style.getSourceAs<GeoJsonSource>(PENDING_POLYGON_SOURCE_ID)
    if (polygonSource != null) {
        polygonSource.setGeoJson(polygonCollection)
    } else if (points.size >= 3) {
        val newPolygonSource = GeoJsonSource(PENDING_POLYGON_SOURCE_ID, polygonCollection)
        style.addSource(newPolygonSource)

        // Fill Layer
        val fillLayer = FillLayer(
            PENDING_POLYGON_LAYER_ID,
            PENDING_POLYGON_SOURCE_ID
        )
        fillLayer.setProperties(
            PropertyFactory.fillColor(AndroidColor.RED),
            PropertyFactory.fillOpacity(0.4f)
        )
        style.addLayer(fillLayer)

        // Outline / Border Line Layer
        val outlineLayer = LineLayer(
            PENDING_POLYGON_OUTLINE_LAYER_ID,
            PENDING_POLYGON_SOURCE_ID
        )
        outlineLayer.setProperties(
            PropertyFactory.lineColor(AndroidColor.RED),
            PropertyFactory.lineWidth(4f),
            PropertyFactory.lineOpacity(1.0f)
        )
        style.addLayer(outlineLayer)
    }

    android.util.Log.d("PolygonRender", "POLYGON DRAWN/UPDATED SUCCESSFULLY")
}

private fun addEditingLineLayer(
    style: Style,
    points: List<LatLng>
) {
    val featureCollection = if (points.size >= 2) {
        val coordinates = points.map { Point.fromLngLat(it.longitude, it.latitude) }
        val lineString = LineString.fromLngLats(coordinates)
        FeatureCollection.fromFeatures(arrayOf(org.maplibre.geojson.Feature.fromGeometry(lineString)))
    } else {
        FeatureCollection.fromFeatures(emptyArray())
    }

    val source = style.getSourceAs<GeoJsonSource>(EDITING_LINE_SOURCE_ID)
    if (source != null) {
        source.setGeoJson(featureCollection)
    } else if (points.size >= 2) {
        val newSource = GeoJsonSource(EDITING_LINE_SOURCE_ID, featureCollection)
        style.addSource(newSource)

        val lineLayer = LineLayer(EDITING_LINE_LAYER_ID, EDITING_LINE_SOURCE_ID)
        lineLayer.setProperties(
            PropertyFactory.lineColor(AndroidColor.BLUE),
            PropertyFactory.lineWidth(6f),
            PropertyFactory.lineOpacity(1.0f)
        )
        style.addLayer(lineLayer)
    }
}

private fun addEditingPolygonLayer(
    style: Style,
    points: List<LatLng>
) {
    val featureCollection = if (points.size >= 3) {
        val closedPoints = points + points.first()
        val coordinates = closedPoints.map { Point.fromLngLat(it.longitude, it.latitude) }
        val polygon = Polygon.fromLngLats(listOf(coordinates))
        FeatureCollection.fromFeatures(arrayOf(org.maplibre.geojson.Feature.fromGeometry(polygon)))
    } else {
        FeatureCollection.fromFeatures(emptyArray())
    }

    val source = style.getSourceAs<GeoJsonSource>(EDITING_POLYGON_SOURCE_ID)
    if (source != null) {
        source.setGeoJson(featureCollection)
    } else if (points.size >= 3) {
        val newSource = GeoJsonSource(EDITING_POLYGON_SOURCE_ID, featureCollection)
        style.addSource(newSource)

        val fillLayer = FillLayer(
            EDITING_POLYGON_LAYER_ID,
            EDITING_POLYGON_SOURCE_ID
        )
        fillLayer.setProperties(
            PropertyFactory.fillColor(AndroidColor.BLUE),
            PropertyFactory.fillOpacity(0.4f)
        )
        style.addLayer(fillLayer)

        val outlineLayer = LineLayer(
            EDITING_POLYGON_OUTLINE_LAYER_ID,
            EDITING_POLYGON_SOURCE_ID
        )
        outlineLayer.setProperties(
            PropertyFactory.lineColor(AndroidColor.BLUE),
            PropertyFactory.lineWidth(4f),
            PropertyFactory.lineOpacity(1.0f)
        )
        style.addLayer(outlineLayer)
    }
}

private fun renderFeatures(
    style: Style,
    features: List<Feature>
) {
    if (features.isEmpty()) {
        val existingSource = style.getSourceAs<GeoJsonSource>(FEATURE_SOURCE_ID)
        existingSource?.setGeoJson(FeatureCollection.fromFeatures(emptyArray()))
        return
    }

    val firstGeometryType = features.first().geometryType

    if (firstGeometryType == GeometryType.POINT) {
        val geoJsonFeatures = features.mapNotNull { feature ->
            val lat = feature.latitude ?: return@mapNotNull null
            val lng = feature.longitude ?: return@mapNotNull null
            org.maplibre.geojson.Feature.fromGeometry(Point.fromLngLat(lng, lat)).apply {
                addNumberProperty("featureId", feature.id)
            }
        }
        val featureCollection = FeatureCollection.fromFeatures(geoJsonFeatures)

        val existingSource = style.getSourceAs<GeoJsonSource>(FEATURE_SOURCE_ID)
        if (existingSource != null) {
            if (style.getLayer(FEATURE_LAYER_ID) !is CircleLayer) {
                style.removeLayer(FEATURE_LAYER_ID)
                if (style.getLayer(FEATURE_OUTLINE_LAYER_ID) != null) {
                    style.removeLayer(FEATURE_OUTLINE_LAYER_ID)
                }
                style.removeSource(FEATURE_SOURCE_ID)

                val newSource = GeoJsonSource(FEATURE_SOURCE_ID, featureCollection)
                style.addSource(newSource)
                val layer = CircleLayer(FEATURE_LAYER_ID, FEATURE_SOURCE_ID).apply {
                    setProperties(
                        PropertyFactory.circleRadius(7f),
                        PropertyFactory.circleColor("#E53935"),
                        PropertyFactory.circleStrokeColor("#FFFFFF"),
                        PropertyFactory.circleStrokeWidth(2f)
                    )
                }
                style.addLayer(layer)
            } else {
                existingSource.setGeoJson(featureCollection)
            }
        } else {
            val source = GeoJsonSource(FEATURE_SOURCE_ID, featureCollection)
            style.addSource(source)
            val layer = CircleLayer(FEATURE_LAYER_ID, FEATURE_SOURCE_ID).apply {
                setProperties(
                    PropertyFactory.circleRadius(7f),
                    PropertyFactory.circleColor("#E53935"),
                    PropertyFactory.circleStrokeColor("#FFFFFF"),
                    PropertyFactory.circleStrokeWidth(2f)
                )
            }
            style.addLayer(layer)
        }
    } else if (firstGeometryType == GeometryType.LINE) {
        val geoJsonFeatures = features.mapNotNull { feature ->
            val jsonStr = feature.geometryJson ?: return@mapNotNull null
            try {
                val lineString = LineString.fromJson(jsonStr)
                org.maplibre.geojson.Feature.fromGeometry(lineString).apply {
                    addNumberProperty("featureId", feature.id)
                }
            } catch (e: Exception) {
                android.util.Log.e("FeatureRender", "Unable to parse LINE geometry: ${feature.id}", e)
                null
            }
        }
        val featureCollection = FeatureCollection.fromFeatures(geoJsonFeatures)

        val existingSource = style.getSourceAs<GeoJsonSource>(FEATURE_SOURCE_ID)
        if (existingSource != null) {
            if (style.getLayer(FEATURE_LAYER_ID) !is LineLayer) {
                style.removeLayer(FEATURE_LAYER_ID)
                if (style.getLayer(FEATURE_OUTLINE_LAYER_ID) != null) {
                    style.removeLayer(FEATURE_OUTLINE_LAYER_ID)
                }
                style.removeSource(FEATURE_SOURCE_ID)

                val newSource = GeoJsonSource(FEATURE_SOURCE_ID, featureCollection)
                style.addSource(newSource)
                val layer = LineLayer(FEATURE_LAYER_ID, FEATURE_SOURCE_ID).apply {
                    setProperties(
                        PropertyFactory.lineColor("#E53935"),
                        PropertyFactory.lineWidth(5f),
                        PropertyFactory.lineOpacity(1.0f)
                    )
                }
                style.addLayer(layer)
            } else {
                existingSource.setGeoJson(featureCollection)
            }
        } else {
            val source = GeoJsonSource(FEATURE_SOURCE_ID, featureCollection)
            style.addSource(source)
            val layer = LineLayer(FEATURE_LAYER_ID, FEATURE_SOURCE_ID).apply {
                setProperties(
                    PropertyFactory.lineColor("#E53935"),
                    PropertyFactory.lineWidth(5f),
                    PropertyFactory.lineOpacity(1.0f)
                )
            }
            style.addLayer(layer)
        }
    } else if (firstGeometryType == GeometryType.POLYGON) {
        val geoJsonFeatures = features.mapNotNull { feature ->
            val jsonStr = feature.geometryJson ?: return@mapNotNull null
            try {
                val polygon = Polygon.fromJson(jsonStr)
                org.maplibre.geojson.Feature.fromGeometry(polygon).apply {
                    addNumberProperty("featureId", feature.id)
                }
            } catch (e: Exception) {
                android.util.Log.e("FeatureRender", "Unable to parse POLYGON geometry: ${feature.id}", e)
                null
            }
        }
        val featureCollection = FeatureCollection.fromFeatures(geoJsonFeatures)

        val existingSource = style.getSourceAs<GeoJsonSource>(FEATURE_SOURCE_ID)
        if (existingSource != null) {
            if (style.getLayer(FEATURE_LAYER_ID) !is FillLayer) {
                style.removeLayer(FEATURE_LAYER_ID)
                if (style.getLayer(FEATURE_OUTLINE_LAYER_ID) != null) {
                    style.removeLayer(FEATURE_OUTLINE_LAYER_ID)
                }
                style.removeSource(FEATURE_SOURCE_ID)

                val newSource = GeoJsonSource(FEATURE_SOURCE_ID, featureCollection)
                style.addSource(newSource)

                val fillLayer = FillLayer(FEATURE_LAYER_ID, FEATURE_SOURCE_ID).apply {
                    setProperties(
                        PropertyFactory.fillColor("#E53935"),
                        PropertyFactory.fillOpacity(0.4f)
                    )
                }
                style.addLayer(fillLayer)

                val outlineLayer = LineLayer(FEATURE_OUTLINE_LAYER_ID, FEATURE_SOURCE_ID).apply {
                    setProperties(
                        PropertyFactory.lineColor("#E53935"),
                        PropertyFactory.lineWidth(3f),
                        PropertyFactory.lineOpacity(1.0f)
                    )
                }
                style.addLayer(outlineLayer)
            } else {
                existingSource.setGeoJson(featureCollection)
            }
        } else {
            val source = GeoJsonSource(FEATURE_SOURCE_ID, featureCollection)
            style.addSource(source)

            val fillLayer = FillLayer(FEATURE_LAYER_ID, FEATURE_SOURCE_ID).apply {
                setProperties(
                    PropertyFactory.fillColor("#E53935"),
                    PropertyFactory.fillOpacity(0.4f)
                )
            }
            style.addLayer(fillLayer)

            val outlineLayer = LineLayer(FEATURE_OUTLINE_LAYER_ID, FEATURE_SOURCE_ID).apply {
                setProperties(
                    PropertyFactory.lineColor("#E53935"),
                    PropertyFactory.lineWidth(3f),
                    PropertyFactory.lineOpacity(1.0f)
                )
            }
            style.addLayer(outlineLayer)
        }
    }
}

private fun parseLineGeometry(
    geometryJson: String?
): List<LatLng> {
    if (geometryJson.isNullOrBlank()) {
        return emptyList()
    }

    return runCatching {
        val json = JSONObject(geometryJson)
        val coordinates = json.getJSONArray("coordinates")

        buildList {
            for (i in 0 until coordinates.length()) {
                val coordinate = coordinates.getJSONArray(i)
                val longitude = coordinate.getDouble(0)
                val latitude = coordinate.getDouble(1)
                add(LatLng(latitude, longitude))
            }
        }
    }.getOrDefault(emptyList())
}

private fun parsePolygonGeometry(
    geometryJson: String?
): List<LatLng> {
    if (geometryJson.isNullOrBlank()) {
        return emptyList()
    }

    return runCatching {
        val json = JSONObject(geometryJson)
        val coordinatesArray = json.getJSONArray("coordinates")
        if (coordinatesArray.length() == 0) return emptyList()

        val outerRing = coordinatesArray.getJSONArray(0)
        buildList {
            val count = outerRing.length()
            val limit = if (count > 1 &&
                outerRing.getJSONArray(0).getDouble(0) == outerRing.getJSONArray(count - 1).getDouble(0) &&
                outerRing.getJSONArray(0).getDouble(1) == outerRing.getJSONArray(count - 1).getDouble(1)
            ) {
                count - 1
            } else {
                count
            }

            for (i in 0 until limit) {
                val coordinate = outerRing.getJSONArray(i)
                val longitude = coordinate.getDouble(0)
                val latitude = coordinate.getDouble(1)
                add(LatLng(latitude, longitude))
            }
        }
    }.getOrDefault(emptyList())
}
