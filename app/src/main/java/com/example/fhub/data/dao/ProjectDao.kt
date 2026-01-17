package com.example.fhub.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fhub.data.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface ProjectDao {
    @Insert
    suspend fun insert(project: ProjectEntity)
    @Update
    suspend fun update(project: ProjectEntity)
    @Delete
    suspend fun delete(project: ProjectEntity)
    @Query("SELECT COUNT(*) FROM invoice WHERE idProject = :idProject")
    suspend fun getInvoiceCountByProject(idProject: Int): Int
    @Query("SELECT * FROM project WHERE idProject = :id")
    fun getProjectStream(id: Int): Flow<ProjectEntity?>
    @Query("SELECT * FROM project WHERE idUser = :idUser AND status = :status ORDER BY deadline ASC")
    fun getProjectByStatusStream(idUser: Int, status: String): Flow<List<ProjectEntity>>
    @Query("SELECT * FROM project WHERE idUser = :idUser ORDER BY namaProject ASC")
    fun getAllProjectByUserStream(idUser: Int): Flow<List<ProjectEntity>>

    // Query untuk mendukung Dashboard: Proyek In Progress dengan deadline < 7 hari
    @Query("""
    SELECT * FROM project 
    WHERE idUser = :idUser AND status = 'In Progress' 
    AND (deadline - strftime('%s','now')*1000) < (7 * 24 * 60 * 60 * 1000)
    ORDER BY deadline ASC
""")
    fun getUrgentProjectsStream(idUser: Int): Flow<List<ProjectEntity>>
}