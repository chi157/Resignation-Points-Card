package com.chi157.resignationpointscard.widget

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.GlanceAppWidgetReceiver
import androidx.glance.appwidget.provideContent
import androidx.glance.background
import androidx.glance.layout.*
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import androidx.glance.unit.ColorProvider
import androidx.glance.appwidget.state.updateAppWidgetState
import androidx.glance.state.PreferencesGlanceStateDefinition
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.glance.currentState
import androidx.glance.action.clickable
import androidx.glance.action.ActionParameters
import androidx.glance.appwidget.action.ActionCallback
import androidx.glance.appwidget.action.actionRunCallback

class QuoteWidget : GlanceAppWidget() {

    override val stateDefinition = PreferencesGlanceStateDefinition

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        // 取得資料庫單一實例
        val db = com.chi157.resignationpointscard.data.AppDatabase.getDatabase(context)
        val settings = db.appSettingsDao().getSettingsOnce()
        
        provideContent {
            val prefs = currentState<Preferences>()
            val index = prefs[indexKey] ?: 0
            val styleIdx = prefs[styleKey] ?: 0
            
            val quotes = listOf(
                "休假快樂嗎?\n只有很快但沒有樂",
                "恭喜你又過了一天，\n離職又更近了",
                "工作的苦是一時的，\n離職的甜是永遠的",
                "雖然薪水是買斷你的意志，\n但買不走你的靈魂",
                "每天叫醒我的不是夢想，\n是沒錢造成的窮緊張",
                "如果不能準時下班，\n那準時上班也就沒意義了",
                "老闆說公司是大家的，\n發財時卻是他一個人的",
                "離職不需要理由，\n留下才需要理由"
            )
            
            val quote = quotes[index % quotes.size]
            
            // 取得自訂顏色
            val c1 = settings?.widgetColor1 ?: "#2C3E50"
            val c2 = settings?.widgetColor2 ?: "#E74C3C"
            val c3 = settings?.widgetColor3 ?: "#27AE60"
            val customColors = listOf(c1, c2, c3)
            
            // 取得文字顏色（每個背景對應一個文字顏色）
            val tc1 = settings?.widgetTextColor1 ?: "#FFFFFF"
            val tc2 = settings?.widgetTextColor2 ?: "#FFFFFF"
            val tc3 = settings?.widgetTextColor3 ?: "#FFFFFF"
            val customTextColors = listOf(tc1, tc2, tc3)
            
            WidgetContent(quote, styleIdx, customColors, customTextColors)
        }
    }

    @Composable
    private fun WidgetContent(quote: String, styleIdx: Int, colors: List<String>, textColors: List<String>) {
        // 定義樣式
        val backgrounds = colors.map { hex ->
            try {
                ColorProvider(Color(android.graphics.Color.parseColor(hex)))
            } catch (e: Exception) {
                ColorProvider(Color(0xFF2C3E50))
            }
        }
        
        val textColorProviders = textColors.map { hex ->
            try {
                ColorProvider(Color(android.graphics.Color.parseColor(hex)))
            } catch (e: Exception) {
                ColorProvider(Color.White)
            }
        }
        
        val bg = backgrounds[styleIdx % backgrounds.size]
        val textColor = textColorProviders[styleIdx % textColorProviders.size]

        Column(
            modifier = GlanceModifier
                .fillMaxSize()
                .background(bg)
                .padding(12.dp)
                .clickable(actionRunCallback<UpdateQuoteAction>()),
            verticalAlignment = Alignment.CenterVertically,
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "離職語錄",
                style = TextStyle(
                    color = textColor,
                    fontSize = 12.sp,
                    fontWeight = FontWeight.Bold
                ),
                modifier = GlanceModifier.padding(bottom = 8.dp)
            )
            
            Box(
                modifier = GlanceModifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Text(
                    text = quote,
                    style = TextStyle(
                        color = textColor,
                        fontSize = 15.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = androidx.glance.text.TextAlign.Center
                    )
                )
            }
        }
    }

    companion object {
        val indexKey = intPreferencesKey("quote_index")
        val styleKey = intPreferencesKey("style_index")
    }
}

class UpdateQuoteAction : ActionCallback {
    override suspend fun onAction(context: Context, glanceId: GlanceId, parameters: ActionParameters) {
        updateAppWidgetState(context, glanceId) { prefs ->
            val currentIndex = prefs[QuoteWidget.indexKey] ?: 0
            val currentStyle = prefs[QuoteWidget.styleKey] ?: 0
            prefs[QuoteWidget.indexKey] = currentIndex + 1
            prefs[QuoteWidget.styleKey] = currentStyle + 1
        }
        QuoteWidget().update(context, glanceId)
    }
}

class QuoteWidgetReceiver : GlanceAppWidgetReceiver() {
    override val glanceAppWidget: GlanceAppWidget = QuoteWidget()
}
