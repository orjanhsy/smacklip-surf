package com.example.myapplication.ui.map

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.Tsunami
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.compose.ui.viewinterop.NoOpUpdate
import androidx.core.graphics.drawable.toBitmap
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.theme.AppTheme
import com.example.myapplication.ui.theme.AppTypography
import com.example.myapplication.ui.theme.onSurfaceVariantLight
import com.example.myapplication.utils.MapUtils
import com.example.myapplication.utils.RecourseUtils
import com.mapbox.geojson.Point
import com.mapbox.maps.CameraOptions
import com.mapbox.maps.MapView
import com.mapbox.maps.Style
import com.mapbox.maps.plugin.animation.flyTo
import com.mapbox.maps.plugin.annotation.annotations
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationManager
import com.mapbox.maps.plugin.annotation.generated.PointAnnotationOptions
import com.mapbox.maps.plugin.annotation.generated.createPointAnnotationManager


//MapScreen er der selve skjermen lages, inkludert bottombar, kartet og searchbar på toppen
//MapScreen-method is where the screen itself is created, including the bottom bar,
// the map, and the search bar at the top.
@Composable
fun MapScreen(mapScreenViewModel : MapScreenViewModel, navController: NavController) {

    val mapScreenUiState : MapScreenUiState by mapScreenViewModel.mapScreenUiState.collectAsState()
    val isSearchActive = remember { mutableStateOf(false) }
    val rememberPoint : MutableState<Point?> = remember { mutableStateOf(null) }
    val mapUtils = MapUtils()


    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                .fillMaxSize()
        ) {
            //With Box, each component is placed on top of each other, the bottom method being
            //called is placed foremost makes the search bar visible.
            Box(modifier = Modifier.weight(1f)) {
                //the map
                MapBoxMap(
                    modifier = Modifier
                        .fillMaxSize(),
                    locations = mapUtils.locationToPoint(),
                    uiState = mapScreenUiState,
                    navController = navController,
                    rememberPoint = rememberPoint
                )
                //the search bar
                SearchBar(
                    onQueryChange = {},
                    isSearchActive = isSearchActive.value,
                    onActiveChanged = { isActive ->
                        isSearchActive.value = isActive
                    },
                    surfAreas = SurfArea.entries.toList(),
                    onZoomToLocation = { point -> rememberPoint.value = point },
                )
            }
        }
    }
}

//get the map from MapBox:
@Composable
fun MapBoxMap(
    modifier: Modifier = Modifier,
    locations: List<Pair<SurfArea, Point>>,
    uiState: MapScreenUiState,
    navController: NavController,
    rememberPoint: MutableState<Point?>
) {
    val mapUtils = MapUtils()
    val startPosition = Point.fromLngLat(13.0, 65.1) //The coordinates for the starting point on the map - approximately in the middle of Norway
    val context = LocalContext.current
    val marker = remember(context) {
        context.getDrawable(R.drawable.marker )!!.toBitmap()
    }

    val selectedMarker = remember { mutableStateOf<SurfArea?>(null) }

    var pointAnnotationManager: PointAnnotationManager? by remember {
        mutableStateOf(null)
    }

    val rememberCameraState = remember { mutableStateOf<CameraOptions?>(null) }

    Box (
        contentAlignment = Alignment.Center
    ){
        //the actual map is composed in AndroidView
        AndroidView(
            //factory runs initially
            factory = {

                MapView(it).also { mapView ->
                    //Determine how the map should be displayed when first loaded:
                    mapView.mapboxMap.loadStyle(Style.STANDARD)
                    val annotationApi = mapView.annotations
                    pointAnnotationManager = annotationApi.createPointAnnotationManager()
                }
            },
            //update updates the view on the screen when the user interacts with the screen
            update = { mapView ->
                //initial camera options using startPosition
                if (rememberPoint.value == null && rememberCameraState.value == null){
                    mapView.mapboxMap.flyTo(CameraOptions.Builder().zoom(3.8).center(startPosition).build())
                }
                //update camera state when the user clickes on a locations in the searchbar
                else if (rememberPoint.value != null){
                    mapView.mapboxMap.flyTo(CameraOptions.Builder().zoom(10.0).center(rememberPoint.value).build())
                    rememberCameraState.value = CameraOptions.Builder().zoom(10.0).center(rememberPoint.value).build()
                    rememberPoint.value = null //set to null when the camera state has been updated
                }
                //if the user exits the location card the camera state should be the same as before the card vas shown
                else if (rememberCameraState.value != null){
                    mapView.mapboxMap.flyTo(CameraOptions.Builder().zoom(rememberCameraState.value!!.zoom).center(
                        rememberCameraState.value!!.center).build())
                }

                pointAnnotationManager?.let {
                    it.addClickListener { pointAnnotation ->
                        // Handle click event:
                        val clickedPoint = pointAnnotation.point
                        val cameraState = mapView.mapboxMap.cameraState
                        rememberCameraState.value = CameraOptions.Builder().zoom(cameraState.zoom).center(cameraState.center).build() //remember the camera state when the user clickes on marker

                        try {
                            val loc = locations.first { location ->
                                mapUtils.isMatchingCoordinates(
                                    location.second,
                                    clickedPoint
                                )
                            }
                            selectedMarker.value = loc.first

                        } catch (_: NoSuchElementException) { //first-methods throws exception if it does not find matching coordinates
                            selectedMarker.value = null
                        }
                        true
                    }

                    //add markers for each location
                    locations.forEach { (location, point) ->
                        val pointAnnotationOptions = PointAnnotationOptions()
                            .withPoint(point)
                            .withIconImage(marker)
                        it.create(pointAnnotationOptions)
                    }
                }
                NoOpUpdate
            },
            modifier = modifier
        )
        //Show corresponding SurfAreaCard if a marker is clicked
        if (selectedMarker.value != null) {
            SurfAreaCard(
                surfArea = selectedMarker.value!!,
                onCloseClick = { selectedMarker.value = null },
                uiState = uiState,
                navController = navController
            )
            Modifier.padding(16.dp)
        }
    }
}



