package com.example.myapplication.ui.commonComponents

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.ui.theme.MyApplicationTheme
import com.example.myapplication.ui.today.TodayScreen

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(){
    var selectedItemIndex by rememberSaveable {
        mutableStateOf(0)
    }

    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Default.Home
        ),
        BottomNavigationItem(
            title = "Explore",
            selectedIcon = Icons.Filled.LocationOn,
            unselectedIcon = Icons.Default.LocationOn
        ),
        BottomNavigationItem(
            title = "Settings",
            selectedIcon = Icons.Filled.Settings,
            unselectedIcon = Icons.Default.Settings
        ),
    )
    Scaffold(
        bottomBar = {
            NavigationBar {
                items.forEachIndexed{index, item->
                    NavigationBarItem(
                        selected = selectedItemIndex == index,
                        onClick = {
                                  selectedItemIndex = index
                            // her man bruker navController

                        },
                        label = {
                                Text(text = item.title)
                        },
                        icon = {
                            BadgedBox(
                                badge = {

                                }) {
                                Icon(
                                    imageVector = if(index == selectedItemIndex){
                                        item.selectedIcon }
                                    else item.unselectedIcon
                                            ,
                                    contentDescription = item.title
                                )
                            }
                        }
                    )
                }
            }
        }
    ) {

    }
}



@Preview(showBackground = true)
@Composable
private fun PreviewBottomBar() {
    MyApplicationTheme {
        BottomBar()
    }
}
