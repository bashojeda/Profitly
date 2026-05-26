package com.example.profitly

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.example.profitly.ui.AppScreens
import com.example.profitly.ui.theme.ProfitlyTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        val container = AppContainer(this)
        setContent {
            ProfitlyTheme {
                AppScreens.ProfitlyApp(container)
            }
        }
    }
}