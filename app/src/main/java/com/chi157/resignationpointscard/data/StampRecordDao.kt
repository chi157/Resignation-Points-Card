package com.chi157.resignationpointscard.data

import androidx.room.*
import kotlinx.coroutines.flow.Flow

@Dao
interface StampRecordDao {
    @Query("SELECT * FROM stamp_records ORDER BY dateMillis DESC")
    fun getAllRecords(): Flow<List<StampRecord>>

    @Query("SELECT * FROM stamp_records WHERE cardIndex = :cardIndex")
    fun getRecordsForCard(cardIndex: Int): Flow<List<StampRecord>>

    @Insert
    suspend fun insertRecord(record: StampRecord)

    @Update
    suspend fun updateRecord(record: StampRecord)

    @Delete
    suspend fun deleteRecord(record: StampRecord)

    @Query("DELETE FROM stamp_records")
    suspend fun deleteAllRecords()
    
    @Query("SELECT COUNT(*) FROM stamp_records")
    suspend fun getTotalStampsCount(): Int
}
