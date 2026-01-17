package com.example.fhub.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fhub.data.entity.KlienEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface KlienDao {
    @Insert
    suspend fun insert(klien: KlienEntity)
    @Update
    suspend fun update(klien: KlienEntity)
    @Delete
    suspend fun delete(klien: KlienEntity)
    @Query("SELECT COUNT(*) FROM project WHERE idKlien = :idKlien")
    suspend fun getProjectCountByKlien(idKlien: Int): Int
    @Query("SELECT * FROM klien WHERE idUser = :idUser ORDER BY namaLengkap ASC")
    fun getKlienByUserStream(idUser: Int): Flow<List<KlienEntity>>
    @Query("SELECT * FROM klien WHERE idKlien = :id")
    fun getKlienStream(id: Int): Flow<KlienEntity?>
    @Query("""
        SELECT * FROM klien 
        WHERE idUser = :idUser 
        AND (namaLengkap LIKE '%' || :searchQuery || '%' OR namaPerusahaan LIKE '%' || :searchQuery || '%')
        ORDER BY namaLengkap ASC
    """)
    fun searchKlienByUserStream(idUser: Int, searchQuery: String): Flow<List<KlienEntity>>
}