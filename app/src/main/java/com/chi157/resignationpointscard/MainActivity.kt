package com.chi157.resignationpointscard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import com.chi157.resignationpointscard.ui.theme.ResignationPointsCardTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            ResignationPointsCardTheme {
                Surface(
                    modifier = Modifier.fillMaxSize()
                ) {
                    val viewModel: AppViewModel = viewModel()
                    AppNavigation(viewModel = viewModel)
                }
            }
        }
    }
}
