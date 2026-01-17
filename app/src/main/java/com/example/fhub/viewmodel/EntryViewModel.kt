package com.example.fhub.viewmodel

import androidx.compose.runtime.mutableStateListOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fhub.R
import com.example.fhub.data.entity.InvoiceEntity
import com.example.fhub.data.entity.InvoiceItemEntity
import com.example.fhub.data.entity.KlienEntity
import com.example.fhub.data.entity.ProjectEntity
import com.example.fhub.repositori.InvoiceItemRepository
import com.example.fhub.repositori.InvoiceRepository
import com.example.fhub.repositori.KlienRepository
import com.example.fhub.repositori.ProjectRepository
import com.example.fhub.utils.ValidasiUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import java.sql.Date
import java.sql.Timestamp

class EntryViewModel(
    private val klienRepository: KlienRepository,
    private val projectRepository: ProjectRepository,
    private val invoiceRepository: InvoiceRepository,
    private val invoiceItemRepository: InvoiceItemRepository
) : ViewModel() {

    private var currentUserId: Int = 0

    // --- UI STATES ---
    private val _uiStateKlien = MutableStateFlow(UIStateKlien())
    val uiStateKlien: StateFlow<UIStateKlien> = _uiStateKlien.asStateFlow()

    private val _uiStateProject = MutableStateFlow(UIStateProject())
    val uiStateProject: StateFlow<UIStateProject> = _uiStateProject.asStateFlow()

    private val _uiStateInvoice = MutableStateFlow(UIStateInvoice())
    val uiStateInvoice: StateFlow<UIStateInvoice> = _uiStateInvoice.asStateFlow()

    // List Item Sementara
    val listInvoiceItems = mutableStateListOf<InvoiceItemEntity>()

    private var maxBudgetProject: Double = 0.0

    fun setUserId(userId: Int) {
        currentUserId = userId
        // Reset form saat masuk halaman agar bersih
        resetKlien()
        resetProject()
        resetInvoice()
        // Generate nomor invoice baru
        generateSequentialInvoiceNumber(userId)
    }

    private fun getUserId(): Int = currentUserId

    /* =====================================================================
       1. KLIEN (VALIDASI DUPLIKAT EMAIL & TELEPON)
       ===================================================================== */
    fun updateUiStateKlien(detail: DetailKlien) {
        // Validasi Real-time menggunakan Utils
        val isEmailValid = ValidasiUtils.isEmailValid(detail.email)
        val isTeleponValid = ValidasiUtils.isTeleponValid(detail.telepon)
        val isNamaValid = ValidasiUtils.isInputWajibDiisi(detail.namaLengkap)
        val isPerusahaanValid = ValidasiUtils.isInputWajibDiisi(detail.namaPerusahaan)

        val isValid = isEmailValid && isTeleponValid && isNamaValid && isPerusahaanValid

        // Update state
        _uiStateKlien.update {
            it.copy(
                detailKlien = detail.copy(idUser = getUserId()),
                isEntryValid = isValid,
                errorMessageId = null,
                isSaveSuccess = false
            )
        }
    }

    fun saveKlien() {
        if (_uiStateKlien.value.isEntryValid) {
            viewModelScope.launch {
                val input = _uiStateKlien.value.detailKlien

                // Cek Duplikat
                val existingClients = klienRepository.getKlienByUserStream(getUserId()).first()
                val isDuplicate = existingClients.any { existing ->
                    if (existing.idKlien == input.idKlien) return@any false

                    val cleanInputTelp = input.telepon.trim()
                    val cleanDbTelp = existing.telepon.trim()

                    existing.email.equals(input.email, ignoreCase = true) || cleanDbTelp == cleanInputTelp
                }

                if (isDuplicate) {
                    _uiStateKlien.update {
                        it.copy(errorMessageId = R.string.error_duplicate_client, isSaveSuccess = false)
                    }
                } else {
                    try {
                        klienRepository.insertKlien(input.toKlien())
                        resetKlien()
                        _uiStateKlien.update { it.copy(isSaveSuccess = true, errorMessageId = null) }
                    } catch (e: Exception) {
                        _uiStateKlien.update { it.copy(errorMessageId = R.string.error_occurred, isSaveSuccess = false) }
                    }
                }
            }
        } else {
            _uiStateKlien.update { it.copy(errorMessageId = R.string.error_wajib_diisi) }
        }
    }

    private fun resetKlien() {
        _uiStateKlien.value = UIStateKlien(DetailKlien(idUser = getUserId()))
    }

    /* =====================================================================
       2. PROJECT (CEK MASA LALU & RANGE)
       ===================================================================== */
    fun updateUiStateProject(detail: DetailProjectModel) {
        val isValidInput = ValidasiUtils.isInputWajibDiisi(detail.namaProject) &&
                detail.idKlien != 0 &&
                ValidasiUtils.isInputWajibDiisi(detail.status) &&
                ValidasiUtils.isHargaValid(detail.anggaran.toString())

        // 1. Cek Apakah Tanggal Dipilih?
        val isTanggalDipilih = detail.tanggalMulai != null && detail.deadline != null

        // 2. Cek Apakah Tanggal Mulai BUKAN MASA LALU? (Pakai Utils yang sudah kita buat)
        val isMasaDepan = if (detail.tanggalMulai != null) {
            ValidasiUtils.isTanggalInputValid(detail.tanggalMulai)
        } else true // Kalau belum dipilih, anggap true dulu biar ga merah duluan

        // 3. Cek Range (Deadline >= Mulai)
        val isRangeValid = if (isTanggalDipilih) {
            ValidasiUtils.isRangeTanggalValid(detail.tanggalMulai, detail.deadline)
        } else true

        // GABUNGKAN SEMUA VALIDASI
        // Tombol simpan hanya nyala jika: Input Lengkap AND Tanggal Dipilih AND Tanggal Masa Depan AND Range Benar
        val isAllValid = isValidInput && isTanggalDipilih && isMasaDepan && isRangeValid

        // Tentukan Pesan Error
        val errorMsg = when {
            !isMasaDepan -> R.string.error_invalid_date_range // Atau buat string baru "Tanggal tidak boleh lampau"
            !isRangeValid -> R.string.error_invalid_date_range
            else -> null
        }

        _uiStateProject.update {
            it.copy(
                detailProject = detail.copy(idUser = getUserId()),
                isEntryValid = isAllValid,
                errorMessageId = errorMsg
            )
        }
    }

    fun saveProject() {
        if (_uiStateProject.value.isEntryValid) {
            viewModelScope.launch {
                try {
                    projectRepository.insertProject(_uiStateProject.value.detailProject.toProjectEntity(getUserId()))
                    resetProject()
                    // Tambahkan flag sukses project jika perlu (seperti klien)
                } catch (e: Exception) {
                    _uiStateProject.update { it.copy(errorMessageId = R.string.error_occurred) }
                }
            }
        } else {
            _uiStateProject.update { it.copy(errorMessageId = R.string.error_wajib_diisi) }
        }
    }

    private fun resetProject() {
        _uiStateProject.value = UIStateProject(
            DetailProjectModel(
                idUser = getUserId(),
                tanggalMulai = null,
                deadline = null
            )
        )
    }

    /* =====================================================================
       3. INVOICE (CEK MASA LALU JUGA)
       ===================================================================== */
    private fun generateSequentialInvoiceNumber(userId: Int) {
        viewModelScope.launch {
            val allInvoices = invoiceRepository.getAllInvoicesByUserStream(userId).first()
            val maxNumber = allInvoices.mapNotNull { inv ->
                if (inv.invoiceNumber.startsWith("INV-")) {
                    inv.invoiceNumber.removePrefix("INV-").toIntOrNull()
                } else null
            }.maxOrNull() ?: 0
            val nextNumber = maxNumber + 1
            val formattedNumber = "INV-${nextNumber.toString().padStart(3, '0')}"

            _uiStateInvoice.update { currentState ->
                // ✅ Gunakan copy agar tanggal (issueDate) yang sudah ada di currentState tidak hilang
                val newDetail = currentState.detailInvoice.copy(invoiceNumber = formattedNumber)
                currentState.copy(
                    detailInvoice = newDetail,
                    isEntryValid = validasiInvoice(newDetail)
                )
            }
        }
    }

    fun onProjectSelected(project: ProjectEntity) {
        maxBudgetProject = project.anggaran // Mengunci batas uang
        _uiStateInvoice.update { currentState ->
            val updatedDetail = currentState.detailInvoice.copy(
                idProject = project.idProject,
                idKlien = project.idKlien
            )
            currentState.copy(detailInvoice = updatedDetail, isEntryValid = validasiInvoice(updatedDetail))
        }
        listInvoiceItems.clear()
    }

    fun updateUiStateInvoice(detail: DetailInvoiceModel) {
        val hariIniMillis = ValidasiUtils.getStartOfToday() // Pastikan fungsi ini ada di Utils kamu

        // 1. Validasi: Tanggal terbit tidak boleh < hari ini
        val isTerbitValid = detail.issueDate != null && detail.issueDate.time >= hariIniMillis

        // 2. Validasi: Jatuh tempo tidak boleh < Tanggal Terbit
        val isRangeValid = if (detail.issueDate != null && detail.dueDate != null) {
            detail.dueDate.time >= detail.issueDate.time
        } else false

        _uiStateInvoice.update {
            it.copy(
                detailInvoice = detail.copy(idUser = getUserId()),
                // Tombol simpan HANYA nyala jika terbit valid DAN range valid
                isEntryValid = validasiInvoice(detail) && isTerbitValid && isRangeValid,
                errorMessageId = when {
                    !isTerbitValid -> R.string.error_invalid_date_range // Pesan: Tanggal terbit tidak boleh masa lalu
                    !isRangeValid -> R.string.error_invalid_date_range  // Pesan: Jatuh tempo tidak boleh sebelum terbit
                    else -> null
                }
            )
        }
    }

    fun tambahItemInvoice(deskripsi: String, harga: Double) {
        if (!ValidasiUtils.isHargaValid(harga.toString())) return

        val totalSaatIni = listInvoiceItems.sumOf { it.harga }
        val estimasiTotal = totalSaatIni + harga

        // --- CEK APAKAH AKAN MELEBIHI ---
        if (estimasiTotal > maxBudgetProject) {
            _uiStateInvoice.update {
                it.copy(errorMessageId = R.string.error_over_budget)
            }
            return // 🛑 Berhenti. Jangan jalankan updateTotalOtomatis()
        }

        // --- JIKA AMAN, TAMBAHKAN ---
        val newItem = InvoiceItemEntity(idInvoice = 0, deskripsi = deskripsi, harga = harga)
        listInvoiceItems.add(newItem)

        // Baru jalankan update total
        updateTotalOtomatis()
    }

    fun hapusItemInvoice(item: InvoiceItemEntity) {
        listInvoiceItems.remove(item)
        updateTotalOtomatis()
    }

    private fun updateTotalOtomatis() {
        val totalBaru = listInvoiceItems.sumOf { it.harga }
        val currentDetail = _uiStateInvoice.value.detailInvoice

        // ✅ Syarat: Harus pas (Full Payment)
        val isBudgetPas = totalBaru == maxBudgetProject

        _uiStateInvoice.update {
            it.copy(
                detailInvoice = currentDetail.copy(total = totalBaru),
                // Tombol simpan hanya menyala jika rincian sudah pas
                isEntryValid = validasiInvoice(currentDetail.copy(total = totalBaru)) && isBudgetPas,
                // Jika belum pas, tampilkan error budget yang sudah ada
                errorMessageId = if (isBudgetPas) null else R.string.error_over_budget
            )
        }
    }

    private fun validasiInvoice(detail: DetailInvoiceModel): Boolean {
        val nomorValid = detail.invoiceNumber.isNotBlank()
        val projectValid = detail.idProject != 0
        val tanggalNotNull = detail.issueDate != null && detail.dueDate != null

        // ✅ Logika tanggalmu (TIDAK DIUBAH)
        val terbitValid = if (detail.issueDate != null) ValidasiUtils.isTanggalInputValid(detail.issueDate) else false
        val rangeValid = if (detail.issueDate != null && detail.dueDate != null) {
            detail.dueDate.time >= detail.issueDate.time
        } else false

        // ✅ Budget Check: Harus pas (Tidak boleh kurang, tidak boleh lebih)
        val totalItem = listInvoiceItems.sumOf { it.harga }
        val budgetValid = totalItem > 0 && totalItem == maxBudgetProject

        return nomorValid && projectValid && tanggalNotNull && terbitValid && rangeValid && budgetValid
    }

    fun saveInvoiceLengkap() {
        val detail = _uiStateInvoice.value.detailInvoice
        val totalItem = listInvoiceItems.sumOf { it.harga }

        when {
            // A, B, C: Validasi tanggal aslimu (TIDAK DIUBAH)
            detail.issueDate == null || detail.dueDate == null -> {
                _uiStateInvoice.update { it.copy(errorMessageId = R.string.error_wajib_diisi) }
            }
            !ValidasiUtils.isTanggalInputValid(detail.issueDate) -> {
                _uiStateInvoice.update { it.copy(errorMessageId = R.string.error_invalid_date_range) }
                return
            }
            detail.dueDate!!.time < detail.issueDate!!.time -> {
                _uiStateInvoice.update { it.copy(errorMessageId = R.string.error_invalid_date_range) }
                return
            }

            // D. ✅ VALIDASI BUDGET: Gunakan string yang sudah ada di strings.xml kamu
            totalItem != maxBudgetProject -> {
                _uiStateInvoice.update { it.copy(errorMessageId = R.string.error_over_budget) }
                return
            }

            // E. JIKA SEMUA BENAR
            _uiStateInvoice.value.isEntryValid && listInvoiceItems.isNotEmpty() -> {
                viewModelScope.launch {
                    try {
                        val invoiceId = invoiceRepository.insertInvoice(detail.toInvoiceEntity(getUserId()))
                        listInvoiceItems.forEach { item ->
                            invoiceItemRepository.insertItem(item.copy(idInvoice = invoiceId.toInt()))
                        }
                        listInvoiceItems.clear()
                        resetInvoice()
                        generateSequentialInvoiceNumber(getUserId())
                    } catch (e: Exception) {
                        _uiStateInvoice.update { it.copy(errorMessageId = R.string.error_duplicate_invoice) }
                    }
                }
            }
        }
    }

    private fun resetInvoice() {
        _uiStateInvoice.value = UIStateInvoice(
            DetailInvoiceModel(
                idUser = getUserId(),
                issueDate = null, // ✅ Ubah dari java.sql.Date(System.currentTimeMillis()) menjadi null
                dueDate = null
            )
        )
    }
}

