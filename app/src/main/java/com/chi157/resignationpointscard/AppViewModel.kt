package com.chi157.resignationpointscard

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.chi157.resignationpointscard.data.AppDatabase
import com.chi157.resignationpointscard.data.AppRepository
import com.chi157.resignationpointscard.data.AppSettings
import com.chi157.resignationpointscard.data.StampRecord
import com.chi157.resignationpointscard.data.TodoItem
import com.chi157.resignationpointscard.data.CommonReason
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import java.util.*
import kotlinx.coroutines.launch

class AppViewModel(application: Application) : AndroidViewModel(application) {
    
    private val repository: AppRepository
    
    private val _settings = MutableStateFlow<AppSettings?>(null)
    val settings: StateFlow<AppSettings?> = _settings.asStateFlow()
    
    private val _isLoading = MutableStateFlow(true)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()
    
    // 蓋章相關狀態
    private val _allStamps = MutableStateFlow<List<StampRecord>>(emptyList())
    val allStamps: StateFlow<List<StampRecord>> = _allStamps.asStateFlow()
    
    // 彩蛋計數器 (連續按「明天再來吧」的次數)
    private val _angryCounter = MutableStateFlow(0)
    val angryCounter: StateFlow<Int> = _angryCounter.asStateFlow()
    
    // 判斷今日是否已蓋章 (不包含彩蛋觸發後的狀態)
    private val _isStampedToday = MutableStateFlow(false)
    val isStampedToday: StateFlow<Boolean> = _isStampedToday.asStateFlow()
    
    // 待辦事項
    private val _allTodos = MutableStateFlow<List<TodoItem>>(emptyList())
    val allTodos: StateFlow<List<TodoItem>> = _allTodos.asStateFlow()
    
    // 常用原因
    private val _allCommonReasons = MutableStateFlow<List<CommonReason>>(emptyList())
    val allCommonReasons: StateFlow<List<CommonReason>> = _allCommonReasons.asStateFlow()
    
    init {
        val database = AppDatabase.getDatabase(application)
        repository = AppRepository(
            database.appSettingsDao(), 
            database.stampRecordDao(),
            database.todoDao(),
            database.commonReasonDao()
        )
        
        // 初始化設定
        viewModelScope.launch {
            repository.initializeSettings()
            
            // 合併設定與紀錄的監聽
            launch {
                repository.settings.collect { settings ->
                    _settings.value = settings
                    // 自動排程小工具刷新
                    settings?.let { 
                        com.chi157.resignationpointscard.widget.QuoteWidgetWorker.schedule(application, it.quoteRefreshRate)
                    }
                }
            }
            
            launch {
                repository.allStamps.collect { stamps ->
                    _allStamps.value = stamps
                    _isStampedToday.value = checkIfStampedToday(stamps)
                    _isLoading.value = false
                }
            }
            
            launch {
                repository.allTodos.collect { _allTodos.value = it }
            }
            
            launch {
                repository.allCommonReasons.collect { _allCommonReasons.value = it }
            }
        }
    }
    
    private fun checkIfStampedToday(stamps: List<StampRecord>): Boolean {
        if (stamps.isEmpty()) return false
        val today = Calendar.getInstance()
        val lastStamp = Calendar.getInstance().apply { 
            timeInMillis = stamps.first().dateMillis 
        }
        return today.get(Calendar.YEAR) == lastStamp.get(Calendar.YEAR) &&
               today.get(Calendar.DAY_OF_YEAR) == lastStamp.get(Calendar.DAY_OF_YEAR)
    }
    
    fun incrementAngryCounter() {
        _angryCounter.value += 1
    }
    
    fun resetAngryCounter() {
        _angryCounter.value = 0
    }
    
    fun addStamp(reason: String, isAngry: Boolean = false) {
        viewModelScope.launch {
            val settings = _settings.value ?: return@launch
            val stamps = _allStamps.value
            
            val lastCompletedIndex = settings.lastCompletedCardIndex
            val cardIndex = lastCompletedIndex + 1
            
            // 找出當前卡片已有的印章數量
            val stampsInCurrentCard = stamps.count { it.cardIndex == cardIndex }
            val targetStamps = settings.targetStamps.takeIf { it > 0 } ?: 20
            val position = stampsInCurrentCard + 1
            
            repository.addStamp(cardIndex, position, reason, isAngry, targetStamps)
            resetAngryCounter()
        }
    }
    
    fun addCommonReason(text: String) {
        viewModelScope.launch {
            // 避免重複
            val exists = _allCommonReasons.value.any { it.text == text }
            if (!exists) {
                repository.addCommonReason(CommonReason(text = text))
            } 
        }
    }
    
    fun updateCommonReason(reason: CommonReason) = viewModelScope.launch { repository.updateCommonReason(reason) }
    fun deleteCommonReason(reason: CommonReason) = viewModelScope.launch { repository.deleteCommonReason(reason) }
    
    fun incrementCommonReasonUsage(id: Int) = viewModelScope.launch { repository.incrementCommonReasonUsage(id) }
    
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
    
    fun saveTargetFund(fund: Long) = viewModelScope.launch { repository.updateTargetFund(fund) }
    fun saveCurrentFund(fund: Long) = viewModelScope.launch { repository.updateCurrentFund(fund) }
    fun toggleResumeReady(ready: Boolean) = viewModelScope.launch { repository.updateResumeReady(ready) }
    fun saveQuoteRefreshRate(rate: Int) = viewModelScope.launch { repository.updateQuoteRefreshRate(rate) }
    fun updateLastCompletedCardIndex(index: Int) = viewModelScope.launch { repository.updateLastCompletedCardIndex(index) }
    fun saveFundIncrementPresets(presets: String) = viewModelScope.launch { repository.updateFundIncrementPresets(presets) }
    fun saveWidgetColors(c1: String, c2: String, c3: String) = viewModelScope.launch { repository.updateWidgetColors(c1, c2, c3) }
    fun saveWidgetTextColors(tc1: String, tc2: String, tc3: String) = viewModelScope.launch { repository.updateWidgetTextColors(tc1, tc2, tc3) }
    
    fun addTodo(item: TodoItem) = viewModelScope.launch { repository.addTodo(item) }
    fun updateTodo(item: TodoItem) = viewModelScope.launch { repository.updateTodo(item) }
    fun deleteTodo(item: TodoItem) = viewModelScope.launch { repository.deleteTodo(item) }
    fun updateStamp(record: StampRecord) {
        viewModelScope.launch {
            val settings = _settings.value ?: return@launch
            if (!record.isLocked(settings.lastCompletedCardIndex)) {
                repository.updateStamp(record)
            }
        }
    }
    
    fun deleteStamp(record: StampRecord) {
        viewModelScope.launch {
            val settings = _settings.value ?: return@launch
            if (!record.isLocked(settings.lastCompletedCardIndex)) {
                repository.deleteStamp(record)
            }
        }
    }
    
    fun resetAllData() {
        viewModelScope.launch {
            repository.resetAllData()
        }
    }
}
