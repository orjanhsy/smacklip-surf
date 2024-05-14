package com.example.myapplication.ui.home

//import com.example.myapplication.ui.theme.MyApplicationTheme

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.defaultMinSize
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
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.CallMade
import androidx.compose.material.icons.outlined.Tsunami
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.R
import com.example.myapplication.SmackLipApplication
import com.example.myapplication.model.metalerts.Alert
import com.example.myapplication.model.weatherforecast.DataAtTime
import com.example.myapplication.model.surfareas.SurfArea
import com.example.myapplication.utils.viewModelFactory
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.theme.AppTheme
import com.example.myapplication.ui.theme.AppTypography


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(homeScreenViewModel: HomeScreenViewModel, navController: NavController) {

    val homeScreenUiState: HomeScreenUiState by homeScreenViewModel.homeScreenUiState.collectAsState()
    val favoriteSurfAreas by homeScreenViewModel.favoriteSurfAreas.collectAsState()
    val isSearchActive = remember { mutableStateOf(false) }

    LaunchedEffect(Unit) {
        homeScreenViewModel.loadFavoriteSurfAreas()
    }

    Scaffold(
        bottomBar = {
            BottomBar(navController = navController)
        }
    ) { innerPadding ->
        Column(
            modifier = Modifier
                .padding(innerPadding)
            //.verticalScroll(rememberScrollState())
        ) {
            SearchBar(
                onQueryChange = {},
                isSearchActive = isSearchActive.value,
                onActiveChanged = { isActive ->
                    isSearchActive.value = isActive
                },
                surfAreas = SurfArea.entries.toList(),
                navController = navController
            )
            Box(modifier = Modifier.fillMaxSize()){
                Column (modifier = Modifier
                    .padding(horizontal = 10.dp)
                ){


                    FavoritesList(
                        favorites = favoriteSurfAreas,
                        ofLfNow = homeScreenUiState.ofLfNow,
                        alerts = homeScreenUiState.allRelevantAlerts,
                        homeScreenViewModel,
                        navController = navController
                    )
                    Spacer(modifier = Modifier.height(10.dp))
                    Column (
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                    ){
                        Text(
                            modifier = Modifier
                                .padding(vertical = 15.dp),
                            text = "Alle lokasjoner",
                            style = AppTypography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    )

                    {
                        items(SurfArea.entries) { location ->
                            // TODO: !!
                            SurfAreaCard(
                                location,
                                windSpeed = homeScreenUiState.ofLfNow[location]?.windSpeed ?: 0.0,
                                windGust = homeScreenUiState.ofLfNow[location]?.windGust ?: 0.0,
                                windDir = homeScreenUiState.ofLfNow[location]?.windDir ?: 0.0,
                                waveHeight = homeScreenUiState.ofLfNow[location]?.waveHeight ?: 0.0,
                                waveDir = homeScreenUiState.ofLfNow[location]?.waveDir ?: 0.0,
                                alerts = homeScreenUiState.allRelevantAlerts[location],
                                homeScreenViewModel = homeScreenViewModel,
                                navController = navController
                            )
                        }
                    }
                }
                // ProgressIndicator(isDisplayed = homeScreenUiState.loading)

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
    navController: NavController
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
                .height(56.dp)
                .background(color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(50.dp))
                .fillMaxWidth(),
            shape = CircleShape,
            value = searchQuery,
            onValueChange = { query ->
                searchQuery = query
                onQueryChange(query)
                activeChanged(true)
                expanded = true
            },
            placeholder = { Text(text="Søk etter surfeområde", style = AppTypography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
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
        val filteredSurfAreas =
            surfAreas.filter { it.locationName.contains(searchQuery, ignoreCase = true) }

        if (expanded && searchQuery.isNotEmpty() && filteredSurfAreas.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier.fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                items(filteredSurfAreas) { surfArea ->
                    Column(modifier = Modifier.clickable {
                        navController.navigate("SurfAreaScreen/${surfArea.locationName}")
                    }) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically,
                            modifier = Modifier
                                .padding(vertical = 8.dp)
                        ) {
                            Text(
                                text = surfArea.locationName,
                                style = AppTypography.bodySmall,
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
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
    alerts: Map<SurfArea, List<Alert>>?,
    homeScreenViewModel: HomeScreenViewModel,
    navController: NavController
) {
    Row(
        modifier = Modifier
            .padding(horizontal = 10.dp, vertical = 0.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = "Favoritter",
            style = AppTypography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,

            modifier = Modifier
                .weight(1f, true)
        )
        Button(
            onClick = { homeScreenViewModel.clearAllFavorites()},
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
            Text(text="Tøm favoritter",
                style = AppTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant)

        }
    }

    if (favorites.isNotEmpty()) {
        LazyRow (
            modifier = Modifier
                .padding(start = 2.dp)
        ) {
            items(favorites) { surfArea ->
                Card(
                    modifier = Modifier
                        .padding(horizontal = 4.dp, vertical = 2.dp)
                        .size(width = 150.0.dp, height = 190.00.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .clickable {
                            navController.navigate("SurfAreaScreen/${surfArea.locationName}")
                        }
                ) {
                    // TODO: !!
                    SurfAreaCard(
                        surfArea = surfArea,
                        windSpeed = ofLfNow[surfArea]?.windSpeed ?: 0.0,
                        windGust = ofLfNow[surfArea]?.windGust?: 0.0,
                        windDir = ofLfNow[surfArea]?.windDir?: 0.0,
                        waveHeight = ofLfNow[surfArea]?.waveHeight?: 0.0,
                        waveDir = ofLfNow[surfArea]?.waveDir?: 0.0,
                        alerts = alerts?.get(surfArea),
                        homeScreenViewModel = homeScreenViewModel,
                        showFavoriteButton = false,
                        navController = navController
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
            .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            .size(width = 150.dp, height = 200.dp)
            .clip(RoundedCornerShape(10.dp))

    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.onPrimary)
                .padding(14.dp),
            contentAlignment = Alignment.Center
        ) {
            Text(
                text = "Trykk på stjernen for å legge til favoritt",
                style = AppTypography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
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
    alerts: List<Alert>?,
    homeScreenViewModel: HomeScreenViewModel,
    showFavoriteButton: Boolean = true,
    navController: NavController
) {

    // windDirection
    val rotationAngleWind: Float = windDir.toFloat()
    val rotationAngleWave: Float = waveDir.toFloat()

    Card(
        modifier = Modifier
            .wrapContentSize()
            //.padding(start = 8.dp, top = 2.dp, end = 10.dp, bottom = 10.dp)
            .clickable {
                navController.navigate("SurfAreaScreen/${surfArea.locationName}")
            },
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .width(162.dp)
                .height(162.dp)
                .background(MaterialTheme.colorScheme.onPrimary)

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
                Row(
                    modifier = Modifier
                        .padding(bottom=6.dp, top= 2.dp)
                ) {
                    Text(
                        text = surfArea.locationName,
                        style = AppTypography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                Row {
                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 2.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Tsunami,
                            contentDescription = "tsunami",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .width(17.dp)
                                .height(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = waveHeight.toString(),
                        style = AppTypography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                    Spacer(modifier = Modifier.width(5.dp))
                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 0.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.CallMade,
                            contentDescription = "arrow icon",
                            modifier = Modifier
                                .width(17.dp)
                                .height(17.dp)
                                .rotate(rotationAngleWave - 135)// 180-45
                        )
                    }
                }

                Row {
                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 2.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.Air,
                            contentDescription = "air",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .width(17.dp)
                                .height(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    Text(
                        text = "${windSpeed.toInt()}${if (windGust > windSpeed) " (${windGust.toInt()})" else ""}",
                        style = AppTypography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(5.dp))
                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 0.dp)) {
                        Icon(
                            imageVector = Icons.Outlined.CallMade,
                            contentDescription = "arrow icon",
                            modifier = Modifier
                                .width(17.dp)
                                .height(17.dp)
                                .rotate(rotationAngleWind - 135)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(8.dp))
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
                                .aspectRatio(12f / 5f)
                                .clip(RoundedCornerShape(8.dp))
                        )
                    }
                }
            }
        }
    }
}

/*
@Preview(showBackground = true)
@Composable
private fun PreviewSurfAreaCard() {
    val windSpeed = 1.0
    val windGust = 3.0
    val windDirection = 184.3
    val waveHeight = 5.0
    val waveDir =  184.3

    val viewModel = HomeScreenViewModel(RepositoryImpl())
    AppTheme {
        SurfAreaCard(
            SurfArea.HODDEVIK,
            windSpeed,
            windGust,
            windDirection,
            waveHeight,
            waveDir,
            listOf((Alert(properties = Properties(description = "Det ræinar")))),
            viewModel,
            true
        ) {}
    }
}

 */

@Preview(showBackground = true)
@Composable
private fun PreviewHomeScreen() {
    val hsvm = viewModel<HomeScreenViewModel>(
        factory = viewModelFactory {
            HomeScreenViewModel(
                SmackLipApplication.container.stateFulRepo,
                SmackLipApplication.container.alertsRepo,
                SmackLipApplication.container.settingsRepo
            )
        }
    )
    AppTheme {
        HomeScreen(hsvm, rememberNavController())
    }
}

