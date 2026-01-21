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
    val quoteRefreshRate: Int = 1 // 單位：小時
)

@Entity(tableName = "todo_items")
data class TodoItem(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val content: String,
    val isDone: Boolean = false,
    val createdAt: Long = System.currentTimeMillis(),
    val startTimeMillis: Long? = null,
    val deadlineTimeMillis: Long? = null
)
