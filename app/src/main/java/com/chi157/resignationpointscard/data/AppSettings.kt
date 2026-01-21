package com.chi157.resignationpointscard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1, // 只會有一筆設定資料
    val companyName: String = "",
    val selectedTheme: String = "", // 預設為空，用戶選擇後填入
    val targetStamps: Int = 0, // 預設為 0
    val isOnboardingCompleted: Boolean = false
)
