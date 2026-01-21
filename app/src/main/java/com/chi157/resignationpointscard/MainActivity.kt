package com.chi157.resignationpointscard

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Button
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
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
                    // 呼叫設定畫面
                    // 這裡呼叫 SetupScreen，它會自動去引用 SetupScreen.kt 裡面的定義
                    SetupScreen(onConfirm = { companyName ->
                        println("公司名稱已輸入: $companyName")
                    })
                }
            }
        }
    }
}

