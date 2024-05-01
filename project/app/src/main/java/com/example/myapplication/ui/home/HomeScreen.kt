package com.example.myapplication.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.outlined.CallMade
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Card
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.ExperimentalComposeUiApi
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.myapplication.NavigationManager
import com.example.myapplication.R
import com.example.myapplication.model.metalerts.Features
import com.example.myapplication.model.metalerts.Properties
import com.example.myapplication.model.smacklip.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.common.composables.ProgressIndicator
import com.example.myapplication.ui.theme.MyApplicationTheme

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeScreenViewModel : HomeScreenViewModel = viewModel(), onNavigateToSurfAreaScreen: (String) -> Unit = {}){
    val homeScreenUiState: HomeScreenUiState by homeScreenViewModel.homeScreenUiState.collectAsState()
    val favoriteSurfAreas by homeScreenViewModel.favoriteSurfAreas.collectAsState()
    val isSearchActive = remember { mutableStateOf(false) }
    val navController = NavigationManager.navController

    Scaffold(
        topBar = {
            Column {
                SearchBar(
                    onQueryChange = {},
                    isSearchActive = isSearchActive.value,
                    onActiveChanged = { isActive ->
                        isSearchActive.value = isActive
                    },
                    surfAreas = SurfArea.entries.toList(),
                    onNavigateToSurfAreaScreen = onNavigateToSurfAreaScreen
                )
            }
        },
        bottomBar = {
            BottomBar(
                onNavigateToMapScreen = {
                    navController?.navigate("MapScreen")
                    // Navigerer til MapScreen
                }
            )
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
                    //.verticalScroll(rememberScrollState())
        ) {
            Box(modifier = Modifier.fillMaxSize()){
                Column (modifier = Modifier.fillMaxSize()){
                    FavoritesList(
                        favorites = favoriteSurfAreas,
                        ofLfNow = homeScreenUiState.ofLfNow,
                        alerts = homeScreenUiState.allRelevantAlerts,
                        onNavigateToSurfAreaScreen = onNavigateToSurfAreaScreen
                    )
                    Column {

                        Text(
                            text = "  Alle lokasjoner",
                            style = TextStyle(
                                fontSize = 15.sp,
                                //fontFamily = FontFamily(Font(R.font.inter))
                                fontWeight = FontWeight(400),
                                color = Color(0xFF9A938C)
                            )
                        )
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .fillMaxWidth(),
                        verticalArrangement = Arrangement.spacedBy(8.dp),
                        horizontalArrangement = Arrangement.Center)

                    {
                        items(SurfArea.entries) { location ->
                            // TODO: !!
                            SurfAreaCard(
                                location,
                                windSpeed = homeScreenUiState.ofLfNow[location]!!.windSpeed,
                                windGust = homeScreenUiState.ofLfNow[location]!!.windGust,
                                windDir = homeScreenUiState.ofLfNow[location]!!.windDir,
                                waveHeight = homeScreenUiState.ofLfNow[location]!!.waveHeight,
                                waveDir = homeScreenUiState.ofLfNow[location]!!.waveDir,
                                alerts = homeScreenUiState.allRelevantAlerts[location],
                                homeScreenViewModel = homeScreenViewModel,
                                onNavigateToSurfAreaScreen = onNavigateToSurfAreaScreen
                            )
                        }
                    }
                }
                ProgressIndicator(isDisplayed = homeScreenUiState.loading)

            }
        }
    }
}

