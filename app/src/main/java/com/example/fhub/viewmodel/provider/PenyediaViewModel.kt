package com.example.fhub.viewmodel.provider

import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.createSavedStateHandle
import androidx.lifecycle.viewmodel.CreationExtras
import androidx.lifecycle.viewmodel.initializer
import androidx.lifecycle.viewmodel.viewModelFactory
import com.example.fhub.repositori.FhubApplication
import com.example.fhub.viewmodel.AuthViewModel
import com.example.fhub.viewmodel.DetailViewModel
import com.example.fhub.viewmodel.EditViewModel
import com.example.fhub.viewmodel.EntryViewModel
import com.example.fhub.viewmodel.HomeViewModel

/**
 * Penyedia ViewModel dengan Factory Pattern
 * Mengelola dependency injection untuk semua ViewModel
 */
object PenyediaViewModel {

    val Factory = viewModelFactory {

        /* ================= AUTH ================= */
        initializer {
            val app = aplikasiFhub()
            AuthViewModel(
                userRepository = app.container.userRepository,
                context = app // Menggunakan context dari application
            )
        }

        /* ================= HOME / DASHBOARD ================= */
        // Menjamin HomeViewModel punya akses ke UserRepository untuk auto-login session
        initializer {
            val app = aplikasiFhub()
            HomeViewModel(
                klienRepository = app.container.klienRepository,
                projectRepository = app.container.projectRepository,
                invoiceRepository = app.container.invoiceRepository,
                userRepository = app.container.userRepository,
                context = app
            )
        }

        /* ================= ENTRY (TAMBAH DATA) ================= */
        initializer {
            val app = aplikasiFhub()
            EntryViewModel(
                klienRepository = app.container.klienRepository,
                projectRepository = app.container.projectRepository,
                invoiceRepository = app.container.invoiceRepository,
                invoiceItemRepository = app.container.invoiceItemRepository
            )
        }

        /* ================= DETAIL ================= */
        // DetailViewModel butuh SavedStateHandle untuk menangkap ID dari navigasi
        initializer {
            val app = aplikasiFhub()
            DetailViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                klienRepository = app.container.klienRepository,
                projectRepository = app.container.projectRepository,
                invoiceRepository = app.container.invoiceRepository,
                invoiceItemRepository = app.container.invoiceItemRepository
            )
        }

        /* ================= EDIT (UBAH DATA) ================= */
        initializer {
            val app = aplikasiFhub()
            EditViewModel(
                savedStateHandle = this.createSavedStateHandle(),
                klienRepository = app.container.klienRepository,
                projectRepository = app.container.projectRepository,
                invoiceRepository = app.container.invoiceRepository,
                invoiceItemRepository = app.container.invoiceItemRepository
            )
        }
    }
}

fun CreationExtras.aplikasiFhub(): FhubApplication =
    (this[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY] as FhubApplication)