package com.anhhoang.tipple

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.contentColorFor
import com.anhhoang.tipple.design.theme.TippleTheme
import com.anhhoang.tipple.navigation.TippleNavigation
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.serialization.json.JsonNull.content

@AndroidEntryPoint
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            TippleTheme {
                Surface {
                    TippleNavigation()
                }
            }
        }
    }
}