@Composable
fun SurfAreaCard(
    surfArea: SurfArea,
    onCloseClick: () -> Unit,
    uiState: MapScreenUiState,
    resourceUtils: RecourseUtils = RecourseUtils(),
    navController: NavController
) {

    //current data for surfArea:
    val windSpeed: Double = uiState.oflfNow[surfArea]?.windSpeed ?: 0.0
    val windGust: Double = uiState.oflfNow[surfArea]?.windGust ?: 0.0
    val airTemperature: Double = uiState.oflfNow[surfArea]?.airTemp ?: 0.0
    val symbolCode: String = uiState.oflfNow[surfArea]?.symbolCode ?: ""
    val waveHeight: Double = uiState.oflfNow[surfArea]?.waveHeight ?: 0.0

    Card(
        modifier = Modifier
            .width(350.dp)
            .wrapContentHeight(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.onPrimary
        )

    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(top = 4.dp)
            ) {
                Text(
                    text = surfArea.locationName,
                    fontWeight = FontWeight.Bold,
                    fontSize = 25.sp,
                    textAlign = TextAlign.Center,
                    modifier = Modifier
                        .align(Alignment.Center)
                )
                IconButton(
                    onClick = onCloseClick,
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                ) {
                    Icon(
                        imageVector = Icons.Outlined.Close,
                        contentDescription = "Close",
                        tint = onSurfaceVariantLight
                    )
                }
            }


            Spacer(modifier = Modifier.height(8.dp))
            //text description of location
            Text(
                text = stringResource(surfArea.description),
                style = AppTypography.titleSmall,
                textAlign = TextAlign.Center
            )

            //The information in the card:
            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {
                //wind:
                Icon(
                    imageVector = Icons.Outlined.Air,
                    contentDescription = "Tsunami",
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "${windSpeed.toInt()}(${windGust.toInt()})",
                    style = AppTypography.bodySmall,
                    modifier = Modifier.padding(horizontal=8.dp)
                )
                //waves
                Icon(
                    imageVector = Icons.Outlined.Tsunami,
                    contentDescription = "Tsunami",
                    modifier = Modifier.size(18.dp)
                )
                Text(
                    text = "$waveHeight",
                    style = AppTypography.bodySmall,
                    modifier = Modifier.padding(horizontal=8.dp)
                )
                //weather symbol
                Image(
                    painter = painterResource(id = resourceUtils.findWeatherSymbol(symbolCode)),
                    contentDescription = "wave icon",
                    modifier = Modifier
                        .padding(horizontal = 8.dp)
                        .width(30.dp)
                        .height(30.dp)
                )
                //temperature
                Text(
                    text = "${airTemperature.toInt()} °C",
                    style = AppTypography.bodySmall,
                    modifier = Modifier.padding(horizontal=8.dp)
                )
            }
            //image of surf area
            if (surfArea.image != 0) {
                Image(
                    painter = painterResource(id = surfArea.image),
                    contentDescription = "SurfArea Image",
                    contentScale = ContentScale.FillBounds,
                    modifier = Modifier
                        .width(240.dp)
                        .height(100.dp)
                        .clip(RoundedCornerShape(8.dp))
                )
            }

            Row(
                modifier = Modifier
                    .fillMaxWidth(),
                horizontalArrangement = Arrangement.Center,
                verticalAlignment = Alignment.CenterVertically
            ) {

                //Navigate to SurfAreaScreen
                Button(
                    onClick = {
                       navController.navigate("SurfAreaScreen/${surfArea.locationName}")
                    },
                    modifier = Modifier
                        .defaultMinSize(minWidth = 62.dp, minHeight = 32.dp)
                        .padding(start = 8.dp, top = 2.dp),
                    contentPadding = PaddingValues(
                        top = 4.dp,
                        bottom = 4.dp,
                        start = 8.dp,
                        end = 8.dp
                    ),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = MaterialTheme.colorScheme.inverseOnSurface
                    )

                ) {
                    Text(
                        text = "   Gå til ${surfArea.locationName}   ",
                        style = AppTypography.bodySmall,
                        color =MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }
            }
        }
    }
}


