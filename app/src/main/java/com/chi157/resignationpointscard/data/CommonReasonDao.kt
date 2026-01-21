package com.chi157.resignationpointscard.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface CommonReasonDao {
    @Query("SELECT * FROM common_reasons ORDER BY usageCount DESC")
    fun getAllCommonReasons(): Flow<List<CommonReason>>

    @Insert(onConflict = OnConflictStrategy.IGNORE)
    suspend fun insertCommonReason(reason: CommonReason)

    @Delete
    suspend fun deleteCommonReason(reason: CommonReason)
    
    @Query("UPDATE common_reasons SET usageCount = usageCount + 1 WHERE id = :id")
    suspend fun incrementUsageCount(id: Int)
}
