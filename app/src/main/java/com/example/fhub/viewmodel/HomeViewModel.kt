package com.example.fhub.viewmodel

import android.content.Context
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fhub.R
import com.example.fhub.data.entity.KlienEntity
import com.example.fhub.data.entity.ProjectEntity
import com.example.fhub.data.entity.InvoiceEntity
import com.example.fhub.repositori.InvoiceRepository
import com.example.fhub.repositori.KlienRepository
import com.example.fhub.repositori.ProjectRepository
import com.example.fhub.repositori.UserRepository
import com.example.fhub.utils.JenisStatus
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class HomeUiState(
    val namaUser: String = "",
    val namaBisnis: String = "",
    val totalKlien: Int = 0,
    val listProyekMendesak: List<ProjectEntity> = emptyList(),
    val listKlien: List<KlienEntity> = emptyList(),
    val searchQuery: String = "",
    val listProject: List<ProjectEntity> = emptyList(),
    val selectedProjectFilter: String = "All",
    val listInvoice: List<InvoiceEntity> = emptyList(),
    val selectedInvoiceFilter: String = "All",
    val isLoading: Boolean = true,
    // Tambahan untuk menangani error saat hapus data
    val errorMessageId: Int? = null,
    val errorMessageString: String? = null // Untuk pesan custom dari Exception
)

