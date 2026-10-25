package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ScriptOutlineDao {
    @Query("SELECT * FROM script_outlines ORDER BY updatedAt DESC")
    fun getAllOutlines(): Flow<List<ScriptOutlineEntity>>

    @Query("SELECT * FROM script_outlines WHERE projectId = :projectId LIMIT 1")
    fun getOutlineForProject(projectId: Long): Flow<ScriptOutlineEntity?>

    @Query("SELECT * FROM script_outlines WHERE projectId = :projectId LIMIT 1")
    suspend fun getOutlineForProjectSync(projectId: Long): ScriptOutlineEntity?

    @Query("SELECT * FROM script_outlines WHERE id = :id")
    suspend fun getOutlineById(id: Long): ScriptOutlineEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertOutline(outline: ScriptOutlineEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(outlines: List<ScriptOutlineEntity>)

    @Update
    suspend fun updateOutline(outline: ScriptOutlineEntity)

    @Delete
    suspend fun deleteOutline(outline: ScriptOutlineEntity)

    @Query("DELETE FROM script_outlines WHERE projectId = :projectId")
    suspend fun deleteByProjectId(projectId: Long)
}
