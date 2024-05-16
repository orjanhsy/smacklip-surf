package no.uio.ifi.in2000.team8.ui.common.composables

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.Divider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import no.uio.ifi.in2000.team8.model.surfareas.SurfArea
import no.uio.ifi.in2000.team8.ui.theme.AppTypography
import com.mapbox.geojson.Point

/*
The SearchBar is a function which gives the user the ability to search for surf area. It is
extended to fit for utilization in both HomeScreen and MapScreen. Each screen require a separate
parameter to navigate to each desired destination. SearchBar takes in the parameter
"onZoomToLocation", which moves the map camera to the location of the search result in map.
There is no functionality to move the camera in the SearchBar-method itself which
adheres to object-oriented principles. SearchBar also takes in the parameter "onItemClick", which
navigates to SurfAreaScreen in HomeScreen.

The SearchBar is displayed as a OutlinedTextField, and the search results are presented in an
expanded LazyColumn which allows the user to scroll among the results, with a column representing
each surf area corresponding to the query provided in the search.
*/

@Composable
fun SearchBar(
    surfAreas: List<SurfArea>,
    onQueryChange: (String) -> Unit,
    isSearchActive: Boolean,
    onActiveChanged: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
    resultsColor: Color,
    onSearch: ((String) -> Unit)? = null,
    onZoomToLocation: ((Point) -> Unit)? = null, // for MapScreen which navigates to point in map
    onItemClick: (SurfArea) -> Unit // for HomeScreen which navigates to SurfAreaScreen
) {
    var searchQuery by remember { mutableStateOf("") }
    var expanded by remember { mutableStateOf(false) }
    val keyboardController = LocalSoftwareKeyboardController.current
    val focusManager = LocalFocusManager.current

    /* Defined "activeChanged" which is used to help manage behavior when the search bar becomes
    active or inactive. */
    val activeChanged: (Boolean) -> Unit = { active ->
        if (!active) {
            searchQuery = ""
            onQueryChange("")
        }
        onActiveChanged(active)
    }

    // This column contains both the search bar itself and the search results
    Column(modifier = modifier.clip(RoundedCornerShape(50.dp))) {
        // The search bar
        OutlinedTextField(
            modifier = modifier
                .padding(12.dp)
                .height(56.dp)
                .background(color = MaterialTheme.colorScheme.onPrimary, shape = RoundedCornerShape(50.dp))
                .fillMaxWidth(),
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
            placeholder = { Text(text = "Søk etter surfeområde", style = AppTypography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant) },
            leadingIcon = {
                Icon(
                    imageVector = Icons.Rounded.Search,
                    contentDescription = "Søkeikon",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            },
            /* Add icon to clear search when search is active. There is no icon when the search
            is inactive. */
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
                            contentDescription = "Angre søk"
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
        /* Filters surf areas based on whether the name of the location starts with the
        search query the user provides */
        val filteredSurfAreas =
            surfAreas.filter { it.locationName.startsWith(searchQuery, ignoreCase = true) }

        // The search results
        if (expanded && searchQuery.isNotEmpty() && filteredSurfAreas.isNotEmpty()) {
            LazyColumn(
                modifier = Modifier
                    .padding(start = 12.dp, top = 0.dp, end = 12.dp, bottom = 12.dp)
                    .background(resultsColor)
                    .fillMaxWidth(),
                contentPadding = PaddingValues(horizontal = 12.dp, vertical = 4.dp)
            ) {
                items(filteredSurfAreas) { surfArea ->
                    /* A column for each surf area which corresponds to the search query. It
                    navigates to a desired location on click */
                    Column(
                        modifier = Modifier.clickable {
                            searchQuery = ""
                            onQueryChange("")
                            onActiveChanged(false)
                            focusManager.clearFocus()
                            keyboardController?.hide()
                            onZoomToLocation?.invoke(Point.fromLngLat(surfArea.lon, surfArea.lat))
                            onItemClick(surfArea)
                        }
                    ) {
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
                                contentDescription = "Bilde av stranden",
                                modifier = Modifier.size(48.dp),
                                contentScale = ContentScale.Crop,
                                alignment = Alignment.CenterEnd
                            )
                        }
                        Divider(modifier = Modifier.padding(horizontal = 12.dp), color = Color.Gray)
                    }
                }
            }
        }
    }
}