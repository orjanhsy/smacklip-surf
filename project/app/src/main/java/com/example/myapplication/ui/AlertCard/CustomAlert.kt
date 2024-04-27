package com.example.myapplication.ui.AlertCard

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.myapplication.R

@Composable
fun <T> CustomAlert(
    title: String,
    message: String,
    actionText: String,
    warningIcon: Int,
    data: T?,
    showAlert: MutableState<Boolean>,
    action: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    if (showAlert.value) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(16.dp), // Outer padding to ensure Card shadow is visible
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = modifier
                    .shadow(elevation = 4.dp, shape = RoundedCornerShape(35.dp))
                    .padding(16.dp), // Inner padding for content inside the card
                shape = RoundedCornerShape(35.dp)
            ) {
                Column(
                    modifier = Modifier
                        .background(MaterialTheme.colorScheme.background)
                        .padding(16.dp)
                ) {
                    Image(
                        painter = painterResource(id = warningIcon),
                        contentDescription = "Image",
                        modifier = Modifier
                            .size(86.dp)
                            .align(Alignment.CenterHorizontally) // Centering the image horizontally
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
                            .fillMaxWidth(0.75f) // Increased width to make the button larger
                            .padding(16.dp)
                            .clickable {
                                showAlert.value = false
                                action?.invoke()
                            }
                            .align(Alignment.CenterHorizontally) // Centering the button horizontally
                    ) {
                        Text(
                            text = actionText,
                            color = Color.DarkGray,
                            fontWeight = FontWeight.Bold, // Made the font weight bolder
                            fontSize = 20.sp, // Increased font size
                            style = MaterialTheme.typography.titleMedium,
                            modifier = Modifier
                                .align(Alignment.Center)
                                .padding(12.dp) // Increased padding for a bigger button feel
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
            warningIcon = R.drawable.icon_awareness_yellow_outlined,
            data = null,
            showAlert = remember { mutableStateOf(true) },
            //actionWithValue = null,
            action = null
        )
    }
}

