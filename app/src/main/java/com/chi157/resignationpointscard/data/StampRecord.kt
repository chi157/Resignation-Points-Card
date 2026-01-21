package com.chi157.resignationpointscard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "stamp_records")
data class StampRecord(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val cardIndex: Int, // 屬於第幾張卡片
    val stampPosition: Int, // 在卡片中的位置 (1 ~ targetStamps)
    val cardCapacity: Int, // 蓋章時這張卡片的分度 (例如 20, 30)
    val dateMillis: Long, // 蓋章時間
    val reason: String, // 蓋章原因
    val isAngry: Boolean = false // 是否觸發了「真的很生氣」彩蛋
) {
    fun isLocked(lastCompletedCardIndex: Int): Boolean {
        return cardIndex <= lastCompletedCardIndex
    }
}
