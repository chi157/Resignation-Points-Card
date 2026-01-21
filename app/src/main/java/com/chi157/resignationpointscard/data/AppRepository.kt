package com.chi157.resignationpointscard.data

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val appSettingsDao: AppSettingsDao,
    private val stampRecordDao: StampRecordDao,
    private val todoDao: TodoDao,
    private val commonReasonDao: CommonReasonDao
) {
    
    val settings: Flow<AppSettings?> = appSettingsDao.getSettings()
    val allStamps: Flow<List<StampRecord>> = stampRecordDao.getAllRecords()
    val allTodos: Flow<List<TodoItem>> = todoDao.getAllTodos()
    val allCommonReasons: Flow<List<CommonReason>> = commonReasonDao.getAllCommonReasons()
    
    // 計畫相關
    suspend fun updateTargetFund(fund: Long) = appSettingsDao.updateTargetFund(fund)
    suspend fun updateCurrentFund(fund: Long) = appSettingsDao.updateCurrentFund(fund)
    suspend fun updateResumeReady(ready: Boolean) = appSettingsDao.updateResumeReady(ready)
    suspend fun updateQuoteRefreshRate(rate: Int) = appSettingsDao.updateQuoteRefreshRate(rate)
    suspend fun updateLastCompletedCardIndex(index: Int) = appSettingsDao.updateLastCompletedCardIndex(index)
    suspend fun updateFundIncrementPresets(presets: String) = appSettingsDao.updateFundIncrementPresets(presets)
    suspend fun updateWidgetColors(c1: String, c2: String, c3: String) = appSettingsDao.updateWidgetColors(c1, c2, c3)
    suspend fun updateWidgetTextColors(tc1: String, tc2: String, tc3: String) = appSettingsDao.updateWidgetTextColors(tc1, tc2, tc3)
    
    // 待辦事項相關
    suspend fun addTodo(item: TodoItem) = todoDao.insertTodo(item)
    suspend fun updateTodo(item: TodoItem) = todoDao.updateTodo(item)
    suspend fun deleteTodo(item: TodoItem) = todoDao.deleteTodo(item)

    // 常用原因相關
    suspend fun addCommonReason(reason: CommonReason) = commonReasonDao.insertCommonReason(reason)
    suspend fun updateCommonReason(reason: CommonReason) = commonReasonDao.updateCommonReason(reason)
    suspend fun deleteCommonReason(reason: CommonReason) = commonReasonDao.deleteCommonReason(reason)
    suspend fun incrementCommonReasonUsage(id: Int) = commonReasonDao.incrementUsageCount(id)
    suspend fun deleteAllCommonReasons() = commonReasonDao.deleteAllCommonReasons()
    
    suspend fun addStamp(cardIndex: Int, position: Int, reason: String, isAngry: Boolean, cardCapacity: Int) {
        val record = StampRecord(
            cardIndex = cardIndex,
            stampPosition = position,
            cardCapacity = cardCapacity,
            dateMillis = System.currentTimeMillis(),
            reason = reason,
            isAngry = isAngry
        )
        stampRecordDao.insertRecord(record)
    }

    suspend fun updateStamp(record: StampRecord) = stampRecordDao.updateRecord(record)
    suspend fun deleteStamp(record: StampRecord) = stampRecordDao.deleteRecord(record)
    
    suspend fun initializeSettings() {
        val existing = appSettingsDao.getSettingsOnce()
        if (existing == null) {
            appSettingsDao.insertSettings(AppSettings())
        }
    }
    
    suspend fun updateCompanyName(name: String) {
        appSettingsDao.updateCompanyName(name)
    }
    
    suspend fun updateTheme(theme: String) {
        appSettingsDao.updateTheme(theme)
    }
    
    suspend fun updateTargetStamps(stamps: Int) {
        appSettingsDao.updateTargetStamps(stamps)
    }
    
    suspend fun completeOnboarding() {
        appSettingsDao.updateOnboardingCompleted(true)
    }
    
    suspend fun resetAllData() {
        appSettingsDao.deleteAllSettings()
        stampRecordDao.deleteAllRecords()
        todoDao.deleteAllTodos()
        commonReasonDao.deleteAllCommonReasons()
        initializeSettings()
    }
}
