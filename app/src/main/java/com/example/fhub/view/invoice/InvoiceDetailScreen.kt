package com.example.fhub.view.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PictureAsPdf
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.example.fhub.R
import com.example.fhub.data.entity.InvoiceItemEntity
import com.example.fhub.data.entity.KlienEntity
import com.example.fhub.data.entity.ProjectEntity
import com.example.fhub.data.entity.UserEntity
import com.example.fhub.utils.FormatterUtils
import com.example.fhub.utils.JenisStatus
import com.example.fhub.viewmodel.DetailViewModel
import com.example.fhub.viewmodel.DetailInvoiceModel
import com.example.fhub.viewmodel.HomeViewModel
import com.example.fhub.viewmodel.toInvoiceEntity
import kotlinx.coroutines.launch
import java.sql.Timestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceDetailScreen(
    id: Int,
    viewModel: DetailViewModel,
    user: UserEntity?,
    onEdit: (Int) -> Unit,
    onBack: () -> Unit
) {
    val uiState by viewModel.uiDetailInvoiceState.collectAsState()
    val invoice = uiState.detailInvoice
    val invoiceItems = uiState.listItem
    val klienAsli = uiState.klien
    val projectAsli = uiState.project

    val coroutineScope = rememberCoroutineScope()
    val context = LocalContext.current
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (invoice.idInvoice == 0) {
        Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
            CircularProgressIndicator(color = Color(0xFF1A237E))
        }
        return
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Detail Invoice", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.Default.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(containerColor = Color.White)
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White)
                .padding(padding)
                .padding(20.dp)
                .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // --- KARTU INFORMASI UTAMA (TEMA BIRU INDIGO) ---
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                // Menggunakan Biru Indigo Muda sebagai background kartu
                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    // Nomor Invoice di Header Kartu
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Icon(Icons.Default.Receipt, null, tint = Color(0xFF1A237E))
                        Text(
                            text = invoice.invoiceNumber,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A237E)
                        )
                    }

                    HorizontalDivider(thickness = 1.dp, color = Color(0xFF1A237E).copy(alpha = 0.1f))

                    // Detail Informasi dengan Icon Biru
                    DetailRowWithIcon(Icons.Default.Person, "Klien", klienAsli?.namaLengkap ?: "-")
                    DetailRowWithIcon(Icons.Default.Work, "Proyek", projectAsli?.namaProject ?: "-")
                    DetailRowWithIcon(Icons.Default.CalendarToday, "Terbit", FormatterUtils.formatTanggal(invoice.issueDate))
                    DetailRowWithIcon(Icons.Default.Event, "Tempo", FormatterUtils.formatTanggal(invoice.dueDate))

                    val isLunas = invoice.status == JenisStatus.INVOICE_LUNAS
                    DetailRowWithIcon(
                        icon = if(isLunas) Icons.Default.CheckCircle else Icons.Default.Info,
                        label = "Status",
                        value = if(isLunas) "LUNAS" else "PENDING",
                        valueColor = if(isLunas) Color(0xFF2E7D32) else Color(0xFFD32F2F)
                    )

                    Spacer(Modifier.height(8.dp))
                    Text("Rincian Tagihan :", style = MaterialTheme.typography.labelLarge, color = Color(0xFF3949AB), fontWeight = FontWeight.Bold)

                    // List Item dengan aksen Biru
                    invoiceItems.forEach { item ->
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text(item.deskripsi, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF1A237E))
                            Text(FormatterUtils.formatRupiah(context, item.harga), fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                        }
                    }

                    HorizontalDivider(thickness = 1.5.dp, color = Color(0xFF1A237E).copy(alpha = 0.2f))

                    // Area Total yang Menonjol
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                        Text("TOTAL", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.ExtraBold, color = Color(0xFF3949AB))
                        Text(
                            FormatterUtils.formatRupiah(context, invoice.total),
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.ExtraBold,
                            color = Color(0xFF1A237E)
                        )
                    }
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- TOMBOL AKSI (SESUAI TEMA BIRU) ---
            Column(verticalArrangement = Arrangement.spacedBy(14.dp)) {
                // Cetak PDF (Warna Hijau untuk menunjukkan fungsi dokumen)
                Button(
                    onClick = {
                        coroutineScope.launch {
                            user?.let { FormatterUtils.generateAndSaveInvoicePdf(context, invoice.toInvoiceEntity(it.idUser), invoiceItems, it, klienAsli, projectAsli) }
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF2E7D32))
                ) {
                    Icon(Icons.Default.PictureAsPdf, null)
                    Spacer(Modifier.width(10.dp))
                    Text("Cetak PDF", fontWeight = FontWeight.Bold)
                }

                // Edit (Biru Indigo Utama)
                Button(
                    onClick = { onEdit(invoice.idInvoice) },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                ) {
                    Icon(Icons.Default.Edit, null)
                    Spacer(Modifier.width(10.dp))
                    Text("Edit Invoice", fontWeight = FontWeight.Bold)
                }

                // Hapus (Minimalis Merah)
                OutlinedButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    shape = RoundedCornerShape(12.dp),
                    border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD32F2F)),
                    colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F))
                ) {
                    Text("Hapus Invoice", fontWeight = FontWeight.Bold)
                }
            }
        }
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Invoice") },
            text = { Text("Tindakan ini tidak dapat dibatalkan. Hapus?") },
            confirmButton = {
                TextButton(onClick = { coroutineScope.launch { viewModel.deleteInvoice(); showDeleteDialog = false; onBack() } }) {
                    Text("Hapus", color = Color.Red)
                }
            },
            dismissButton = { TextButton({ showDeleteDialog = false }) { Text("Batal") } }
        )
    }
}

@Composable
private fun DetailRowWithIcon(icon: ImageVector, label: String, value: String, valueColor: Color = Color(0xFF1A237E)) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, modifier = Modifier.size(18.dp), tint = Color(0xFF3949AB))
        Spacer(Modifier.width(10.dp))
        Text(text = "$label :", style = MaterialTheme.typography.bodyMedium, color = Color(0xFF3949AB))
        Spacer(Modifier.weight(1f))
        Text(
            text = value,
            style = MaterialTheme.typography.bodyMedium,
            fontWeight = FontWeight.Bold,
            color = valueColor,
            textAlign = TextAlign.End
        )
    }
}