@OptIn(ExperimentalComposeUiApi::class)
@Composable
fun SearchBar(
    surfAreas: List<SurfArea>,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    onSearch: ((String) -> Unit)? = null,
    onNavigateToSurfAreaScreen: (String) -> Unit
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

    Column(modifier = modifier) {
        OutlinedTextField(
            modifier = modifier
                .padding(12.dp)
                .fillMaxWidth(),
            shape = CircleShape,
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                onQueryChange(query)
                activeChanged(true)
                expanded = true
            },
            placeholder = { Text("Søk etter surfeområde") },
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
        if (expanded && searchQuery.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                val filteredSurfAreas =
                    surfAreas.filter { it.locationName.contains(searchQuery, ignoreCase = true) }
                items(filteredSurfAreas) { surfArea ->
                    Column(modifier = Modifier.clickable {
                        onNavigateToSurfAreaScreen(surfArea.locationName)
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
            /* if (searchQuery.isNotEmpty() && filteredSurfAreas.isEmpty() && expanded) {
            Text("Ingen samsvarende resultater")
        } */
        }
    }
}

/* TODO:
implement windspeedmap, windgustmap, waveheightmap and alerts correctly,
to receive accurate values in favorite surfareacards
 */

@Composable
fun FavoritesList(
    favorites: List<SurfArea>,
    ofLfNow: Map<SurfArea, DataAtTime>,
    alerts: Map<SurfArea, List<Features>>?,
    onNavigateToSurfAreaScreen: (String) -> Unit
) {
    Column {
        Text(
            text = "  Favoritter",
            style = TextStyle(
                fontSize = 15.sp,
                //fontFamily = FontFamily(Font(R.font.inter))
                fontWeight = FontWeight(400),
                color = Color(0xFF9A938C)
            )
        )
    }
    if (favorites.isNotEmpty()) {
        LazyRow {
            items(favorites) { surfArea ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 8.dp, vertical = 8.dp)
                        .size(width = 150.0.dp, height = 200.00.dp)
                        .clip(RoundedCornerShape(10.dp))
                ) {
                    // TODO: !!
                    SurfAreaCard(
                        surfArea = surfArea,
                        windSpeed = ofLfNow[surfArea]!!.windSpeed,
                        windGust = ofLfNow[surfArea]!!.windGust,
                        windDir = ofLfNow[surfArea]!!.windDir,
                        waveHeight = ofLfNow[surfArea]!!.waveHeight,
                        waveDir = ofLfNow[surfArea]!!.waveDir,
                        alerts = alerts?.get(surfArea),
                        homeScreenViewModel = HomeScreenViewModel(),
                        showFavoriteButton = false,
                        onNavigateToSurfAreaScreen = onNavigateToSurfAreaScreen
                    )
                    if (!alerts.isNullOrEmpty()) {
                        Image(
                            painter = painterResource(id = R.drawable.icon_awareness_yellow_outlined),
                            contentDescription = "warning icon",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.icon_awareness_yellow_outlined),
                            contentDescription = "warning icon",
                            modifier = Modifier
                                .padding(8.dp)
                        )
                    }
                }
            }
        }
    } else {
        EmptyFavoriteCard()
    }
}

@Composable
fun EmptyFavoriteCard() {
    Card(
        modifier =
        Modifier
            .wrapContentSize()
            .padding(start = 8.dp, top = 10.dp, end = 8.dp, bottom = 8.dp)
            //.border(width = 0.80835.dp, color = Color(0xFFBEC8CA), shape = RoundedCornerShape(size = 6.70023.dp ))
            .size(width = 150.dp, height = 200.dp)
            .background(color = Color(0xFFF5FAFB))
            .clip(RoundedCornerShape(10.dp))

    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Ingen favoritter lagt til",
                fontSize = 20.sp,
                textAlign = TextAlign.Center,
                color = Color.Gray,
                modifier = Modifier.padding(16.dp)
            )
        }
    }
}

