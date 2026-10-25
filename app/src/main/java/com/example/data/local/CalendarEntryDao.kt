package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface CalendarEntryDao {
    @Query("SELECT * FROM calendar_entries ORDER BY scheduledDate ASC, updatedAt DESC")
    fun getAllCalendarEntries(): Flow<List<CalendarEntryEntity>>

    @Query("SELECT * FROM calendar_entries WHERE status = :status ORDER BY scheduledDate ASC")
    fun getEntriesByStatus(status: String): Flow<List<CalendarEntryEntity>>

    @Query("SELECT * FROM calendar_entries WHERE scheduledDate = :date")
    fun getEntriesForDate(date: String): Flow<List<CalendarEntryEntity>>

    @Query("SELECT * FROM calendar_entries WHERE projectId = :projectId LIMIT 1")
    fun getEntryForProject(projectId: Long): Flow<CalendarEntryEntity?>

    @Query("SELECT * FROM calendar_entries WHERE projectId = :projectId LIMIT 1")
    suspend fun getEntryForProjectSync(projectId: Long): CalendarEntryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertEntry(entry: CalendarEntryEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entries: List<CalendarEntryEntity>)

    @Update
    suspend fun updateEntry(entry: CalendarEntryEntity)

    @Delete
    suspend fun deleteEntry(entry: CalendarEntryEntity)

    @Query("DELETE FROM calendar_entries WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM calendar_entries WHERE projectId = :projectId")
    suspend fun deleteByProjectId(projectId: Long)

    @Query("SELECT COUNT(*) FROM calendar_entries")
    suspend fun getCount(): Int
}
