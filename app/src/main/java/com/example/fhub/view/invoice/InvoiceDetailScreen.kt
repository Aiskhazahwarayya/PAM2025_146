package com.example.fhub.view.invoice

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
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
import androidx.compose.material.icons.filled.Numbers
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
        containerColor = Color(0xFFF8F9FE) // Background abu-abu kebiruan soft
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER GRADIENT DENGAN RINGKASAN TOTAL ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(240.dp)
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A237E), Color(0xFF3949AB))
                        )
                    )
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                }

                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(top = 60.dp),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = "Total Tagihan",
                        color = Color.White.copy(alpha = 0.7f),
                        style = MaterialTheme.typography.labelLarge
                    )
                    Text(
                        text = FormatterUtils.formatRupiah(context, invoice.total),
                        color = Color.White,
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.ExtraBold
                    )
                    Spacer(Modifier.height(12.dp))
                    // Badge Status
                    val isLunas = invoice.status == JenisStatus.INVOICE_LUNAS
                    Surface(
                        shape = CircleShape,
                        color = if (isLunas) Color(0xFF66BB6A).copy(alpha = 0.2f) else Color(0xFFFF7043).copy(alpha = 0.2f),
                        border = BorderStroke(1.dp, if (isLunas) Color(0xFF66BB6A) else Color(0xFFFF7043))
                    ) {
                        Text(
                            text = invoice.status.uppercase(),
                            modifier = Modifier.padding(horizontal = 16.dp, vertical = 4.dp),
                            color = Color.White,
                            style = MaterialTheme.typography.labelMedium,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            // --- KARTU DETAIL INVOICE  ---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-40).dp),
                verticalArrangement = Arrangement.spacedBy(16.dp)
            ) {
                // 1. KARTU INFORMASI UTAMA
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(Icons.Default.Receipt, null, tint = Color(0xFF3949AB), modifier = Modifier.size(20.dp))
                            Spacer(Modifier.width(8.dp))
                            Text(
                                text = "Detail Dokumen",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A237E)
                            )
                        }

                        HorizontalDivider(Modifier.padding(vertical = 16.dp), thickness = 0.5.dp, color = Color.LightGray)

                        DetailInfoRow(Icons.Default.Numbers, "No. Invoice", invoice.invoiceNumber)
                        DetailInfoRow(Icons.Default.Person, "Klien", klienAsli?.namaLengkap ?: "-")
                        DetailInfoRow(Icons.Default.Work, "Proyek", projectAsli?.namaProject ?: "-")
                        DetailInfoRow(Icons.Default.CalendarToday, "Tgl Terbit", FormatterUtils.formatTanggal(invoice.issueDate))
                        DetailInfoRow(Icons.Default.Event, "Jatuh Tempo", FormatterUtils.formatTanggal(invoice.dueDate))
                    }
                }

                // 2. KARTU RINCIAN ITEM
                Card(
                    shape = RoundedCornerShape(28.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp)) {
                        Text(
                            text = "Rincian Item",
                            style = MaterialTheme.typography.titleSmall,
                            fontWeight = FontWeight.Bold,
                            color = Color.Gray
                        )

                        Spacer(Modifier.height(12.dp))

                        invoiceItems.forEach { item ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(vertical = 8.dp),
                                horizontalArrangement = Arrangement.SpaceBetween
                            ) {
                                Text(item.deskripsi, style = MaterialTheme.typography.bodyMedium, modifier = Modifier.weight(1f))
                                Text(
                                    text = FormatterUtils.formatRupiah(context, item.harga),
                                    style = MaterialTheme.typography.bodyMedium,
                                    fontWeight = FontWeight.Bold
                                )
                            }
                        }
                    }
                }

                // --- TOMBOL AKSI ---
                Row(
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp),
                    horizontalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    // Tombol PDF (Secondary)
                    OutlinedButton(
                        onClick = {
                            coroutineScope.launch {
                                user?.let { FormatterUtils.generateAndSaveInvoicePdf(context, invoice.toInvoiceEntity(it.idUser), invoiceItems, it, klienAsli, projectAsli) }
                            }
                        },
                        modifier = Modifier.weight(1f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        border = BorderStroke(1.5.dp, Color(0xFF2E7D32)),
                        colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFF2E7D32))
                    ) {
                        Icon(Icons.Default.PictureAsPdf, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("PDF", fontWeight = FontWeight.Bold)
                    }

                    // Tombol Edit
                    Button(
                        onClick = { onEdit(invoice.idInvoice) },
                        modifier = Modifier.weight(1.5f).height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                    ) {
                        Icon(Icons.Default.Edit, null, modifier = Modifier.size(18.dp))
                        Spacer(Modifier.width(8.dp))
                        Text("Edit Invoice", fontWeight = FontWeight.Bold)
                    }
                }

                // Tombol Hapus
                TextButton(
                    onClick = { showDeleteDialog = true },
                    modifier = Modifier.fillMaxWidth()
                ) {
                    Icon(Icons.Default.Delete, null, tint = Color.Red, modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text("Hapus Invoice Permanen", color = Color.Red)
                }

                Spacer(Modifier.height(20.dp))
            }
        }
    }

    // Dialog Konfirmasi Hapus
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            icon = { Icon(Icons.Default.Warning, null, tint = Color.Red) },
            title = { Text("Hapus Invoice?") },
            text = { Text("Data ini akan dihapus permanen dari sistem FreelanceHub.") },
            confirmButton = {
                Button(
                    onClick = { coroutineScope.launch { viewModel.deleteInvoice(); showDeleteDialog = false; onBack() } },
                    colors = ButtonDefaults.buttonColors(containerColor = Color.Red)
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
fun DetailInfoRow(icon: ImageVector, label: String, value: String) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(icon, null, tint = Color.Gray, modifier = Modifier.size(16.dp))
        Spacer(Modifier.width(12.dp))
        Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray, modifier = Modifier.width(80.dp))
        Text(text = value, style = MaterialTheme.typography.bodyMedium, fontWeight = FontWeight.SemiBold, color = Color.Black)
    }
}