class HomeViewModel(
    private val klienRepository: KlienRepository,
    private val projectRepository: ProjectRepository,
    private val invoiceRepository: InvoiceRepository,
    private val userRepository: UserRepository,
    context: Context
) : ViewModel() {

    private val _userId = MutableStateFlow<Int?>(null)
    // Gunakan ApplicationContext untuk mencegah Memory Leak
    private val sharedPrefs = context.applicationContext.getSharedPreferences("fhub_session", Context.MODE_PRIVATE)

    // State tambahan untuk notifikasi error (Snackbar)
    private val _errorMessage = MutableStateFlow<String?>(null)

    init {
        loadSession()
    }

    private fun loadSession() {
        viewModelScope.launch {
            val email = sharedPrefs.getString("saved_email", null)
            if (email != null) {
                try {
                    val user = userRepository.getUserByEmail(email)
                    if (user != null) {
                        setUserId(user.idUser, user.namaLengkap, user.namaBisnis)
                    }
                } catch (e: Exception) {
                    // Ignore error session load
                }
            }
        }
    }

    private val _searchQuery = MutableStateFlow("")
    private val _projectFilter = MutableStateFlow("All")
    private val _invoiceFilter = MutableStateFlow("All")

    var namaUser by mutableStateOf("")
        private set

    var namaBisnis by mutableStateOf("")
        private set

    fun setUserId(id: Int, name: String, business: String) {
        _userId.value = id
        namaUser = name
        namaBisnis = business
    }

    fun onSearchQueryChange(query: String) { _searchQuery.value = query }

    // Filter Project (Gunakan String dari JenisStatus nanti di UI)
    fun onFilterProjectChange(status: String) { _projectFilter.value = status }

    // Filter Invoice
    fun onFilterInvoiceChange(status: String) { _invoiceFilter.value = status }

    // === FUNGSI HAPUS DENGAN ERROR HANDLING ===

    fun deleteKlien(item: KlienEntity) = viewModelScope.launch {
        try {
            klienRepository.deleteKlien(item)
        } catch (e: Exception) {
            // Tangkap error dari Repository (misal: "Klien punya proyek aktif")
            triggerError(e.message ?: "Gagal menghapus klien")
        }
    }

    fun deleteProject(item: ProjectEntity) = viewModelScope.launch {
        try {
            projectRepository.deleteProject(item)
        } catch (e: Exception) {
            triggerError("Gagal menghapus proyek")
        }
    }

    fun deleteInvoice(item: InvoiceEntity) = viewModelScope.launch {
        try {
            invoiceRepository.deleteInvoice(item)
        } catch (e: Exception) {
            triggerError("Gagal menghapus invoice")
        }
    }

    // Helper untuk menampilkan error sesaat lalu hilang
    private fun triggerError(msg: String) {
        viewModelScope.launch {
            _errorMessage.value = msg
            delay(1500) // Tampil 3 detik
            _errorMessage.value = null
        }
    }

    // Helper reset error manual (dipanggil UI saat Snackbar dismiss)
    fun clearError() {
        _errorMessage.value = null
    }

    // Data Class internal untuk Combine Flow
    private data class FilterParams(
        val userId: Int,
        val search: String,
        val pFilter: String,
        val iFilter: String,
        val errorMsg: String?
    )

    // === MAIN UI STATE FLOW ===
    val uiState: StateFlow<HomeUiState> = combine(
        _userId.filterNotNull(),
        _searchQuery,
        _projectFilter,
        _invoiceFilter,
        _errorMessage // Gabungkan error message ke state utama
    ) { userId, search, pFilter, iFilter, errorMsg ->
        FilterParams(userId, search, pFilter, iFilter, errorMsg)
    }.flatMapLatest { params ->

        // 1. Flow Klien (Searchable)
        val klienFlow = if (params.search.isBlank())
            klienRepository.getKlienByUserStream(params.userId)
        else
            klienRepository.searchKlienStream(params.userId, params.search)

        // 2. Flow Project & Invoice
        val projectFlow = projectRepository.getAllProjectsByUserStream(params.userId)
        val invoiceFlow = invoiceRepository.getAllInvoicesByUserStream(params.userId)

        combine(klienFlow, projectFlow, invoiceFlow) { kliens, projects, invoices ->
            val currentTime = System.currentTimeMillis()
            val sevenDaysFromNow = currentTime + (7 * 24 * 60 * 60 * 1000L)

            // Logic Dashboard: Proyek Mendesak (Status In Progress & Deadline < 7 hari)
            val dashboardDeadlineList = projects
                .filter { p ->
                    p.status == JenisStatus.PROYEK_IN_PROGRESS &&
                            (p.deadline?.time ?: 0L) > currentTime && // Masih belum lewat
                            (p.deadline?.time ?: 0L) <= sevenDaysFromNow
                }
                .sortedBy { it.deadline?.time ?: 0L }
                .take(3)

            // Logic Filter Project (Pakai JenisStatus biar konsisten)
            val filteredProjects = when (params.pFilter) {
                JenisStatus.PROYEK_IN_PROGRESS -> projects.filter { it.status == JenisStatus.PROYEK_IN_PROGRESS }
                JenisStatus.PROYEK_COMPLETED -> projects.filter { it.status == JenisStatus.PROYEK_COMPLETED }
                else -> projects // "All"
            }

            // Logic Filter Invoice
            val filteredInvoices = when (params.iFilter) {
                JenisStatus.INVOICE_BELUM_BAYAR -> invoices.filter { it.status == JenisStatus.INVOICE_BELUM_BAYAR }
                JenisStatus.INVOICE_LUNAS -> invoices.filter { it.status == JenisStatus.INVOICE_LUNAS }
                else -> invoices // "All"
            }

            HomeUiState(
                namaUser = namaUser,
                namaBisnis = namaBisnis,
                totalKlien = kliens.size,
                listProyekMendesak = dashboardDeadlineList,
                listKlien = kliens,
                searchQuery = params.search,
                listProject = filteredProjects,
                selectedProjectFilter = params.pFilter,
                listInvoice = filteredInvoices.sortedByDescending { it.issueDate?.time ?: 0L },
                selectedInvoiceFilter = params.iFilter,
                isLoading = false,
                errorMessageString = params.errorMsg // Masukkan pesan error ke state
            )
        }
    }.stateIn(
        scope = viewModelScope,
        started = SharingStarted.WhileSubscribed(5_000),
        initialValue = HomeUiState()
    )
}
