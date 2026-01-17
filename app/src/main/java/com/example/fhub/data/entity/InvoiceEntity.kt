package com.example.fhub.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Date


@Entity(
    tableName = "invoice",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["idUser"],
            childColumns = ["idUser"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = KlienEntity::class,
            parentColumns = ["idKlien"],
            childColumns = ["idKlien"],
            onDelete = ForeignKey.CASCADE
        ),
        ForeignKey(
            entity = ProjectEntity::class,
            parentColumns = ["idProject"],
            childColumns = ["idProject"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idUser"]),
        Index(value = ["idKlien"]),
        Index(value = ["idProject"]),
        Index(value = ["invoiceNumber"], unique = true)
    ]
)
data class InvoiceEntity(
    @PrimaryKey(autoGenerate = true)
    val idInvoice: Int = 0,
    val idUser: Int,
    val idKlien: Int,
    val idProject: Int,
    val invoiceNumber: String,
    val issueDate: Date,
    val dueDate: Date,
    val total: Double,
    val status: String
)