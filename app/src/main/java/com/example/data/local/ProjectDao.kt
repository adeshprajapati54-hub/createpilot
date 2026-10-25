package com.example.data.local

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Query("SELECT * FROM creator_projects ORDER BY updatedAt DESC")
    fun getAllProjects(): Flow<List<ProjectEntity>>

    @Query("SELECT * FROM creator_projects ORDER BY updatedAt DESC")
    suspend fun getAllProjectsSync(): List<ProjectEntity>

    @Query("SELECT * FROM creator_projects WHERE id = :id")
    fun getProjectById(id: Long): Flow<ProjectEntity?>

    @Query("SELECT * FROM creator_projects WHERE id = :id")
    suspend fun getProjectByIdSync(id: Long): ProjectEntity?

    @Query("SELECT * FROM creator_projects WHERE status = :status ORDER BY updatedAt DESC")
    fun getProjectsByStatus(status: String): Flow<List<ProjectEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertProject(project: ProjectEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(projects: List<ProjectEntity>)

    @Update
    suspend fun updateProject(project: ProjectEntity)

    @Delete
    suspend fun deleteProject(project: ProjectEntity)

    @Query("DELETE FROM creator_projects WHERE id = :id")
    suspend fun deleteProjectById(id: Long)

    @Query("SELECT COUNT(*) FROM creator_projects")
    suspend fun getCount(): Int
}
