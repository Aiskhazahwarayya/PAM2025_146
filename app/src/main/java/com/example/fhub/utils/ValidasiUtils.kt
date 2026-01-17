package com.example.fhub.utils

import android.util.Patterns
import java.sql.Timestamp
import java.util.Calendar
import java.util.Date

object ValidasiUtils {

    // ==========================================
    // 1. VALIDASI FORMAT INPUT (TEXT)
    // ==========================================

    fun isEmailValid(email: String): Boolean =
        email.isNotEmpty() && Patterns.EMAIL_ADDRESS.matcher(email).matches()

    fun isTeleponValid(telepon: String): Boolean =
        telepon.all { it.isDigit() } && telepon.startsWith("08") && (telepon.length in 12..13)

    fun isHargaValid(harga: String): Boolean =
        try { harga.toDouble() > 0 } catch (e: Exception) { false }

    fun isPasswordValid(password: String): Boolean {
        return password.length >= 8
    }

    fun isInvoiceNumberValid(number: String): Boolean {
        return number.startsWith("INV-") && number.length >= 5
    }

    fun isInputWajibDiisi(text: String): Boolean {
        return text.isNotBlank()
    }

    // ==========================================
    // 2. VALIDASI TANGGAL (FIXED FOR OBJECTS)
    // ==========================================

    /**
     * Helper Internal: Mengubah berbagai tipe (Long/Timestamp/Date) ke milidetik.
     * Ini mencegah error "Argument type mismatch".
     */
    private fun toMillis(dateObj: Any?): Long {
        return when (dateObj) {
            is Long -> dateObj
            is Timestamp -> dateObj.time
            is Date -> dateObj.time
            is java.util.Date -> dateObj.time
            else -> 0L
        }
    }

    fun getStartOfToday(): Long {
        val cal = Calendar.getInstance()
        cal.set(Calendar.HOUR_OF_DAY, 0)
        cal.set(Calendar.MINUTE, 0)
        cal.set(Calendar.SECOND, 0)
        cal.set(Calendar.MILLISECOND, 0)
        return cal.timeInMillis
    }

    /**
     * Validasi Tanggal Mulai / Terbit.
     * Sesuai Kamus Data: Default hari ini, tidak boleh masa lalu.
     */
    fun isTanggalInputValid(dateObj: Any?): Boolean {
        val millis = toMillis(dateObj)
        if (millis == 0L) return false
        return millis >= getStartOfToday()
    }

    /**
     * Validasi Deadline Proyek / Jatuh Tempo Invoice.
     * End tidak boleh sebelum Start.
     */
    fun isRangeTanggalValid(startObj: Any?, endObj: Any?): Boolean {
        val start = toMillis(startObj)
        val end = toMillis(endObj)
        if (start == 0L || end == 0L) return false
        return end >= start
    }
}