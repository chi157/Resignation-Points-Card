package com.chi157.resignationpointscard.data

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val appSettingsDao: AppSettingsDao,
    private val stampRecordDao: StampRecordDao,
    private val todoDao: TodoDao
) {
    
    val settings: Flow<AppSettings?> = appSettingsDao.getSettings()
    val allStamps: Flow<List<StampRecord>> = stampRecordDao.getAllRecords()
    val allTodos: Flow<List<TodoItem>> = todoDao.getAllTodos()
    
    // 計畫相關
    suspend fun updateTargetFund(fund: Long) = appSettingsDao.updateTargetFund(fund)
    suspend fun updateCurrentFund(fund: Long) = appSettingsDao.updateCurrentFund(fund)
    suspend fun updateResumeReady(ready: Boolean) = appSettingsDao.updateResumeReady(ready)
    
    // 待辦事項相關
    suspend fun addTodo(item: TodoItem) = todoDao.insertTodo(item)
    suspend fun updateTodo(item: TodoItem) = todoDao.updateTodo(item)
    suspend fun deleteTodo(item: TodoItem) = todoDao.deleteTodo(item)
    
    suspend fun addStamp(cardIndex: Int, position: Int, reason: String, isAngry: Boolean) {
        val record = StampRecord(
            cardIndex = cardIndex,
            stampPosition = position,
            dateMillis = System.currentTimeMillis(),
            reason = reason,
            isAngry = isAngry
        )
        stampRecordDao.insertRecord(record)
    }
    
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
        initializeSettings()
    }
}
