package com.example.fhub.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "invoice_item",
    // Definisi Foreign Key ke tabel Invoice
    foreignKeys = [
        ForeignKey(
            entity = InvoiceEntity::class,
            parentColumns = ["idInvoice"],
            childColumns = ["idInvoice"],
            onDelete = ForeignKey.CASCADE // Invoice dihapus -> Item ikut terhapus
        )
    ],
    // Wajib Index kolom FK
    indices = [Index(value = ["idInvoice"])]
)
data class InvoiceItemEntity(
    @PrimaryKey(autoGenerate = true)
    val idInvoiceItem: Int = 0,
    val idInvoice: Int, // FK ke Invoice
    val deskripsi: String,
    val harga: Double
)

