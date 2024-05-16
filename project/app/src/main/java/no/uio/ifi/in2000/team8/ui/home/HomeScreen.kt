package no.uio.ifi.in2000.team8.ui.home


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
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.CallMade
import androidx.compose.material.icons.outlined.Air
import androidx.compose.material.icons.outlined.Tsunami
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team8.R
import no.uio.ifi.in2000.team8.model.metalerts.Alert
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.model.weatherforecast.DataAtTime
import no.uio.ifi.in2000.team8.ui.common.composables.BottomBar
import no.uio.ifi.in2000.team8.ui.common.composables.SearchBar
import no.uio.ifi.in2000.team8.ui.theme.AppTypography

/*
HomeScreen is the start destination in the application.

The screen contains a convenient search bar navigating directly to
SurfAreaScreen containing detailed information about locations saved in SurfArea.

HomeScreen contains a LazyVerticalGrid showcasing all surf areas, providing essential data like
wave height, wave direction, wind speed, gusts and wind direction.
Surf locations are possible to save from the lazyVerticalGrid with just clicking the
star icon in the upper corner. Clicking it adds it to a row of cards displayed right under
the search bar.
 */

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
        ) {
            // Searchbar allows exploring all surf areas, when clicking a result it will navigate to the SurfAreaScreen for the specific location
            SearchBar(
                surfAreas = SurfArea.entries.toList(),
                onQueryChange = {},
                isSearchActive = isSearchActive.value,
                onActiveChanged = { isActive ->
                    isSearchActive.value = isActive
                },
                resultsColor = MaterialTheme.colorScheme.background,
                onItemClick = { surfArea ->
                    navController.navigate("SurfAreaScreen/${surfArea.locationName}")
                }
            )
            Box(modifier = Modifier.fillMaxSize()){
                Column (modifier = Modifier
                    .padding(horizontal = 10.dp)
                ){

                    // A section for surf area locations saved as favorites.
                    // Containing the same information as the regular surf area cards but will show up under a favorite section
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
                                .padding(vertical = 3.dp),
                            text = "Alle lokasjoner",
                            style = AppTypography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                    // Sorting all locations using a scrollable vertical grid with two and two cards.
                    LazyVerticalGrid(
                        columns = GridCells.Fixed(2),
                        modifier = Modifier
                            .padding(horizontal = 6.dp)
                            .fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(8.dp),
                        verticalArrangement = Arrangement.spacedBy(8.dp)
                    )

                    // The actual values shown in the surfarea cards for each entry in our enum class SurfArea, which contains our locations.
                    // Values are retrieved from the homeScreenUiState based on location.
                    // passing navController allowing navigation from clicking the card
                    {
                        items(SurfArea.entries) { location ->
                            SurfAreaCard(
                                location,
                                windSpeed = homeScreenUiState.ofLfNow[location]?.windSpeed ?: 0.0,
                                windGust = homeScreenUiState.ofLfNow[location]?.windGust ?: 0.0,
                                windDir = homeScreenUiState.ofLfNow[location]?.windDir ?: 0.0,
                                waveHeight = homeScreenUiState.ofLfNow[location]?.waveHeight ?: 0.0,
                                waveDir = homeScreenUiState.ofLfNow[location]?.waveDir ?: 0.0,
                                homeScreenViewModel = homeScreenViewModel,
                                navController = navController
                            )
                        }
                    }
                }
            }
        }
    }
}


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
        verticalAlignment = Alignment.Bottom
    ) {
        Text(
            text = "Favoritter",
            style = AppTypography.bodySmall,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier
                .weight(1f, true)
        )
        // Button that will clear all saved favorites from the dataStore.
        // Once cleared, unless re-saved, they will no longer appear in the favorites section
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
                    SurfAreaCard(
                        surfArea = surfArea,
                        windSpeed = ofLfNow[surfArea]?.windSpeed ?: 0.0,
                        windGust = ofLfNow[surfArea]?.windGust?: 0.0,
                        windDir = ofLfNow[surfArea]?.windDir?: 0.0,
                        waveHeight = ofLfNow[surfArea]?.waveHeight?: 0.0,
                        waveDir = ofLfNow[surfArea]?.waveDir?: 0.0,
                        homeScreenViewModel = homeScreenViewModel,
                        showFavoriteButton = false,
                        navController = navController
                    )
                    if (!alerts.isNullOrEmpty()) { //ørjan se her
                        Image(
                            painter = painterResource(id = R.drawable.icon_awareness_yellow_outlined),
                            contentDescription = "Farevarsel",
                            modifier = Modifier
                                .padding(8.dp)
                                .size(24.dp)
                        )
                    } else {
                        Image(
                            painter = painterResource(id = R.drawable.icon_awareness_yellow_outlined),
                            contentDescription = "Farevarsel",
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

// A empty card will be displayed if no favorites are saved
// Serves as a reminder that you can save your favorite locations for easy access
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

// Our data presented in the cards shown in HomeScreen
@Composable
fun SurfAreaCard(
    surfArea: SurfArea,
    windSpeed: Double,
    windGust: Double,
    windDir: Double,
    waveHeight: Double,
    waveDir: Double,
    homeScreenViewModel: HomeScreenViewModel,
    showFavoriteButton: Boolean = true,
    navController: NavController
) {

    // using our wind and wave direction values to show as arrows rotating in value direction.
    val rotationAngleWind: Float = windDir.toFloat()
    val rotationAngleWave: Float = waveDir.toFloat()

    Card(
        modifier = Modifier
            .wrapContentSize()
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

            // If clicking the star the surfarea location will be saved in the dataStore so favorites wont disappear unless removed.
            if (showFavoriteButton) {
                IconButton(
                    onClick = { homeScreenViewModel.updateFavorites(surfArea) },
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(end = 0.dp, top = 0.dp)
                ){
                    Icon(
                        painter = painterResource(id = homeScreenViewModel.updateFavoritesIcon(surfArea)),
                        contentDescription = "Stjerneikon",
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
                        // icon for waves
                        Icon(
                            imageVector = Icons.Outlined.Tsunami,
                            contentDescription = "Bølgeikon",
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
                        //Utilizing wave direction value, displaying a arrow that dynamically rotate according to the direction indicated by the value. .
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.CallMade,
                            contentDescription = "Bølgeretning pil",
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
                            // icon for wind
                            imageVector = Icons.Outlined.Air,
                            contentDescription = "Vindikon",
                            tint = MaterialTheme.colorScheme.onSurfaceVariant,
                            modifier = Modifier
                                .width(17.dp)
                                .height(17.dp)
                        )
                    }
                    Spacer(modifier = Modifier.width(5.dp))
                    // showing wind speed and wind gust in the SurfAreaCard
                    Text(
                        text = "${windSpeed.toInt()}${if (windGust > windSpeed) " (${windGust.toInt()})" else ""}",
                        style = AppTypography.bodySmall,
                        color = MaterialTheme.colorScheme.onSurfaceVariant
                    )

                    Spacer(modifier = Modifier.width(5.dp))
                    Box(contentAlignment = Alignment.TopCenter, modifier = Modifier.padding(top = 0.dp)) {
                        // rotating arrow in the wind direction
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.CallMade,
                            contentDescription = "Vindretning pil",
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
                            contentDescription = "Bilde av strand",
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


