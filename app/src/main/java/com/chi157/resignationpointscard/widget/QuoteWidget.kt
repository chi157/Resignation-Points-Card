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
                "什麼是同事，同在一個群組卻不見他做事",
                "什麼是主管，主要是什麼都不管",
                "什麼是店長，在店時間不長",
                "上班前的我只是經濟有問題，上班後的我連精神都出問題",
                "我對公司的期待，與我的年資成反比",
                "小孩子才做選擇，成年人根本沒得選擇",
                "18歲心情不好可以翹課，25歲的你只能9點打卡",
                "我最大的不足就是餘額不足",
                "不要跟我談夢想，我的夢想就是不工作",
                "不要相信壓力會轉化為動力，你的壓力只會轉化為病歷",
                "成人的世界，除了長胖，其他都不容易",
                "我想打起精神來，卻不小心把它打死了。",
                "我的錢包就像顆洋蔥，每當打開它，我就會想哭",
                "公司說有提供三餐，但沒跟我說是老闆畫的餅",
                "我上班領的不是薪水，是精神賠償金。",
                "工作的態度，決定你肝的硬度。",
                "工作馬馬虎虎，上班馬爾第夫。",
                "我對公司最真誠的時刻，是準時打卡。",
                "早退不做牛馬，公司就是羅馬。",
                "開會隨便中離，就像我在巴黎。",
                "公司廁所大一粒，上班就像義大利。",
                "什麼叫「萬死不辭」，就是每天被氣死一萬次，仍然不辭職。",
                "心裡有無數句的「TMD」，到了嘴邊都成「好的」。",
                "就算你是仙女，下了凡也得靠自己。",
                "一個星期總有那麼六七天不想上班",
                "試著去理解那些你討厭的人，你會發現他們越看越討厭",
                "休假快樂嗎？只有很快但沒有樂",
                "我們都是以不變的薪水，因應萬變的物價",
                "準時下班需要的是勇氣，不是能力",
                "同事都是設計師，每個都準備設計我",
                "看鬧鐘是確定還能睡多久，不是想要起床",
                "我以為是來上班的，沒人跟我說還要會通靈",
                "坐牢還有減刑，上班卻只能加班",
                "我沒有失去熱情，是熱情先離職了。",
                "人生啊 拿得起放得下的只有筷子而已",
                "我只是來上班，你別想感動我，感動我的只有薪水跟獎金",
                "如果問我想選什麼工作？ 我會選擇不工作。",
                "職場就是專門在懲罰做事的人。",
                "上班時別跟我談夢想，我的夢想就是不上班",
                "上班就是不去會有經濟問題，去了會有精神問題",
                "老闆都說公司是個大家庭，那我明天不回家了",
                "薪水就好像月經一樣，一個月來一次，一個星期就沒了",
                "甘願做，歡喜受。不爽不會不要做。",
                "我對工作的愛，停在入職那天。",
                "成人的世界，除了長胖，其他都不容易",
                "不是我不上進，是上面沒空。",
                "每天都在消耗生命值，換取月底結算。",
                "我不是不會走，只是還沒準備好。",
                "工作讓我成長，也讓我想離開。",
                "我對未來的期待，需要一點勇氣。",
                "不是我不夠好，是位置不對。",
                "公司需要我，我需要錢，算是互相利用。",
                "我沒有不努力，內容物與封面不符。",
                "每天都在心裡遞辭呈，現實中打卡。",
                "人生就跟香菜一樣，什麼都沒做也會被別人討厭",
                "不要總是送年輕人一句話，年輕人需要的是一筆錢",
                "我對工作的忠誠，只維持到下班。",
                "不是我沒夢想，是夢想不付房租。",
                "太喜歡下班後的自由感",
                "只要心態好，公司就是峇里島",
                "我不是沒有底線，只是一直往下調。",
                "每天都在幫公司撐場面。",
                "這不是懶，是看透。",
                "我沒有迷失方向，我只是暫時被困住。",
                "不是我不想走，是還沒輪到我。",
                "公司說未來可期，但日期未定。",
                "我對工作的熱情，需要外力支援。",
                "我已經接受了我的平庸，但我主管不接受。",
                "上輩子作惡多端，這輩子早起上班",
                "錢=自由的話，那我的自由快沒了",
                "萬聖節我只想扮準時下班的上班族",
                "不想上班的時候就看一下自己的銀行餘額",
                "我對公司最大的貢獻，是沒當場走人。",
                "每天都在等一個下定決心的瞬間。",
                "小朋友才會選擇，大人都做牛做馬",
                "工作不是壓垮我，是慢慢磨。",
                "我還沒離職，但心已經在外面看房子了。",
                "這張集點卡，不是為了公司，是為了我自己。",
                "點數累積中，勇氣讀取中。",
                "總有一天，這些點數會兌換成自由。",
                "今天還在，代表我還撐得住。",
                "離開不是失敗，是結算。"
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
