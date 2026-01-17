package com.example.fhub.data.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.example.fhub.data.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface InvoiceItemDao {
    @Insert
    suspend fun insert(item: InvoiceItemEntity)
    @Update
    suspend fun update(item: InvoiceItemEntity)
    @Delete
    suspend fun delete(item: InvoiceItemEntity)
    @Query("SELECT * FROM invoice_item WHERE idInvoice = :idInvoice")
    fun getItemsByInvoiceStream(idInvoice: Int): Flow<List<InvoiceItemEntity>>
    @Query("SELECT * FROM invoice_item WHERE idInvoiceItem = :id")
    fun getItemStream(id: Int): Flow<InvoiceItemEntity?>
}