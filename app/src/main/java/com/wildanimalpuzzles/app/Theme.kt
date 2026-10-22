package com.wildanimalpuzzles.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            MaterialTheme(
                colorScheme = androidx.compose.material3.lightColorScheme(
                    primary = androidx.compose.ui.graphics.Color(0xFF8B5A2B),
                    secondary = androidx.compose.ui.graphics.Color(0xFFE6C594),
                    background = androidx.compose.ui.graphics.Color(0xFFF4E8D1),
                    surface = androidx.compose.ui.graphics.Color(0xFFFFF8DC)
                )
            ) {
                AppContent()
            }
        }
    }
}

@Composable
fun WildAnimalPuzzlesTheme(content: @Composable () -> Unit) {
    MaterialTheme(
        colorScheme = androidx.compose.material3.lightColorScheme(
            primary = androidx.compose.ui.graphics.Color(0xFF8B5A2B),
            secondary = androidx.compose.ui.graphics.Color(0xFFE6C594),
            background = androidx.compose.ui.graphics.Color(0xFFF4E8D1),
            surface = androidx.compose.ui.graphics.Color(0xFFFFF8DC)
        ),
        content = content
    )
}
