package com.chi157.resignationpointscard.widget

import android.content.Context
import androidx.glance.appwidget.GlanceAppWidgetManager
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.work.*
import java.util.concurrent.TimeUnit

class QuoteWidgetWorker(
    private val context: Context,
    workerParams: WorkerParameters
) : CoroutineWorker(context, workerParams) {

    override suspend fun doWork(): Result {
        val manager = GlanceAppWidgetManager(context)
        val glanceIds = manager.getGlanceIds(QuoteWidget::class.java)
        
        glanceIds.forEach { glanceId ->
            updateAppWidgetState(context, glanceId) { prefs ->
                val currentIndex = prefs[QuoteWidget.indexKey] ?: 0
                val currentStyle = prefs[QuoteWidget.styleKey] ?: 0
                prefs[QuoteWidget.indexKey] = currentIndex + 1
                prefs[QuoteWidget.styleKey] = currentStyle + 1
            }
            QuoteWidget().update(context, glanceId)
        }
        
        // 如果是開發模式（30秒），則重新排程
        if (tags.contains(WORK_NAME_DEV)) {
            val workRequest = OneTimeWorkRequestBuilder<QuoteWidgetWorker>()
                .setInitialDelay(30, TimeUnit.SECONDS)
                .addTag(WORK_NAME_DEV)
                .build()

            WorkManager.getInstance(context).enqueueUniqueWork(
                WORK_NAME_DEV,
                ExistingWorkPolicy.REPLACE,
                workRequest
            )
        }
        
        return Result.success()
    }

    companion object {
        private const val WORK_NAME = "QuoteWidgetUpdateWork"
        private const val WORK_NAME_DEV = "QuoteWidgetUpdateWork_Dev"

        fun schedule(context: Context, intervalHours: Int) {
            // 取消所有現有工作
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_DEV)
            
            if (intervalHours == 0) {
                // 開發模式：30 秒間隔（使用 OneTimeWorkRequest 循環）
                val workRequest = OneTimeWorkRequestBuilder<QuoteWidgetWorker>()
                    .setInitialDelay(30, TimeUnit.SECONDS)
                    .addTag(WORK_NAME_DEV)
                    .build()

                WorkManager.getInstance(context).enqueueUniqueWork(
                    WORK_NAME_DEV,
                    ExistingWorkPolicy.REPLACE,
                    workRequest
                )
            } else {
                // 正常模式：使用小時間隔
                val workRequest = PeriodicWorkRequestBuilder<QuoteWidgetWorker>(
                    intervalHours.toLong(), TimeUnit.HOURS
                ).build()

                WorkManager.getInstance(context).enqueueUniquePeriodicWork(
                    WORK_NAME,
                    ExistingPeriodicWorkPolicy.UPDATE,
                    workRequest
                )
            }
        }
        
        fun cancel(context: Context) {
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME)
            WorkManager.getInstance(context).cancelUniqueWork(WORK_NAME_DEV)
        }
    }
}
