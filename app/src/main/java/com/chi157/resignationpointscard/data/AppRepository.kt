package com.chi157.resignationpointscard.data

import kotlinx.coroutines.flow.Flow

class AppRepository(
    private val appSettingsDao: AppSettingsDao,
    private val stampRecordDao: StampRecordDao
) {
    
    val settings: Flow<AppSettings?> = appSettingsDao.getSettings()
    val allStamps: Flow<List<StampRecord>> = stampRecordDao.getAllRecords()
    
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
        initializeSettings()
    }
}
