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
    
    @Query("DELETE FROM app_settings")
    suspend fun deleteAllSettings()
}
