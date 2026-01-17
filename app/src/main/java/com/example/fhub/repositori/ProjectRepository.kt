package com.example.fhub.repositori

import com.example.fhub.data.dao.ProjectDao
import com.example.fhub.data.entity.ProjectEntity
import kotlinx.coroutines.flow.Flow

interface ProjectRepository {
    fun getAllProjectsByUserStream(idUser: Int): Flow<List<ProjectEntity>>
    fun getProjectByStatusStream(idUser: Int, status: String): Flow<List<ProjectEntity>>
    fun getProjectStream(id: Int): Flow<ProjectEntity?>
    fun getUrgentProjectsStream(idUser: Int): Flow<List<ProjectEntity>>
    suspend fun insertProject(project: ProjectEntity)
    suspend fun deleteProject(project: ProjectEntity)
    suspend fun updateProject(project: ProjectEntity)
}

class OfflineProjectRepository(
    private val projectDao: ProjectDao
): ProjectRepository {
    override fun getAllProjectsByUserStream(idUser: Int): Flow<List<ProjectEntity>> = projectDao.getAllProjectByUserStream(idUser)
    override fun getProjectByStatusStream(idUser: Int, status: String): Flow<List<ProjectEntity>> = projectDao.getProjectByStatusStream(idUser, status)
    override fun getProjectStream(id: Int): Flow<ProjectEntity?> = projectDao.getProjectStream(id)
    override fun getUrgentProjectsStream(idUser: Int): Flow<List<ProjectEntity>> { return projectDao.getUrgentProjectsStream(idUser) }
    override suspend fun insertProject(project: ProjectEntity) = projectDao.insert(project)
    override suspend fun updateProject(project: ProjectEntity) = projectDao.update(project)
    override suspend fun deleteProject(project: ProjectEntity) {
        val relatedInvoices = projectDao.getInvoiceCountByProject(project.idProject) // [cite: 383]
        if (relatedInvoices > 0) {
            throw Exception("Proyek tidak bisa dihapus karena memiliki invoice terkait.") // [cite: 384, 408]
        }
        projectDao.delete(project)
    }
}