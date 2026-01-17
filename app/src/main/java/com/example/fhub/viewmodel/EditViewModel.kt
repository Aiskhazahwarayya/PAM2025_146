package com.example.fhub.viewmodel

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fhub.R
import com.example.fhub.data.entity.InvoiceItemEntity
import com.example.fhub.repositori.InvoiceItemRepository
import com.example.fhub.repositori.InvoiceRepository
import com.example.fhub.repositori.KlienRepository
import com.example.fhub.repositori.ProjectRepository
import com.example.fhub.utils.ValidasiUtils
import kotlinx.coroutines.flow.filterNotNull
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

class EditViewModel(
    savedStateHandle: SavedStateHandle,
    private val klienRepository: KlienRepository,
    private val projectRepository: ProjectRepository,
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository
) : ViewModel() {

    private var currentUserId: Int = 0
    private fun getUserId(): Int = currentUserId

    /* ================= STATE UI ================= */
    // Pastikan UIStateKlien sudah punya field 'isSaveSuccess' (dari update sebelumnya)
    var uiStateKlien by mutableStateOf(UIStateKlien())
        private set

    var uiStateProject by mutableStateOf(UIStateProject())
        private set

    var uiStateInvoice by mutableStateOf(UIStateInvoice())
        private set

    var listInvoiceItems by mutableStateOf(listOf<InvoiceItemEntity>())
        private set

    private var maxBudgetProject: Double = 0.0

    /* ================= INISIALISASI DATA ================= */
    fun initData(userId: Int, idKlien: Int = 0, idProject: Int = 0, idInvoice: Int = 0) {
        this.currentUserId = userId

        viewModelScope.launch {
            // ---------- LOAD KLIEN ----------
            if (idKlien != 0) {
                klienRepository.getKlienStream(idKlien)
                    .filterNotNull()
                    .first()
                    .let { entity ->
                        if (entity.idUser == userId) {
                            uiStateKlien = UIStateKlien(
                                detailKlien = DetailKlien(
                                    idKlien = entity.idKlien,
                                    idUser = entity.idUser,
                                    namaLengkap = entity.namaLengkap,
                                    telepon = entity.telepon,
                                    email = entity.email,
                                    alamat = entity.alamat,
                                    namaPerusahaan = entity.namaPerusahaan
                                ),
                                isEntryValid = true
                            )
                        }
                    }
            }

            // ---------- LOAD PROJECT ----------
            if (idProject != 0) {
                projectRepository.getProjectStream(idProject)
                    .filterNotNull()
                    .first()
                    .let { entity ->
                        if (entity.idUser == userId) {
                            uiStateProject = UIStateProject(
                                detailProject = DetailProjectModel(
                                    idProject = entity.idProject,
                                    idUser = entity.idUser,
                                    idKlien = entity.idKlien,
                                    namaProject = entity.namaProject,
                                    deskripsi = entity.deskripsi,
                                    anggaran = entity.anggaran,
                                    tanggalMulai = entity.tanggalMulai,
                                    deadline = entity.deadline,
                                    status = entity.status
                                ),
                                isEntryValid = true
                            )
                        }
                    }
            }

            // ---------- LOAD INVOICE ----------
            if (idInvoice != 0) {
                invoiceRepository.getInvoiceStream(idInvoice)
                    .filterNotNull()
                    .first()
                    .let { entity ->
                        if (entity.idUser == userId) {
                            val project = projectRepository.getProjectStream(entity.idProject).first()
                            maxBudgetProject = project?.anggaran ?: 0.0
                            uiStateInvoice = UIStateInvoice(
                                detailInvoice = DetailInvoiceModel(
                                    idInvoice = entity.idInvoice,
                                    idUser = entity.idUser,
                                    idKlien = entity.idKlien,
                                    idProject = entity.idProject,
                                    invoiceNumber = entity.invoiceNumber,
                                    issueDate = entity.issueDate,
                                    dueDate = entity.dueDate,
                                    total = entity.total,
                                    status = entity.status
                                ),
                                isEntryValid = true
                            )
                            listInvoiceItems = invoiceItemRepository
                                .getItemsByInvoiceStream(idInvoice).first()
                        }
                    }
            }
        }
    }

    /* ================= UPDATE KLIEN (VALIDASI DUPLIKAT SAAT EDIT) ================= */
    fun updateUiStateKlien(detail: DetailKlien) {
        val isEmailValid = ValidasiUtils.isEmailValid(detail.email)
        val isTeleponValid = ValidasiUtils.isTeleponValid(detail.telepon)
        val isNamaValid = ValidasiUtils.isInputWajibDiisi(detail.namaLengkap)
        val isPerusahaanValid = ValidasiUtils.isInputWajibDiisi(detail.namaPerusahaan)

        val isValid = isEmailValid && isTeleponValid && isNamaValid && isPerusahaanValid

        // Reset error saat ngetik & isSaveSuccess false
        uiStateKlien = uiStateKlien.copy(
            detailKlien = detail.copy(idUser = getUserId()),
            isEntryValid = isValid,
            errorMessageId = null,
            isSaveSuccess = false
        )
    }

    fun saveUpdateKlien() {
        if (uiStateKlien.isEntryValid) {
            viewModelScope.launch {
                val input = uiStateKlien.detailKlien

                // 1. Ambil semua klien User
                val existingClients = klienRepository.getKlienByUserStream(getUserId()).first()

                // 2. Cek Duplikat (KECUALI Diri Sendiri)
                val isDuplicate = existingClients.any { existing ->
                    // PENTING: Jangan cek data yang sedang diedit (idKlien sama = skip)
                    if (existing.idKlien == input.idKlien) return@any false

                    val emailSama = existing.email.equals(input.email, ignoreCase = true)
                    val teleponSama = existing.telepon == input.telepon
                    emailSama || teleponSama
                }

                if (isDuplicate) {
                    // ERROR: Set Success False, Tampilkan Pesan
                    uiStateKlien = uiStateKlien.copy(
                        errorMessageId = R.string.error_duplicate_client,
                        isSaveSuccess = false
                    )
                } else {
                    try {
                        // SUKSES: Update DB & Set Success True
                        klienRepository.updateKlien(input.toKlien())
                        uiStateKlien = uiStateKlien.copy(
                            isSaveSuccess = true,
                            errorMessageId = null
                        )
                    } catch (e: Exception) {
                        uiStateKlien = uiStateKlien.copy(
                            errorMessageId = R.string.error_occurred,
                            isSaveSuccess = false
                        )
                    }
                }
            }
        } else {
            uiStateKlien = uiStateKlien.copy(errorMessageId = R.string.error_wajib_diisi)
        }
    }

    /* ================= UPDATE PROJECT ================= */
    fun updateUiStateProject(detail: DetailProjectModel) {
        val isValid = ValidasiUtils.isInputWajibDiisi(detail.namaProject) &&
                detail.idKlien != 0 &&
                ValidasiUtils.isInputWajibDiisi(detail.status) &&
                ValidasiUtils.isHargaValid(detail.anggaran.toString()) &&
                detail.tanggalMulai != null && detail.deadline != null // Wajib isi tanggal

        // Cek Range
        val isRangeValid = ValidasiUtils.isRangeTanggalValid(detail.tanggalMulai, detail.deadline)

        uiStateProject = UIStateProject(
            detailProject = detail.copy(idUser = getUserId()),
            isEntryValid = isValid && isRangeValid,
            errorMessageId = if (!isRangeValid) R.string.error_invalid_date_range else null
        )
    }

    fun saveUpdateProject() {
        if (uiStateProject.isEntryValid) {
            viewModelScope.launch {
                projectRepository.updateProject(uiStateProject.detailProject.toProjectEntity(getUserId()))
            }
        } else {
            uiStateProject = uiStateProject.copy(errorMessageId = R.string.error_wajib_diisi)
        }
    }

    /* ================= UPDATE INVOICE ================= */
    fun updateUiStateInvoice(detail: DetailInvoiceModel) {
        val isTerbitValid = ValidasiUtils.isTanggalInputValid(detail.issueDate)
        val isRangeValid = if (detail.issueDate != null && detail.dueDate != null) {
            detail.dueDate.time >= detail.issueDate.time
        } else true

        // ✅ KEMBALI KE AWAL: Harus sama persis
        val isBudgetPas = detail.total == maxBudgetProject

        uiStateInvoice = uiStateInvoice.copy(
            detailInvoice = detail.copy(idUser = getUserId()),
            isEntryValid = validasiInvoice(detail) && isTerbitValid && isRangeValid && isBudgetPas,
            errorMessageId = when {
                !isTerbitValid -> R.string.error_past_date
                !isRangeValid -> R.string.error_invalid_range
                !isBudgetPas -> R.string.error_over_budget
                else -> null
            }
        )
    }

    private fun validasiInvoice(detail: DetailInvoiceModel): Boolean {
        val tanggalNotNull = detail.issueDate != null && detail.dueDate != null
        val nomorValid = detail.invoiceNumber.isNotBlank()
        val projectValid = detail.idProject != 0
        val terbitValid = ValidasiUtils.isTanggalInputValid(detail.issueDate)
        val rangeValid = if (tanggalNotNull) detail.dueDate!!.time >= detail.issueDate!!.time else false

        // ✅ KEMBALI KE AWAL: Total wajib sama dengan anggaran
        val budgetValid = detail.total > 0 && detail.total == maxBudgetProject

        return nomorValid && projectValid && tanggalNotNull && terbitValid && rangeValid && budgetValid
    }

    fun tambahItemEdit(deskripsi: String, harga: Double) {
        if (!ValidasiUtils.isHargaValid(harga.toString())) return

        val totalSaatIni = listInvoiceItems.sumOf { it.harga }

        // Blokir jika input baru bikin total melebihi budget
        if (totalSaatIni + harga > maxBudgetProject) {
            uiStateInvoice = uiStateInvoice.copy(errorMessageId = R.string.error_over_budget)
            return
        }

        val newItem = InvoiceItemEntity(
            idInvoice = uiStateInvoice.detailInvoice.idInvoice,
            deskripsi = deskripsi,
            harga = harga
        )
        listInvoiceItems = listInvoiceItems + newItem
        updateTotalOtomatis()
    }

    fun hapusItemEdit(item: InvoiceItemEntity) {
        viewModelScope.launch {
            // Hapus dari database jika item sudah tersimpan sebelumnya
            if (item.idInvoiceItem != 0) {
                invoiceItemRepository.deleteItem(item)
            }
            // Hapus dari list UI
            listInvoiceItems = listInvoiceItems - item
            updateTotalOtomatis()
        }
    }

    private fun updateTotalOtomatis() {
        val totalBaru = listInvoiceItems.sumOf { it.harga }

        // Tombol simpan nyala hanya jika total rincian PAS dengan budget
        val isBudgetPas = totalBaru == maxBudgetProject

        uiStateInvoice = uiStateInvoice.copy(
            detailInvoice = uiStateInvoice.detailInvoice.copy(total = totalBaru),
            isEntryValid = validasiInvoice(uiStateInvoice.detailInvoice.copy(total = totalBaru)) && isBudgetPas,
            errorMessageId = if (isBudgetPas) null else R.string.error_over_budget
        )
    }

    fun saveUpdateInvoiceLengkap() {
        val detail = uiStateInvoice.detailInvoice
        val totalItem = listInvoiceItems.sumOf { it.harga }

        when {
            // Validasi tanggal tetap (Anti Masa Lalu & Range Benar)
            detail.issueDate == null || detail.dueDate == null -> {
                uiStateInvoice = uiStateInvoice.copy(errorMessageId = R.string.error_wajib_diisi)
            }
            !ValidasiUtils.isTanggalInputValid(detail.issueDate) -> {
                uiStateInvoice = uiStateInvoice.copy(errorMessageId = R.string.error_past_date)
            }
            detail.dueDate!!.time < detail.issueDate!!.time -> {
                uiStateInvoice = uiStateInvoice.copy(errorMessageId = R.string.error_invalid_range)
            }

            // ✅ KEMBALI KE AWAL: Tolak jika tidak sama persis
            totalItem != maxBudgetProject -> {
                uiStateInvoice = uiStateInvoice.copy(errorMessageId = R.string.error_over_budget)
            }

            listInvoiceItems.isEmpty() -> {
                uiStateInvoice = uiStateInvoice.copy(errorMessageId = R.string.error_empty_field)
            }

            else -> {
                viewModelScope.launch {
                    try {
                        invoiceRepository.updateInvoice(detail.toInvoiceEntity(getUserId()))

                        // Proses simpan/update list item
                        listInvoiceItems.forEach { item ->
                            if (item.idInvoiceItem == 0) {
                                invoiceItemRepository.insertItem(item.copy(idInvoice = detail.idInvoice))
                            } else {
                                invoiceItemRepository.updateItem(item)
                            }
                        }

                        uiStateInvoice = uiStateInvoice.copy(isSaveSuccess = true, errorMessageId = null)
                    } catch (e: Exception) {
                        uiStateInvoice = uiStateInvoice.copy(errorMessageId = R.string.error_duplicate_invoice)
                    }
                }
            }
        }
    }
}