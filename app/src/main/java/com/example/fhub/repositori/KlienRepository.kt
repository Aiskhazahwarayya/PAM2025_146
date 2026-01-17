package com.example.fhub.repositori

import com.example.fhub.data.dao.KlienDao
import com.example.fhub.data.entity.KlienEntity
import kotlinx.coroutines.flow.Flow

interface KlienRepository {
    fun getKlienByUserStream(idUser: Int): Flow<List<KlienEntity>>
    fun getKlienStream(id: Int): Flow<KlienEntity?>
    suspend fun insertKlien(klien: KlienEntity)
    suspend fun deleteKlien(klien: KlienEntity)
    suspend fun updateKlien(klien: KlienEntity)
    fun searchKlienStream(idUser: Int, query: String): Flow<List<KlienEntity>>
}

class OfflineKlienRepository(
    private val klienDao: KlienDao
): KlienRepository {
    override fun getKlienByUserStream(idUser: Int): Flow<List<KlienEntity>> = klienDao.getKlienByUserStream(idUser)
    override fun getKlienStream(id: Int): Flow<KlienEntity?> = klienDao.getKlienStream(id)
    override suspend fun insertKlien(klien: KlienEntity) = klienDao.insert(klien)
    override suspend fun updateKlien(klien: KlienEntity) = klienDao.update(klien)
    override suspend fun deleteKlien(klien: KlienEntity) {
        val relatedProjects = klienDao.getProjectCountByKlien(klien.idKlien) // [cite: 333]
        if (relatedProjects > 0) {
            throw Exception("Klien tidak bisa dihapus karena masih memiliki proyek aktif.") // [cite: 335]
        }
        klienDao.delete(klien)
    }
    override fun searchKlienStream(idUser: Int, query: String): Flow<List<KlienEntity>> { return klienDao.searchKlienByUserStream(idUser, "$query") }
}