/*
The search bar at the top of the map screen gives the user the ability to search for
surf area. By pressing a location in the search bar, the map camera moves to the desired
location. This is achieved using the parameter "onZoomToLocation," which is a lambda function.
There is no functionality to move the camera in the SearchBar-method itself which
adheres to object-oriented principles.
 */
@Composable
fun SearchBar(
    surfAreas: List<SurfArea>,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: ((String) -> Unit)? = null,
    onZoomToLocation: (Point) -> Unit
) {
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    val activeChanged: (Boolean) -> Unit = { active ->
        if (!active) {
            searchQuery = ""
            onQueryChange("")
        }
        onActiveChanged(active)
    }

    Column(modifier = modifier.clip(RoundedCornerShape(50.dp))) {
        OutlinedTextField(
            modifier = modifier
                .padding(12.dp)
                .fillMaxWidth()
                .background(color = Color.Transparent),
            shape = CircleShape,
            colors = OutlinedTextFieldDefaults.colors(
                focusedContainerColor = MaterialTheme.colorScheme.surface,
                unfocusedContainerColor = MaterialTheme.colorScheme.surface,
                disabledContainerColor = MaterialTheme.colorScheme.surface,
                cursorColor = MaterialTheme.colorScheme.onBackground,
                focusedBorderColor = Color.Gray,
                unfocusedBorderColor = Color.Gray,
            ),
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                onQueryChange(query)
                activeChanged(true)
                expanded = true
            },
            placeholder = { Text("Søk etter surfeområde", style = AppTypography.titleMedium) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Search icon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            trailingIcon = {
                if (isSearchActive) {
                    IconButton(
                        onClick = {
                            searchQuery = ""
                            onQueryChange("")
                            onActiveChanged(false)
                            focusManager.clearFocus()
                            keyboardController?.hide()
                        }
                    ) {
                        Icon(
                            imageVector = Icons.Default.Clear,
                            contentDescription = "Clear searchbar"
                        )
                    }
                }
            },
            singleLine = true,
            keyboardOptions = KeyboardOptions.Default.copy(
                imeAction = ImeAction.Search
            ),
            keyboardActions = KeyboardActions(
                onSearch = {
                    onSearch?.invoke(searchQuery)
                    activeChanged(false)
                    keyboardController?.hide()
                    focusManager.clearFocus()
                }
            )
        )
        //show available locations matching the user input
        val filteredSurfAreas =
            surfAreas.filter { it.locationName.startsWith(searchQuery, ignoreCase = true) }

        if (expanded && searchQuery.isNotEmpty() && filteredSurfAreas.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(start = 12.dp, top = 0.dp, end = 12.dp, bottom = 12.dp)
                    .background(Color.White),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                items(filteredSurfAreas) { surfArea ->
                    Column(modifier = Modifier.clickable {
                        searchQuery = ""
                        onQueryChange("")
                        onActiveChanged(false)
                        focusManager.clearFocus()
                        keyboardController?.hide()
                        onZoomToLocation(Point.fromLngLat(surfArea.lon, surfArea.lat)) //send the coordinates as argument to onZoomToLocation
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = surfArea.locationName,
                                modifier = Modifier.weight(1f)
                            )
                            Spacer(modifier = Modifier.width(12.dp))
                            Image(
                                painter = painterResource(id = surfArea.image),
                                contentDescription = "SurfArea image",
                                modifier = Modifier.size(48.dp),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.CenterEnd
                            )
                        }
                        Divider(modifier = Modifier.padding(horizontal = 12.dp))
                    }
                }
            }
        }
    }
}

@Preview
@Composable
fun SurfAreaPreview(){
    AppTheme {
        SurfAreaCard(surfArea = SurfArea.STAVASANDEN, {}, MapScreenUiState(), RecourseUtils(), rememberNavController())
    }
}


