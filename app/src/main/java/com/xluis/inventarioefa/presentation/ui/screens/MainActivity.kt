package com.xluis.inventarioefa.presentation.ui.screens

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.xluis.inventarioefa.presentation.ui.navigation.NavigationWrapper
import com.xluis.inventarioefa.presentation.ui.theme.InventarioEFATheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            InventarioEFATheme {
                NavigationWrapper()
            }
        }
    }
}


