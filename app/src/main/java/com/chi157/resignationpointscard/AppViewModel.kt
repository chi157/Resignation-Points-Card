package com.chi157.resignationpointscard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chi157.resignationpointscard.data.AppDatabase
import com.chi157.resignationpointscard.data.AppRepository
import com.chi157.resignationpointscard.data.AppSettings
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: AppRepository
    
    private val _settings = MutableStateFlow<AppSettings?>(null)
    val settings: StateFlow<AppSettings?> = _settings.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(database.appSettingsDao())
        
        // 初始化設定
        viewModelScope.launch {
            repository.initializeSettings()
            repository.settings.collect { settings ->
                _settings.value = settings
                _isLoading.value = false
            }
        }
    }
    
    fun saveCompanyName(name: String) {
        viewModelScope.launch {
            repository.updateCompanyName(name)
        }
    }
    
    fun saveTheme(theme: CardTheme) {
        viewModelScope.launch {
            repository.updateTheme(theme.name)
        }
    }
    
    fun saveTargetStamps(stamps: Int) {
        viewModelScope.launch {
            repository.updateTargetStamps(stamps)
        }
    }
    
    fun completeOnboarding() {
        viewModelScope.launch {
            repository.completeOnboarding()
        }
    }
    
    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }
}
