package no.uio.ifi.in2000.team8.ui.info

import androidx.compose.animation.animateContentSize
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ExpandLess
import androidx.compose.material.icons.filled.ExpandMore
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.ColorFilter
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import no.uio.ifi.in2000.team8.R
import no.uio.ifi.in2000.team8.Settings
import no.uio.ifi.in2000.team8.ui.common.composables.BottomBar
import no.uio.ifi.in2000.team8.ui.theme.AppTheme
import no.uio.ifi.in2000.team8.ui.theme.AppTypography

/*
InfoScreen is the screen where all the information lays. The upper half of the screen contains a image of our logo for SmackLip Surf.
In the lower half of the screen we have four expandable cards. The first card contains a switch button that allows
the user to chose either dark or light theme for the app. The second card contains text about why we chose to make the application,
the third card contains text about how we calculate our conditions, and the final card contains text about where we get our data from.
 */
@Composable
fun InfoScreen(infoScreenViewModel: InfoScreenViewModel, navController: NavController) {
    val isDarkThemeEnabled by infoScreenViewModel.isDarkThemEnabled.collectAsState()


    AppTheme(useDarkTheme = isDarkThemeEnabled) {
        Scaffold(
            bottomBar = {
                BottomBar(navController = navController)
            }
        ) { innerPadding ->
            Column(
                modifier = Modifier
                    .padding(innerPadding)
            ) {
                    Column(
                        modifier = Modifier,

                        verticalArrangement = Arrangement.Center,
                        horizontalAlignment = Alignment.CenterHorizontally
                    ) {
                        Image(
                            painter = painterResource(id = R.drawable.smacklip_logo),
                            contentDescription = "app logo",
                            contentScale = ContentScale.FillBounds,
                            colorFilter = ColorFilter.tint(MaterialTheme.colorScheme.onTertiary),

                                    modifier = Modifier
                                        .width(307.dp)
                                        .height(259.dp)
                                        .padding(16.dp)
                        )
                        Spacer(modifier = Modifier.height(14.dp))

                        LazyColumn(
                            modifier = Modifier
                                .fillMaxSize(),
                            verticalArrangement = Arrangement.Center,
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            item {
                                Card(
                                    modifier = Modifier
                                        .width(300.dp)
                                        .heightIn(min = 57.dp)
                                        .animateContentSize(),
                                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
                                    elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
                                ) {
                                    var expandedThemeCard by remember { mutableStateOf(false) }
                                    Column(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .padding(4.dp)
                                    )
                                     {
                                        Text(
                                            text = "Velg appmodus",
                                            style = AppTypography.titleMedium,
                                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                                            modifier = Modifier.fillMaxWidth()
                                                .padding(top = 4.dp)

                                        )
                                         IconButton(
                                             onClick = { expandedThemeCard = !expandedThemeCard },
                                             modifier = Modifier
                                                 .align(Alignment.CenterHorizontally)
                                         ) {
                                             Icon(
                                                 imageVector = if (expandedThemeCard)
                                                     Icons.Filled.ExpandLess
                                                 else
                                                     Icons.Filled.ExpandMore,
                                                 contentDescription = if (expandedThemeCard) "Skjul" else "Utvid",

                                             )
                                         }
                                        // Expandable card for dark or light mode switch
                                        if (expandedThemeCard) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight(),
                                                horizontalArrangement = Arrangement.Center
                                            ) {
                                                Text(text = if (isDarkThemeEnabled) "Bytt til Light Mode " else "Bytt til Dark Mode",
                                                    textAlign = TextAlign.Center,
                                                )
                                            }
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight(),
                                                horizontalArrangement = Arrangement.Center
                                            ){
                                                //Saving the new theme locally ensures that it remains unchanged when navigating away from the screen or exiting the application
                                                Switch(
                                                            checked = isDarkThemeEnabled,
                                                    onCheckedChange = { isChecked ->
                                                        infoScreenViewModel.updateTheme(if (isChecked) Settings.Theme.DARK else Settings.Theme.LIGHT)
                                                    }
                                                )
                                            }
                                        }

                                    }
                                }
                                Spacer(modifier = Modifier.height(14.dp))
                            }

                            item{
                                InformationCard(
                                    title = "Hvorfor SmackLip Surf?",
                                    content = stringResource(id = R.string.hvorfor_smacklip)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                            item {
                                InformationCard(
                                    title = "Hvordan beregner vi forhold?",
                                    content = stringResource(id = R.string.beregner_forhold)
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                            item{
                                InformationCard(
                                    title = "Hvor henter vi data fra?",
                                    content = stringResource(id = R.string.henter_data)
                                )
                            }
                        }
                    }
            }
        }

    }

}
// Expandable cards for our cards containing information

@Composable
fun InformationCard(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .width(300.dp)
            .heightIn(min = 57.dp)
            .animateContentSize(),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.onPrimary),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            Text(
                text = title,
                style = AppTypography.titleMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.fillMaxWidth()
                    .padding(top=6.dp)
            )
            IconButton(
                onClick = { expanded = !expanded },
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                ) {
                Icon(
                    imageVector = if (expanded)
                        Icons.Filled.ExpandLess
                    else
                        Icons.Filled.ExpandMore,
                    contentDescription = if (expanded) "Skjul" else "Utvid",

                )
            }
            if (expanded) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    textAlign = TextAlign.Center,
                    modifier = Modifier.padding(top = 4.dp)
                )


            }


        }
    }

}



