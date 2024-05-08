package com.example.myapplication.ui.common.composables

import android.annotation.SuppressLint
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LocationOn
import androidx.compose.material3.BadgedBox
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.myapplication.ui.theme.AppTheme

data class BottomNavigationItem(
    val title: String,
    val route: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@SuppressLint("UnusedMaterial3ScaffoldPaddingParameter")
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun BottomBar(navController: NavController) {
    val navBackStackEntry by navController.currentBackStackEntryAsState()
    val currentDestination = navBackStackEntry?.destination?.route

    val items = listOf(
        BottomNavigationItem(
            title = "Hjem",
            route = "HomeScreen",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Default.Home
        ),
        BottomNavigationItem(
            title = "Utforsk",
            route = "MapScreen",
            selectedIcon = Icons.Filled.LocationOn,
            unselectedIcon = Icons.Default.LocationOn
        ),
        BottomNavigationItem(
            title = "Info",
            route = "SettingsScreen",
            selectedIcon = Icons.Filled.Info,
            unselectedIcon = Icons.Default.Info
        ),
    )
    NavigationBar {
        items.forEach { item->
            val isSelected = item.route == currentDestination
            NavigationBarItem(
                selected = isSelected,
                onClick = {
                    if (!isSelected) {
                        navController.navigate(item.route)
                    }
                },
                label = {
                    Text(text = item.title)
                },
                icon = {
                    BadgedBox(
                        badge = {

                        }) {
                        Icon(
                            imageVector = if(isSelected){
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



@Preview(showBackground = true)
@Composable
private fun PreviewBottomBar() {
    AppTheme {
        BottomBar(navController = rememberNavController())
    }
}