@Composable
fun SurfAreaCard(
    surfArea: SurfArea,
    windSpeed: Double,
    windGust: Double,
    windDir: Double,
    waveHeight: Double,
    waveDir: Double,
    alerts: List<Features>?,
    homeScreenViewModel: HomeScreenViewModel,
    showFavoriteButton: Boolean = true,
    onNavigateToSurfAreaScreen: (String) -> Unit
) {

    // windDirection
    val rotationAngleWind: Float = windDir.toFloat()
    val rotationAngleWave: Float = waveDir.toFloat()

    Card(
        modifier = Modifier
            .wrapContentSize()
            .padding(start = 8.dp, top = 10.dp, end = 10.dp, bottom = 10.dp)
            .shadow(4.dp, shape = RoundedCornerShape(10.dp))
            .clickable(
                onClick = { onNavigateToSurfAreaScreen(surfArea.locationName) })
    ) {

        Box(
            modifier = Modifier
                .fillMaxSize()
                .width(162.dp)
                .height(162.dp)

        ) {

            // Stjerneikon
            if (showFavoriteButton) {
                IconButton(
                    onClick = { homeScreenViewModel.updateFavorites(surfArea) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 0.dp, top = 0.dp)
                ){
                    Icon(
                        painter = painterResource(id = homeScreenViewModel.updateFavoritesIcon(surfArea)),
                        contentDescription = "Toggle favorite",
                        modifier = Modifier.size(16.dp)
                    )
                }
            }

            Column(
                modifier = Modifier
                    .fillMaxHeight()
                    .padding(16.dp),
                horizontalAlignment = Alignment.Start,
            )
            {
                Row {
                    Text(
                        text = surfArea.locationName,
                        style = TextStyle(
                            fontSize = 12.93.sp,
                            lineHeight = 19.4.sp,
                            fontWeight = FontWeight(700),
                            letterSpacing = 0.12.sp

                        )

                    )
                }

                Row {
                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 4.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.tsunami),
                            contentDescription = "wave icon",
                            modifier = Modifier
                                .padding(0.02021.dp)
                                .width(15.3587.dp)
                                .height(14.47855.dp)

                        )
                    }
                    Text(
                        text = waveHeight.toString()
                    )
                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.CallMade,
                            contentDescription = "arrow icon",
                            modifier = Modifier
                                .width(17.dp)
                                .height(17.dp)
                                .rotate(rotationAngleWave - 45)
                        )
                    }
                }

                Row {
                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 4.dp)) {
                        Image(
                            painter = painterResource(id = R.drawable.air),
                            contentDescription = "Air icon",
                            modifier = Modifier
                                .padding(0.02021.dp)
                                .width(15.3587.dp)
                                .height(13.6348.dp)
                        )
                    }
                    Text(
                        text = windSpeed.toString() + if (windGust > windSpeed) windGust.toString() else ""
                    )

                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 4.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.CallMade,
                            contentDescription = "arrow icon",
                            modifier = Modifier
                                .width(17.dp)
                                .height(17.dp)
                                .rotate(rotationAngleWind - 45)
                        )
                    }
                }

                /*
                Row {
                    Text(
                        // only shows description of first alert. There may be several.
                        text = "Alert:  ${if (alerts?.isNotEmpty() == true) alerts[0][0].properties?.description else ""}"
                    )
                }

                Row {
                    if (alerts?.isNotEmpty() == true) {
                        val icon =
                            homeScreenViewModel.getIconBasedOnAwarenessLevel(alerts[0][0].properties?.awarenessLevel.toString())
                        Image(
                            painter = painterResource(id = icon),
                            contentDescription = "Awareness Level Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.icon_awareness_default),
                            contentDescription = "Awareness Level Icon",
                            modifier = Modifier.size(24.dp)
                        )
                    }
                }

                 */
                Column(
                    horizontalAlignment = Alignment.End
                ) {
                    if (surfArea.image != 0) {
                        Image(
                            painter = painterResource(id = surfArea.image),
                            contentDescription = "SurfArea Image",
                            contentScale = ContentScale.FillBounds,
                            modifier = Modifier
                                .width(162.dp)
                                .height(100.dp)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSurfAreaCard() {
    val windSpeed = 1.0
    val windGust = 3.0
    val windDirection = 184.3
    val waveHeight = 5.0
    val waveDir =  184.3

    val viewModel = HomeScreenViewModel()
    MyApplicationTheme {
        SurfAreaCard(
            SurfArea.HODDEVIK,
            windSpeed,
            windGust,
            windDirection,
            waveHeight,
            waveDir,
            listOf((Features(properties = Properties(description = "Det ræinar")))),
            viewModel,
            true
        ) {}
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    MyApplicationTheme {
        HomeScreen(){}
    }
}