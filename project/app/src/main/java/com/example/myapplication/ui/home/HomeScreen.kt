package com.example.myapplication.ui.home

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp

@Composable
fun HomeScreen() {
    SurfAreaCard()
}

@Composable
fun SurfAreaCard() {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxHeight()
                .padding(16.dp)
            ,
            horizontalAlignment = Alignment.Start,
        ) {
            Row {
                Text(
                    text = "Hoddevik",
                    fontWeight = FontWeight.Bold
                )
            }
            Row {
                Text(
                    text = "Wind"
                )
            }

            Row {
                Text(
                    text = "Wave height"
                )
            }
        }
        Column {
            Text("hei")
        }
    }
}

@Preview(showBackground = true)
@Composable
private fun PreviewSurfAreaCard() {
    SurfAreaCard()
}