package com.anhhoang.tipple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.anhhoang.tipple.design.theme.TippleTheme
import com.anhhoang.tipple.navigation.TippleNavigation
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TippleTheme {
                TippleNavigation()
            }
        }
    }
}
