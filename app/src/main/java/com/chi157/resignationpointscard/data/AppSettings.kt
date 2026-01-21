package com.chi157.resignationpointscard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1, // 只會有一筆設定資料
    val companyName: String = "",
    val selectedTheme: String = "VACATION_MODE", // CLASSIC_RPG, SYSTEM_ERROR, VACATION_MODE
    val targetStamps: Int = 30, // 目標格數 (10, 20, 30)
    val isOnboardingCompleted: Boolean = false
)