// ===================== DATA CLASSES & EXTENSIONS =====================

data class UIStateKlien(
    val detailKlien: DetailKlien = DetailKlien(),
    val isEntryValid: Boolean = false,
    val errorMessageId: Int? = null,
    val isSaveSuccess: Boolean = false
)

data class DetailKlien(
    val idKlien: Int = 0,
    val idUser: Int = 0,
    val namaLengkap: String = "",
    val telepon: String = "",
    val email: String = "",
    val alamat: String = "",
    val namaPerusahaan: String = ""
)

fun DetailKlien.toKlien(): KlienEntity = KlienEntity(
    idKlien = idKlien, idUser = idUser, namaPerusahaan = namaPerusahaan,
    namaLengkap = namaLengkap, telepon = telepon, email = email, alamat = alamat
)

data class UIStateProject(
    val detailProject: DetailProjectModel = DetailProjectModel(),
    val isEntryValid: Boolean = false,
    val errorMessageId: Int? = null
)

// Tanggal Nullable agar user wajib pilih
data class DetailProjectModel(
    val idProject: Int = 0,
    val idUser: Int = 0,
    val idKlien: Int = 0,
    val namaProject: String = "",
    val deskripsi: String = "",
    val anggaran: Double = 0.0,
    val tanggalMulai: Timestamp? = null,
    val deadline: Timestamp? = null,
    val status: String = ""
)

