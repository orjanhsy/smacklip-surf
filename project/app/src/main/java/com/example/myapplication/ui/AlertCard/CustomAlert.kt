package com.example.myapplication.ui.AlertCard

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.R
import com.example.myapplication.ui.theme.AppTypography

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
                .padding(10.dp),
            contentAlignment = Alignment.Center
        ) {
            Card(
                modifier = modifier
                    .padding(16.dp),
                shape = RoundedCornerShape(15.dp),
                border = BorderStroke(1.5.dp, Color.LightGray)

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
                            .size(100.dp)
                            .align(Alignment.CenterHorizontally)
                            .padding(12.dp, top = 12.dp)
                    )
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.Center,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Text(
                            text = message,
                            modifier = Modifier
                                .width(200.dp)
                                .padding(vertical = 8.dp),
                            style = AppTypography.titleMedium,
                            textAlign = TextAlign.Center
                        )
                    }
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.75f)
                            //.padding(16.dp)
                            .clickable {
                                showAlert.value = false
                                action?.invoke()
                            }
                            .align(Alignment.CenterHorizontally)
                    ) {
                        Text(
                            text = actionText,
                            style = AppTypography.titleLarge,
                            modifier = Modifier
                                .align(Alignment.Center)
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
            warningIcon = R.drawable.icon_warning_orange,
            data = null,
            showAlert = remember { mutableStateOf(true) },
            //actionWithValue = null,
            action = null
        )
    }
}

