package com.example.fhub.repositori

import com.example.fhub.data.dao.InvoiceDao
import com.example.fhub.data.entity.InvoiceEntity
import kotlinx.coroutines.flow.Flow

interface InvoiceRepository {
    fun getAllInvoicesByUserStream(idUser: Int): Flow<List<InvoiceEntity>>
    fun getInvoiceByStatusStream(idUser: Int, status: String): Flow<List<InvoiceEntity>>
    fun getInvoiceStream(id: Int): Flow<InvoiceEntity?>
    suspend fun insertInvoice(invoice: InvoiceEntity) : Long
    suspend fun deleteInvoice(invoice: InvoiceEntity)
    suspend fun updateInvoice(invoice: InvoiceEntity)
}

class OfflineInvoiceRepository(
    private val invoiceDao: InvoiceDao
): InvoiceRepository {
    override fun getAllInvoicesByUserStream(idUser: Int): Flow<List<InvoiceEntity>> = invoiceDao.getAllInvoicesByUserStream(idUser)
    override fun getInvoiceByStatusStream(idUser: Int, status: String): Flow<List<InvoiceEntity>> = invoiceDao.getInvoiceByStatusStream(idUser, status)
    override fun getInvoiceStream(id: Int): Flow<InvoiceEntity?> = invoiceDao.getInvoiceStream(id)
    override suspend fun insertInvoice(invoice: InvoiceEntity): Long = invoiceDao.insert(invoice)
    override suspend fun deleteInvoice(invoice: InvoiceEntity) = invoiceDao.delete(invoice)
    override suspend fun updateInvoice(invoice: InvoiceEntity) = invoiceDao.update(invoice)
}