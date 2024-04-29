package com.example.myapplication.ui.common.composables

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.example.myapplication.ui.theme.MyApplicationTheme

@Composable
fun ProgressIndicator(
    isDisplayed : Boolean
){
    if (isDisplayed) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(MaterialTheme.colorScheme.background) ,
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center,

            ) {
            CircularProgressIndicator(
                color = MaterialTheme.colorScheme.tertiary ,
                trackColor = MaterialTheme.colorScheme.surface,
                strokeWidth = 5.dp,
                strokeCap = StrokeCap.Round
            )
        }
    }

}


@Composable
@Preview
fun PreviewProgressIndicator(){
    MyApplicationTheme {
        ProgressIndicator(true)
    }
}
