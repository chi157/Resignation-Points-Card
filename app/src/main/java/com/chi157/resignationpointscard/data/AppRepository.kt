package com.chi157.resignationpointscard.data

import kotlinx.coroutines.flow.Flow

class AppRepository(private val appSettingsDao: AppSettingsDao) {
    
    val settings: Flow<AppSettings?> = appSettingsDao.getSettings()
    
    suspend fun getSettingsOnce(): AppSettings? {
        return appSettingsDao.getSettingsOnce()
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
        initializeSettings()
    }
}
