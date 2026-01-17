package com.example.fhub.repositori

import com.example.fhub.data.dao.InvoiceItemDao
import com.example.fhub.data.entity.InvoiceItemEntity
import kotlinx.coroutines.flow.Flow

interface InvoiceItemRepository {
    fun getItemsByInvoiceStream(idInvoice: Int): Flow<List<InvoiceItemEntity>>
    fun getItemStream(id: Int): Flow<InvoiceItemEntity?>
    suspend fun insertItem(item: InvoiceItemEntity)
    suspend fun deleteItem(item: InvoiceItemEntity)
    suspend fun updateItem(item: InvoiceItemEntity)
}

class OfflineInvoiceItemRepository(
    private val itemDao: InvoiceItemDao
): InvoiceItemRepository {
    override fun getItemsByInvoiceStream(idInvoice: Int): Flow<List<InvoiceItemEntity>> = itemDao.getItemsByInvoiceStream(idInvoice)
    override fun getItemStream(id: Int): Flow<InvoiceItemEntity?> = itemDao.getItemStream(id)
    override suspend fun insertItem(item: InvoiceItemEntity) = itemDao.insert(item)
    override suspend fun deleteItem(item: InvoiceItemEntity) = itemDao.delete(item)
    override suspend fun updateItem(item: InvoiceItemEntity) = itemDao.update(item)
}



