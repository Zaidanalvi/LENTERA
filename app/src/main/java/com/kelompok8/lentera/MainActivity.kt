package com.kelompok8.lentera

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.kelompok8.lentera.core.navigation.LenteraApp
import com.kelompok8.lentera.ui.theme.LENTERATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            LENTERATheme {
                LenteraApp()
            }
        }
    }
}
