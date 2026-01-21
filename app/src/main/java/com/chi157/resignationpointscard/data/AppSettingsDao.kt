package com.chi157.resignationpointscard.data

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface AppSettingsDao {
    @Query("SELECT * FROM app_settings WHERE id = 1")
    fun getSettings(): Flow<AppSettings?>
    
    @Query("SELECT * FROM app_settings WHERE id = 1")
    suspend fun getSettingsOnce(): AppSettings?
    
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertSettings(settings: AppSettings)
    
    @Update
    suspend fun updateSettings(settings: AppSettings)
    
    @Query("UPDATE app_settings SET companyName = :name WHERE id = 1")
    suspend fun updateCompanyName(name: String)
    
    @Query("UPDATE app_settings SET selectedTheme = :theme WHERE id = 1")
    suspend fun updateTheme(theme: String)
    
    @Query("UPDATE app_settings SET targetStamps = :stamps WHERE id = 1")
    suspend fun updateTargetStamps(stamps: Int)
    
    @Query("UPDATE app_settings SET isOnboardingCompleted = :completed WHERE id = 1")
    suspend fun updateOnboardingCompleted(completed: Boolean)
    
    @Query("UPDATE app_settings SET targetFund = :fund WHERE id = 1")
    suspend fun updateTargetFund(fund: Long)

    @Query("UPDATE app_settings SET currentFund = :fund WHERE id = 1")
    suspend fun updateCurrentFund(fund: Long)

    @Query("UPDATE app_settings SET isResumeReady = :ready WHERE id = 1")
    suspend fun updateResumeReady(ready: Boolean)

    @Query("UPDATE app_settings SET quoteRefreshRate = :rate WHERE id = 1")
    suspend fun updateQuoteRefreshRate(rate: Int)
    
    @Query("UPDATE app_settings SET lastCompletedCardIndex = :index WHERE id = 1")
    suspend fun updateLastCompletedCardIndex(index: Int)
    
    @Query("UPDATE app_settings SET fundIncrementPresets = :presets WHERE id = 1")
    suspend fun updateFundIncrementPresets(presets: String)
    
    @Query("UPDATE app_settings SET widgetColor1 = :c1, widgetColor2 = :c2, widgetColor3 = :c3 WHERE id = 1")
    suspend fun updateWidgetColors(c1: String, c2: String, c3: String)
    
    // 待辦事項相關
    @Query("UPDATE app_settings SET widgetTextColor1 = :tc1, widgetTextColor2 = :tc2, widgetTextColor3 = :tc3 WHERE id = 1")
    suspend fun updateWidgetTextColors(tc1: String, tc2: String, tc3: String)
    
    @Query("DELETE FROM app_settings")
    suspend fun deleteAllSettings()
}
