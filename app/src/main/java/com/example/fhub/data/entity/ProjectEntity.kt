package com.example.fhub.data.entity

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey
import java.sql.Timestamp

@Entity(
    tableName = "project",
    // Definisi 2 Foreign Key (Ke User dan Ke Klien)
    foreignKeys = [
        // 1. Hubungan ke User (Induk Utama)
        ForeignKey(
            entity = UserEntity::class,
            parentColumns = ["idUser"],
            childColumns = ["idUser"],
            onDelete = ForeignKey.CASCADE
        ),
        // 2. Hubungan ke Klien (Project ini milik klien siapa)
        ForeignKey(
            entity = KlienEntity::class,
            parentColumns = ["idKlien"],
            childColumns = ["idKlien"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    // Wajib Index kedua kolom foreign key tersebut
    indices = [Index(value = ["idUser"]), Index(value = ["idKlien"])]
)

data class ProjectEntity(
    @PrimaryKey(autoGenerate = true)
    val idProject: Int = 0,
    val idUser: Int,  // FK ke User
    val idKlien: Int, // FK ke Klien
    val namaProject: String,
    val deskripsi: String,
    val anggaran: Double,
    val tanggalMulai: Timestamp,
    val deadline: Timestamp,
    val status: String
)


