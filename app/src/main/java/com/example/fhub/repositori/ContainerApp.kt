package com.example.fhub.repositori

import android.app.Application
import android.content.Context
import com.example.fhub.data.database.FhubDatabase


interface ContainerApp {
    val userRepository: UserRepository
    val klienRepository: KlienRepository
    val projectRepository: ProjectRepository
    val invoiceRepository: InvoiceRepository
    val invoiceItemRepository: InvoiceItemRepository
}

/* ============== IMPLEMENTATION ============== */
class ContainerDataApp(private val context: Context) :
    ContainerApp {
    override val userRepository: UserRepository by lazy {
        OfflineUserRepository(FhubDatabase.getDatabase(context).userDao())
    }
    override val klienRepository: KlienRepository by lazy {
        OfflineKlienRepository(FhubDatabase.getDatabase(context).klienDao())
    }
    override val projectRepository: ProjectRepository by lazy {
        OfflineProjectRepository(FhubDatabase.getDatabase(context).projectDao())
    }
    override val invoiceRepository: InvoiceRepository by lazy {
        OfflineInvoiceRepository(FhubDatabase.getDatabase(context).invoiceDao())
    }
    override val invoiceItemRepository: InvoiceItemRepository by lazy {
        OfflineInvoiceItemRepository(FhubDatabase.getDatabase(context).invoiceItemDao())
    }
}

/* ================= APPLICATION ================= */
class FhubApplication : Application() {
    lateinit var container: ContainerApp

    override fun onCreate() {
        super.onCreate()
        container = ContainerDataApp(this)
    }
}