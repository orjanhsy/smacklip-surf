package com.example.myapplication.ui.AlertCard

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

@Composable
fun <T> CustomAlert(
    title: String,
    message: String,
    actionText: String,
    data: T?,
    showAlert: MutableState<Boolean>,
    //actionWithValue: ((T) -> Unit)? = null,
    action: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (showAlert.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
               // .background(Color.Black.copy(alpha = 0.5f))
                .padding(16.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = modifier,
                shape = RoundedCornerShape(35.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    Text(
                        title,
                        modifier = Modifier.fillMaxWidth(),
                        color = Color.Gray,
                        fontWeight = FontWeight.Bold,
                        style = MaterialTheme.typography.headlineMedium,
                        textAlign = TextAlign.Center
                    )

                    Text(
                        message,
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 8.dp),
                        fontSize = 19.sp,
                        textAlign = TextAlign.Center,
                        color = Color.Gray,
                        style = MaterialTheme.typography.titleLarge
                    )
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.5f) // Set width to half the available width
                            .height(60.dp) // Set a custom height
                            .padding(16.dp)
                            .background(color = Color.LightGray, shape = RoundedCornerShape(35.dp))
                            .clickable {
                                showAlert.value = false
                                action?.invoke()
                            }
                            .align(Alignment.CenterHorizontally) // Center the button horizontally
                    ) {
                        Text(
                            text = actionText,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.SemiBold,
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier.align(Alignment.Center) // Align text to the center vertically
                        )
                    }

                }
            }
        }
    }
}


@Preview
@Composable
fun PreviewCustomAlert() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Gray),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        CustomAlert(
            title = "Farevarsel",
            message = "STOOORM incoming, søk dekning søk dekning søk dekning søk dekning",
            actionText = "OK",
            data = null,
            showAlert = remember { mutableStateOf(true) },
            //actionWithValue = null,
            action = null
        )
    }
}

