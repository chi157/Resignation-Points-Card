package com.chi157.resignationpointscard.data

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "common_reasons")
data class CommonReason(
    @PrimaryKey(autoGenerate = true) val id: Int = 0,
    val text: String,
    val usageCount: Int = 0 
)
