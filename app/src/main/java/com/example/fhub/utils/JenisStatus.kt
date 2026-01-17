package com.example.fhub.utils

object JenisStatus {
    const val PROYEK_IN_PROGRESS = "In Progress"
    const val PROYEK_COMPLETED = "Completed"

    const val INVOICE_BELUM_BAYAR = "Belum Bayar"
    const val INVOICE_LUNAS = "Lunas"

    val listStatusProyek = listOf(PROYEK_IN_PROGRESS, PROYEK_COMPLETED)
    val listStatusInvoice = listOf(INVOICE_BELUM_BAYAR, INVOICE_LUNAS)
}