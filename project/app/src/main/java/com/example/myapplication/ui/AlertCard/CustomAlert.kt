package com.example.myapplication.ui.AlertCard

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
    actionWithValue: ((T) -> Unit)?,
    action: (() -> Unit)?,
) {
    Column(
        modifier = Modifier.fillMaxSize(),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Card(
            modifier = Modifier.padding(8.dp),
            shape = RoundedCornerShape(35.dp)
        ) {
            Column(
                modifier = Modifier
                    .background(MaterialTheme.colorScheme.background)
                    .padding(16.dp),
                verticalArrangement = Arrangement.spacedBy(16.dp),
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(
                    title,
                    modifier = Modifier.fillMaxWidth(),
                    color = Color(0xFF47B0EC),
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
                    style = MaterialTheme.typography.titleLarge
                )

                Button(
                    onClick = {
                        showAlert.value = false
                        action?.invoke()
                    },
                    modifier = Modifier
                        .background(
                            color = Color(0xFF47B0EC),
                            shape = RoundedCornerShape(35.dp)
                        )
                        .height(55.dp)
                        .fillMaxWidth(),
                    colors = ButtonDefaults.buttonColors(
                        containerColor = Color.Transparent,
                    ),
                ) {
                    Text(
                        text = actionText,
                        fontWeight = FontWeight.SemiBold,
                        style = MaterialTheme.typography.titleMedium
                    )
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
            actionWithValue = null,
            action = null
        )
    }
}

