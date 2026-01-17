package com.example.fhub.viewmodel

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.fhub.R
import com.example.fhub.data.entity.UserEntity
import com.example.fhub.repositori.UserRepository
import com.example.fhub.utils.ValidasiUtils
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

data class AuthUiState(
    val isLoading: Boolean = false,
    val isSuccess: Boolean = false,
    val errorMessageId: Int? = null,
    val isLoggedIn: Boolean = false,
    val currentUser: UserEntity? = null,
    val successMessageId: Int? = null
)

class AuthViewModel(
    private val userRepository: UserRepository,
    context: Context
) : ViewModel() {

    // Menggunakan Application Context biar aman dari memory leak
    private val sharedPrefs = context.applicationContext.getSharedPreferences("fhub_session", Context.MODE_PRIVATE)

    private val _uiState = MutableStateFlow(AuthUiState())
    val uiState: StateFlow<AuthUiState> = _uiState.asStateFlow()

    var currentUserId: Int? = null
        private set

    init {
        // Cek sesi login saat aplikasi dibuka
        val savedEmail = sharedPrefs.getString("saved_email", null)
        if (!savedEmail.isNullOrBlank()) {
            checkActiveSession(savedEmail)
        }
    }

    private fun checkActiveSession(email: String) {
        viewModelScope.launch {
            try {
                val user = userRepository.getUserByEmail(email)
                if (user != null) {
                    currentUserId = user.idUser
                    _uiState.update { it.copy(isLoggedIn = true, currentUser = user) }
                }
            } catch (e: Exception) {
                // Ignore error saat auto-login silent
            }
        }
    }

    fun login(email: String, pass: String) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageId = null, isSuccess = false) }
            try {
                val user = userRepository.getUserByEmail(email)
                // Cek apakah user ada DAN password cocok
                if (user != null && user.password == pass) {
                    // Simpan sesi
                    sharedPrefs.edit().putString("saved_email", email).apply()
                    currentUserId = user.idUser

                    _uiState.update {
                        it.copy(isLoading = false, isSuccess = true, isLoggedIn = true, currentUser = user)
                    }
                } else {
                    // Password salah / User tidak ditemukan -> msg_login_failed
                    _uiState.update { it.copy(isLoading = false, errorMessageId = R.string.msg_login_failed) }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessageId = R.string.error_occurred) }
            }
        }
    }

    fun register(
        namaLengkap: String,
        namaBisnis: String,
        email: String,
        pass: String
    ) {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessageId = null, isSuccess = false) }

            // 1. Validasi Wajib Diisi (Hanya 4 field sesuai skenario)
            if (!ValidasiUtils.isInputWajibDiisi(namaLengkap) ||
                !ValidasiUtils.isInputWajibDiisi(namaBisnis) ||
                !ValidasiUtils.isInputWajibDiisi(email) ||
                !ValidasiUtils.isInputWajibDiisi(pass)) {

                _uiState.update { it.copy(isLoading = false, errorMessageId = R.string.error_empty_field) }
                return@launch
            }

            // 2. Validasi Format Email
            if (!ValidasiUtils.isEmailValid(email)) {
                _uiState.update { it.copy(isLoading = false, errorMessageId = R.string.error_email_invalid) }
                return@launch
            }

            // 3. Validasi Panjang Password
            if (!ValidasiUtils.isPasswordValid(pass)) {
                _uiState.update { it.copy(isLoading = false, errorMessageId = R.string.error_password_too_short) }
                return@launch
            }

            try {
                // 4. Cek Duplikat Email di Database
                val existingUser = userRepository.getUserByEmail(email)
                if (existingUser != null) {
                    _uiState.update { it.copy(isLoading = false, errorMessageId = R.string.error_duplicate_email) }
                } else {
                    // Tambahkan "-" atau "" untuk alamat agar UserEntity tidak error
                    val newUser = UserEntity(
                        namaLengkap = namaLengkap,
                        namaBisnis = namaBisnis,
                        alamat = "-", // Memberikan nilai default agar database tidak error
                        email = email,
                        password = pass
                    )
                    userRepository.insertUser(newUser)

                    _uiState.update {
                        it.copy(
                            isLoading = false,
                            isSuccess = true,
                            successMessageId = R.string.msg_account_created
                        )
                    }
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessageId = R.string.error_occurred) }
            }
        }
    }

    fun updateProfile(namaLengkap: String, namaBisnis: String, alamat: String) {
        val currentUser = _uiState.value.currentUser ?: return

        // Validasi input kosong saat update profile
        if (!ValidasiUtils.isInputWajibDiisi(namaLengkap) ||
            !ValidasiUtils.isInputWajibDiisi(namaBisnis) ||
            !ValidasiUtils.isInputWajibDiisi(alamat)) {
            _uiState.update { it.copy(errorMessageId = R.string.error_empty_field) }
            return
        }

        _uiState.update { it.copy(isLoading = true, isSuccess = false, errorMessageId = null) }

        viewModelScope.launch {
            try {
                val updatedUser = currentUser.copy(
                    namaLengkap = namaLengkap,
                    namaBisnis = namaBisnis,
                    alamat = alamat
                )
                userRepository.updateUser(updatedUser)

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isSuccess = true,
                        currentUser = updatedUser,
                        successMessageId = R.string.msg_profile_updated
                    )
                }
            } catch (e: Exception) {
                _uiState.update { it.copy(isLoading = false, errorMessageId = R.string.error_occurred) }
            }
        }
    }

    fun logout() {
        // Hapus sesi email
        sharedPrefs.edit().remove("saved_email").apply()
        currentUserId = null

        // Reset state dan beri pesan logout
        _uiState.update {
            AuthUiState(
                isLoggedIn = false,
                successMessageId = R.string.msg_logout
            )
        }
    }

    fun resetState() {
        _uiState.update { it.copy(isSuccess = false, errorMessageId = null, successMessageId = null) }
    }

    fun refreshCurrentUser() {
        val email = sharedPrefs.getString("saved_email", null)
        if (!email.isNullOrBlank()) {
            viewModelScope.launch {
                try {
                    // Ambil data terbaru dari database berdasarkan email yang tersimpan
                    val updatedUser = userRepository.getUserByEmail(email)
                    if (updatedUser != null) {
                        _uiState.update { it.copy(currentUser = updatedUser) }
                    }
                } catch (e: Exception) {
                    // Abaikan jika error saat refresh
                }
            }
        }
    }
}