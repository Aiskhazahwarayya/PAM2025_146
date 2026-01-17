package com.example.fhub.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.fhub.data.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    suspend fun insert(invoice: InvoiceEntity) : Long
    @Update
    suspend fun update(invoice: InvoiceEntity)
    @Delete
    suspend fun delete(invoice: InvoiceEntity)
    @Query("SELECT * FROM invoice WHERE idInvoice = :id")
    fun getInvoiceStream(id: Int): Flow<InvoiceEntity?>
    @Query("SELECT * FROM invoice WHERE idUser = :idUser AND status = :status ORDER BY dueDate ASC")
    fun getInvoiceByStatusStream(idUser: Int, status: String): Flow<List<InvoiceEntity>>
    @Query("SELECT * FROM invoice WHERE idUser = :idUser ORDER BY dueDate ASC")
    fun getAllInvoicesByUserStream(idUser: Int): Flow<List<InvoiceEntity>>
}