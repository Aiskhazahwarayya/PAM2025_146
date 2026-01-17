package com.example.fhub.data.database

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.fhub.data.dao.InvoiceDao
import com.example.fhub.data.dao.InvoiceItemDao
import com.example.fhub.data.dao.KlienDao
import com.example.fhub.data.dao.ProjectDao
import com.example.fhub.data.dao.UserDao
import com.example.fhub.data.entity.InvoiceEntity
import com.example.fhub.data.entity.InvoiceItemEntity
import com.example.fhub.data.entity.KlienEntity
import com.example.fhub.data.entity.ProjectEntity
import com.example.fhub.data.entity.UserEntity

// Daftarkan 5 Entity sesuai SRS
@Database(
    entities = [
        UserEntity::class,
        KlienEntity::class,
        ProjectEntity::class,
        InvoiceEntity::class,
        InvoiceItemEntity::class
    ],
    version =5,
    exportSchema = false
)

@TypeConverters(DateConverters::class)
abstract class FhubDatabase : RoomDatabase() {

    abstract fun userDao(): UserDao
    abstract fun klienDao(): KlienDao
    abstract fun projectDao(): ProjectDao
    abstract fun invoiceDao(): InvoiceDao
    abstract fun invoiceItemDao(): InvoiceItemDao

    companion object {
        @Volatile
        private var Instance: FhubDatabase? = null

        fun getDatabase(context: Context): FhubDatabase {
            return (Instance ?: synchronized(this) {
                Room.databaseBuilder(
                    context,
                    FhubDatabase::class.java,
                    "fhub_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                    .also { Instance = it }
            })
        }
    }
}