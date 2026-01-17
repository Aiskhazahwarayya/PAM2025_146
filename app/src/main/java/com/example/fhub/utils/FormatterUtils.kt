package com.example.fhub.utils

import android.content.ContentValues
import android.content.Context
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.pdf.PdfDocument
import android.graphics.Paint
import android.graphics.Typeface
import android.os.Build
import android.os.Environment
import android.provider.MediaStore
import android.widget.Toast
import com.example.fhub.data.entity.InvoiceEntity
import com.example.fhub.data.entity.InvoiceItemEntity
import com.example.fhub.data.entity.KlienEntity
import com.example.fhub.data.entity.ProjectEntity
import com.example.fhub.data.entity.UserEntity
import java.io.File
import java.io.FileOutputStream
import java.io.OutputStream
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object FormatterUtils {

    // Format Desimal (,00)
    fun formatRupiah(context: Context, amount: Double): String {
        val localeID = Locale("in", "ID")
        val numberFormat = NumberFormat.getNumberInstance(localeID)

        // Wajib 2 digit desimal
        numberFormat.minimumFractionDigits = 2
        numberFormat.maximumFractionDigits = 2

        return "Rp " + numberFormat.format(amount)
    }

    // Format Tanggal
    fun formatTanggal(dateObj: Any?): String {
        if (dateObj == null) return ""
        val sdf = SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID"))
        return try {
            when (dateObj) {
                is Long -> sdf.format(Date(dateObj))
                is java.sql.Timestamp -> sdf.format(Date(dateObj.time))
                is java.sql.Date -> sdf.format(dateObj)
                is Date -> sdf.format(dateObj)
                else -> "-"
            }
        } catch (e: Exception) { "-" }
    }

    // ================= PDF GENERATOR =================
    fun generateAndSaveInvoicePdf(
        context: Context,
        invoice: InvoiceEntity,
        items: List<InvoiceItemEntity>,
        user: UserEntity,
        klien: KlienEntity?,
        project: ProjectEntity?
    ) {
        val pdfDocument = PdfDocument()
        val pageInfo = PdfDocument.PageInfo.Builder(595, 842, 1).create() // A4
        val page = pdfDocument.startPage(pageInfo)
        val canvas: Canvas = page.canvas
        val paint = Paint()

        val marginStart = 40f
        val marginEnd = 555f
        var yPos = 50f

        // 1. HEADER
        paint.color = Color.BLACK
        paint.textSize = 24f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val headerName = if (user.namaBisnis.isNotBlank()) user.namaBisnis else user.namaLengkap
        canvas.drawText(headerName, marginStart, yPos, paint)

        yPos += 20f
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        if (user.alamat.isNotBlank() && user.alamat != "-") {
            canvas.drawText(user.alamat, marginStart, yPos, paint)
        }

        yPos += 25f
        paint.strokeWidth = 2f
        paint.color = Color.DKGRAY
        canvas.drawLine(marginStart, yPos, marginEnd, yPos, paint)

        // 2. JUDUL
        yPos += 40f
        paint.color = Color.BLACK
        paint.textSize = 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.CENTER
        canvas.drawText("INVOICE", 595f / 2, yPos, paint)
        paint.textAlign = Paint.Align.LEFT

        // 3. INFO
        yPos += 40f
        val startYInfo = yPos
        val colRightX = 350f

        // KIRI
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)

        canvas.drawText("Nomor: ${invoice.invoiceNumber}", marginStart, yPos, paint)
        yPos += 15f
        canvas.drawText("Terbit: ${formatTanggal(invoice.issueDate)}", marginStart, yPos, paint)
        yPos += 15f
        canvas.drawText("Jatuh Tempo: ${formatTanggal(invoice.dueDate)}", marginStart, yPos, paint)
        yPos += 20f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        if (invoice.status == JenisStatus.INVOICE_LUNAS) paint.color = Color.rgb(76, 175, 80)
        else paint.color = Color.RED
        canvas.drawText("Status: ${invoice.status.uppercase()}", marginStart, yPos, paint)
        paint.color = Color.BLACK

        yPos += 20f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        val projName = project?.namaProject ?: "-"
        canvas.drawText("PROYEK: $projName", marginStart, yPos, paint)

        // KANAN
        var yRight = startYInfo
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("KEPADA:", colRightX, yRight, paint)

        yRight += 15f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        val klienName = klien?.namaLengkap ?: "-"
        canvas.drawText(klienName, colRightX, yRight, paint)

        yRight += 15f
        val klienCompany = klien?.namaPerusahaan ?: ""
        if (klienCompany.isNotBlank()) {
            canvas.drawText(klienCompany, colRightX, yRight, paint)
        }

        yRight += 15f
        val klienAlamat = klien?.alamat ?: ""
        if (klienAlamat.isNotBlank()) {
            canvas.drawText(klienAlamat, colRightX, yRight, paint)
        }

        yPos = maxOf(yPos, yRight) + 40f

        // 4. TABEL
        paint.style = Paint.Style.FILL
        paint.color = Color.LTGRAY
        canvas.drawRect(marginStart, yPos - 15f, marginEnd, yPos + 10f, paint)

        paint.color = Color.BLACK
        paint.style = Paint.Style.FILL
        paint.textSize = 10f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        canvas.drawText("DESKRIPSI", marginStart + 10f, yPos, paint)

        paint.textAlign = Paint.Align.RIGHT
        canvas.drawText("HARGA", marginEnd - 10f, yPos, paint)
        paint.textAlign = Paint.Align.LEFT

        yPos += 30f

        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.NORMAL)
        if (items.isEmpty()) {
            canvas.drawText("- Tidak ada item -", marginStart + 10f, yPos, paint)
            yPos += 20f
        } else {
            items.forEach { item ->
                canvas.drawText(item.deskripsi, marginStart + 10f, yPos, paint)

                paint.textAlign = Paint.Align.RIGHT
                // Format Decimal akan muncul di sini
                canvas.drawText(formatRupiah(context, item.harga), marginEnd - 10f, yPos, paint)
                paint.textAlign = Paint.Align.LEFT

                yPos += 20f
            }
        }

        yPos += 10f
        paint.strokeWidth = 1f
        paint.color = Color.BLACK
        canvas.drawLine(marginStart, yPos, marginEnd, yPos, paint)

        // 5. TOTAL
        yPos += 30f
        paint.textSize = 12f
        paint.typeface = Typeface.create(Typeface.DEFAULT, Typeface.BOLD)
        paint.textAlign = Paint.Align.RIGHT
        // Format Decimal akan muncul di sini
        canvas.drawText("TOTAL:  ${formatRupiah(context, invoice.total)}", marginEnd - 10f, yPos, paint)

        pdfDocument.finishPage(page)

        val fileName = "Invoice_${invoice.invoiceNumber}_${System.currentTimeMillis()}.pdf"
        savePdf(context, pdfDocument, fileName)
    }

    private fun maxOf(a: Float, b: Float): Float {
        return if (a > b) a else b
    }

    private fun savePdf(context: Context, pdf: PdfDocument, fileName: String) {
        try {
            val stream: OutputStream?
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                val cv = ContentValues().apply {
                    put(MediaStore.MediaColumns.DISPLAY_NAME, fileName)
                    put(MediaStore.MediaColumns.MIME_TYPE, "application/pdf")
                    put(MediaStore.MediaColumns.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS)
                }
                val uri = context.contentResolver.insert(MediaStore.Downloads.EXTERNAL_CONTENT_URI, cv)
                stream = uri?.let { context.contentResolver.openOutputStream(it) }
            } else {
                val file = File(Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS), fileName)
                stream = FileOutputStream(file)
            }

            stream?.use {
                pdf.writeTo(it)
                Toast.makeText(context, "PDF Disimpan: Downloads/$fileName", Toast.LENGTH_LONG).show()
            }
        } catch (e: Exception) {
            e.printStackTrace()
            Toast.makeText(context, "Gagal simpan PDF: ${e.message}", Toast.LENGTH_SHORT).show()
        } finally {
            pdf.close()
        }
    }
}