fun DetailProjectModel.toProjectEntity(userId: Int): ProjectEntity = ProjectEntity(
    idProject = idProject, idUser = userId, idKlien = idKlien,
    namaProject = namaProject, deskripsi = deskripsi, anggaran = anggaran,
    // !! (Bang Operator) aman disini KARENA kita sudah validasi di tombol simpan (isEntryValid)
    // Ini menjamin tanggal yang disimpan adalah tanggal PILIHAN USER, bukan tanggal hari ini.
    tanggalMulai = tanggalMulai ?: Timestamp(System.currentTimeMillis()),
    deadline = deadline ?: Timestamp(System.currentTimeMillis()),
    status = status
)

data class UIStateInvoice(
    val detailInvoice: DetailInvoiceModel = DetailInvoiceModel(),
    val isEntryValid: Boolean = false,
    val errorMessageId: Int? = null,
    val isSaveSuccess: Boolean = false
)

data class DetailInvoiceModel(
    val idInvoice: Int = 0,
    val idUser: Int = 0,
    val idKlien: Int = 0,
    val idProject: Int = 0,
    val invoiceNumber: String = "",
    val issueDate: Date? = null, // Default Null
    val dueDate: Date? = null,   // Default Null
    val total: Double = 0.0,
    val status: String = ""
)

fun DetailInvoiceModel.toInvoiceEntity(userId: Int): InvoiceEntity = InvoiceEntity(
    idInvoice = idInvoice, idUser = userId, idKlien = idKlien, idProject = idProject,
    invoiceNumber = invoiceNumber,
    // Sama, pakai data dari user. Fallback ke Now hanya jika lolos validasi tapi datanya null (seharusnya tidak mungkin)
    issueDate = issueDate ?: Date(System.currentTimeMillis()),
    dueDate = dueDate ?: Date(System.currentTimeMillis()),
    total = total, status = status
)
