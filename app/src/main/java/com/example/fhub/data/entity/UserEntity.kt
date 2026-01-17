package com.example.fhub.data.entity

import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey

@Entity(
    tableName = "user",
    indices = [Index(value = ["email"], unique = true)]
)
data class UserEntity(
    @PrimaryKey(autoGenerate = true)
    val idUser: Int = 0,
    val email: String,
    val password: String,
    val namaLengkap: String,
    val namaBisnis: String,
    val alamat: String
)