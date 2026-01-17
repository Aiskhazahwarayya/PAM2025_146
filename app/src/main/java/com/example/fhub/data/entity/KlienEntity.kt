package com.example.fhub.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "klien",
    foreignKeys = [
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["idUser"],
            childColumns = ["idUser"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [
        Index(value = ["idUser"]),
        Index(value = ["idUser", "email"], unique = true),
        Index(value = ["idUser", "telepon"], unique = true)
    ]
)
data class KlienEntity(
    @PrimaryKey(autoGenerate = true)
    val idKlien: Int = 0,
    val idUser: Int,
    val namaPerusahaan: String,
    val namaLengkap: String,
    val telepon: String,
    val email: String,
    val alamat: String
)