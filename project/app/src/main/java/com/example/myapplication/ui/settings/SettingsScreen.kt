package com.example.myapplication.ui.settings

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
import androidx.compose.material3.ExperimentalMaterial3Api
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
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavController
import com.example.myapplication.R
import com.example.myapplication.Settings
import com.example.myapplication.ui.common.composables.BottomBar
import com.example.myapplication.ui.theme.AppTheme


@OptIn(ExperimentalMaterial3Api::class)
//@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@Composable
fun SettingsScreen(settingsScreenViewModel: SettingsScreenViewModel, navController: NavController) {
    val settingsUiState by settingsScreenViewModel.settingsUiState.collectAsState()
    val isDarkThemeEnabled by settingsScreenViewModel.isDarkThemEnabled.collectAsState()


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
                                        .width(265.dp)
                                        .heightIn(min = 57.dp)
                                        .animateContentSize(),
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
                                            style = TextStyle(
                                                fontSize = 16.sp,
                                                fontFamily =
                                                FontFamily.Default,
                                                fontWeight = FontWeight(400),
                                                //color = Color(0xFF4D5E6F),
                                                textAlign = TextAlign.Center
                                            ),
                                            modifier = Modifier.fillMaxWidth()
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
                                                 modifier = Modifier.rotate(if (expandedThemeCard) 180f else 0f)

                                             )
                                         }

                                        if (expandedThemeCard) {
                                            Row(
                                                modifier = Modifier
                                                    .fillMaxWidth()
                                                    .wrapContentHeight(),
                                                horizontalArrangement = Arrangement.SpaceBetween
                                            ) {
                                                Text(text = if (isDarkThemeEnabled) "Switch for light mode" else "switch for dark mode")
                                                Switch(
                                                    checked = isDarkThemeEnabled,
                                                    onCheckedChange = { isChecked ->
                                                        settingsScreenViewModel.updateTheme(if (isChecked) Settings.Theme.DARK else Settings.Theme.LIGHT)
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
                                    content = "Beskrivelse"
                                )
                                Spacer(modifier = Modifier.height(14.dp))
                            }
                            item{
                                InformationCard(
                                    title = "Hvor henter vi data fra?", 
                                    content = "Beskrivelse"
                                )
                            }
                        }
                    }
            }
        }

    }

}

@Composable
fun InformationCard(title: String, content: String) {
    var expanded by remember { mutableStateOf(false) }
    Card(
        modifier = Modifier
            .width(265.dp)
            .heightIn(min = 57.dp)
            .animateContentSize(),
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(10.dp)
        ) {

            Text(
                text = title,
                style = TextStyle(
                    fontSize = 16.sp,
                    fontFamily =
                    FontFamily.Default,
                    fontWeight = FontWeight(400),
                    //color = Color(0xFF4D5E6F),
                    textAlign = TextAlign.Center
                ),
                modifier = Modifier.fillMaxWidth()
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
                    modifier = Modifier.rotate(if (expanded) 180f else 0f)

                )
            }
            //ekspandert innhold
            if (expanded) {
                Text(
                    text = content,
                    style = MaterialTheme.typography.bodySmall,
                    modifier = Modifier.padding(top = 4.dp)
                )


            }


        }
    }

}


