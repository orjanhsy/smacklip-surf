package com.example.myapplication.ui.splashscreen

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.tooling.preview.Preview
import com.example.myapplication.MainActivity
import com.example.myapplication.ui.theme.AppTheme
import kotlinx.coroutines.delay

/*
@SuppressLint("CustomSpashScreen")
class SplashActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            AppTheme{
                SplashScreen()
            }
        }
    }

    @Preview
    @Composable
    private fun SplashScreen(){
        LaunchedEffect(key1 = true) {
            delay(5000)
            startActivity(Intent(this@SplashActivity, MainActivity::class.java))
        }
    }
}
*/