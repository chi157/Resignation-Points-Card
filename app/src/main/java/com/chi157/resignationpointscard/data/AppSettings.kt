package com.chi157.resignationpointscard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "app_settings")
data class AppSettings(
    @PrimaryKey val id: Int = 1, // 只會有一筆設定資料
    val companyName: String = "",
    val selectedTheme: String = "", // 預設為空，用戶選擇後填入
    val targetStamps: Int = 0, // 預設為 0
    val isOnboardingCompleted: Boolean = false,
    
    // 離職計畫相關
    val targetFund: Long = 0,
    val currentFund: Long = 0,
    val isResumeReady: Boolean = false,
    
    val lastCompletedCardIndex: Int = 0, // 紀錄已完成並按過「再給一次機會」的卡片索引
    
    // 小工具設定
    val quoteRefreshRate: Int = 1, // 單位：小時
    
    // 金額增量預設值 (存為逗點分隔字串，例如 "500,1000,3000")
    val fundIncrementPresets: String = "500,1000,3000",
    
    // 小工具自訂背景顏色 (Hex 格式)
    val widgetColor1: String = "#2C3E50",
    val widgetColor2: String = "#E74C3C",
    val widgetColor3: String = "#27AE60",
    
    // 小工具文字顏色 (每個背景對應一個文字顏色)
    val widgetTextColor1: String = "#FFFFFF", // 背景1的文字顏色
    val widgetTextColor2: String = "#FFFFFF", // 背景2的文字顏色
    val widgetTextColor3: String = "#FFFFFF"  // 背景3的文字顏色
)

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val startTimeMillis: Long? = null,
    val deadlineTimeMillis: Long? = null,
    val hasStartTimeTime: Boolean = true,
    val hasDeadlineTime: Boolean = true
)
