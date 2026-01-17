package com.example.fhub.viewmodel

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fhub.data.entity.InvoiceEntity
import com.example.fhub.data.entity.InvoiceItemEntity
import com.example.fhub.data.entity.KlienEntity
import com.example.fhub.data.entity.ProjectEntity
import com.example.fhub.repositori.InvoiceItemRepository
import com.example.fhub.repositori.InvoiceRepository
import com.example.fhub.repositori.KlienRepository
import com.example.fhub.repositori.ProjectRepository
import com.example.fhub.view.route.DestinasiDetailInvoice
import com.example.fhub.view.route.DestinasiDetailKlien
import com.example.fhub.view.route.DestinasiDetailProject
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class DetailViewModel(
    savedStateHandle: SavedStateHandle,
    private val klienRepository: KlienRepository,
    private val projectRepository: ProjectRepository,
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository
) : ViewModel() {

    companion object {
        private const val TIMEOUT = 1800L
    }

    /* ================= SESSION USER ================= */
    private val _currentUserId = MutableStateFlow<Int?>(null)

    fun setUserId(userId: Int) {
        _currentUserId.value = userId
    }

    private fun requireUserId(): Int =
        _currentUserId.value ?: error("User belum login")

    /* ================= ID DARI NAVIGASI ================= */
    // Pastikan key argument ini sesuai dengan yang ada di file Destinasi (Route)
    private val idKlien: Int? = savedStateHandle[DestinasiDetailKlien.itemIdArg]
    private val idProject: Int? = savedStateHandle[DestinasiDetailProject.itemIdArg]
    private val idInvoice: Int? = savedStateHandle[DestinasiDetailInvoice.itemIdArg]

    /* ================= DETAIL KLIEN ================= */
    val uiDetailKlienState: StateFlow<DetailKlienUiState> =
        _currentUserId.filterNotNull().flatMapLatest { userId ->
            if (idKlien == null || idKlien == 0) flowOf(DetailKlienUiState())
            else klienRepository.getKlienStream(idKlien)
                .filterNotNull()
                .filter { it.idUser == userId }
                .map { DetailKlienUiState(detailKlien = it.toDetailKlien()) }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(TIMEOUT), DetailKlienUiState())

    /* ================= DETAIL PROJECT ================= */
    val uiDetailProjectState: StateFlow<DetailProjectUiState> =
        _currentUserId.filterNotNull().flatMapLatest { userId ->
            if (idProject == null || idProject == 0) flowOf(DetailProjectUiState())
            else projectRepository.getProjectStream(idProject)
                .filterNotNull()
                .filter { it.idUser == userId }
                .map {
                    DetailProjectUiState(
                        detailProject = DetailProjectModel(
                            idProject = it.idProject,
                            idUser = it.idUser,
                            idKlien = it.idKlien,
                            namaProject = it.namaProject,
                            deskripsi = it.deskripsi,
                            anggaran = it.anggaran,
                            tanggalMulai = it.tanggalMulai,
                            deadline = it.deadline,
                            status = it.status
                        )
                    )
                }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(TIMEOUT), DetailProjectUiState())

    /* ================= DETAIL INVOICE + DATA LENGKAP (PDF) ================= */
    val uiDetailInvoiceState: StateFlow<DetailInvoiceUiState> =
        _currentUserId.filterNotNull().flatMapLatest { userId ->
            if (idInvoice == null || idInvoice == 0) flowOf(DetailInvoiceUiState())
            else {
                // 1. Ambil Data Invoice Utama
                invoiceRepository.getInvoiceStream(idInvoice)
                    .filterNotNull()
                    .filter { it.idUser == userId } // Validasi Keamanan User
                    .flatMapLatest { invoice ->

                        // 2. Ambil List Item
                        val itemsFlow = invoiceItemRepository.getItemsByInvoiceStream(idInvoice)

                        // 3. Ambil Klien (Jika ada) - Penting untuk PDF
                        val klienFlow = if (invoice.idKlien != 0)
                            klienRepository.getKlienStream(invoice.idKlien)
                        else flowOf(null)

                        // 4. Ambil Project (Jika ada) - Penting untuk PDF
                        val projectFlow = if (invoice.idProject != 0)
                            projectRepository.getProjectStream(invoice.idProject)
                        else flowOf(null)

                        // 5. Gabungkan Semua
                        combine(itemsFlow, klienFlow, projectFlow) { items, klien, project ->
                            DetailInvoiceUiState(
                                detailInvoice = DetailInvoiceModel(
                                    idInvoice = invoice.idInvoice,
                                    idUser = invoice.idUser,
                                    idKlien = invoice.idKlien,
                                    idProject = invoice.idProject,
                                    invoiceNumber = invoice.invoiceNumber,
                                    issueDate = invoice.issueDate,
                                    dueDate = invoice.dueDate,
                                    total = invoice.total,
                                    status = invoice.status
                                ),
                                listItem = items,
                                // ✅ DATA ASLI DIKIRIM KE UI UNTUK CETAK PDF
                                klien = klien,
                                project = project
                            )
                        }
                    }
            }
        }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(TIMEOUT), DetailInvoiceUiState())

    /* ================= DELETE ACTIONS ================= */
    fun deleteKlien() = viewModelScope.launch {
        val data = uiDetailKlienState.value.detailKlien
        if (data.idKlien != 0 && data.idUser == requireUserId()) {
            klienRepository.deleteKlien(data.toKlien())
        }
    }

    fun deleteProject() = viewModelScope.launch {
        val data = uiDetailProjectState.value.detailProject
        if (data.idProject != 0 && data.idUser == requireUserId()) {
            projectRepository.deleteProject(data.toProjectEntity(requireUserId()))
        }
    }

    fun deleteInvoice() = viewModelScope.launch {
        val data = uiDetailInvoiceState.value.detailInvoice
        if (data.idInvoice != 0 && data.idUser == requireUserId()) {
            invoiceRepository.deleteInvoice(data.toInvoiceEntity(requireUserId()))
        }
    }
}

/* ================= HELPER & STATE CLASSES ================= */

fun com.example.fhub.data.entity.KlienEntity.toDetailKlien() = DetailKlien(
    idKlien = idKlien,
    idUser = idUser,
    namaLengkap = namaLengkap,
    telepon = telepon,
    email = email,
    alamat = alamat,
    namaPerusahaan = namaPerusahaan
)

data class DetailKlienUiState(val detailKlien: DetailKlien = DetailKlien())

data class DetailProjectUiState(val detailProject: DetailProjectModel = DetailProjectModel())

data class DetailInvoiceUiState(
    val detailInvoice: DetailInvoiceModel = DetailInvoiceModel(),
    val listItem: List<InvoiceItemEntity> = emptyList(),
    // Data Tambahan untuk Tampilan Detail & PDF
    val klien: KlienEntity? = null,
    val project: ProjectEntity? = null
)