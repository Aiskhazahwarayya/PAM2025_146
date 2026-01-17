package com.example.fhub.view.project

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
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
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material.icons.filled.Edit
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Timeline
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.AssistChip
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
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
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
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
import androidx.compose.ui.unit.dp
import com.example.fhub.R
import com.example.fhub.utils.FormatterUtils
import com.example.fhub.utils.JenisStatus
import com.example.fhub.viewmodel.DetailViewModel
import kotlinx.coroutines.launch
import java.text.NumberFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectDetailScreen(
    id: Int,
    viewModel: DetailViewModel,
    onBack: () -> Unit,
    onEdit: (Int) -> Unit
) {
    val uiState by viewModel.uiDetailProjectState.collectAsState()
    val project = uiState.detailProject
    val coroutineScope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }
    val context = LocalContext.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(text = "Detail Proyek", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                // ACTIONS DI ATAS DIHAPUS AGAR CLEAN
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { padding ->
        if (project.idProject == 0) {
            Box(Modifier.fillMaxSize().padding(padding), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color(0xFF1A237E))
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .background(Color(0xFFF5F5F5))
                    .padding(padding)
                    .verticalScroll(rememberScrollState())
            ) {
                // --- HEADER SECTION ---
                Box(
                    modifier = Modifier.fillMaxWidth()
                        .background(Brush.verticalGradient(listOf(Color(0xFF1A237E), Color(0xFF283593))))
                        .padding(dimensionResource(R.dimen.spacing_large))
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.fillMaxWidth()) {
                        Box(
                            modifier = Modifier.size(80.dp).clip(CircleShape).background(Color.White.copy(alpha = 0.2f)),
                            contentAlignment = Alignment.Center
                        ) {
                            Icon(Icons.Default.Work, null, Modifier.size(48.dp), tint = Color.White)
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(text = project.namaProject, style = MaterialTheme.typography.headlineSmall, fontWeight = FontWeight.Bold, color = Color.White)
                        Spacer(Modifier.height(8.dp))

                        Surface(
                            shape = RoundedCornerShape(16.dp),
                            color = if (project.status == JenisStatus.PROYEK_COMPLETED) Color(0xFF4CAF50) else Color(0xFF2196F3)
                        ) {
                            Text(
                                text = if (project.status == JenisStatus.PROYEK_IN_PROGRESS) "Sedang Berjalan" else "Selesai",
                                modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp),
                                color = Color.White, fontWeight = FontWeight.Bold
                            )
                        }
                    }
                }

                // --- CONTENT INFO ---
                Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {

                    if (project.deskripsi.isNotEmpty()) {
                        InfoCard(title = "Deskripsi") {
                            Text(text = project.deskripsi, style = MaterialTheme.typography.bodyMedium, color = Color(0xFF424242))
                        }
                    }

                    InfoCard(title = "Informasi Anggaran") {
                        ProjectDetailInfoItem(
                            Icons.Default.AttachMoney,
                            "Anggaran (Rp)",
                            FormatterUtils.formatRupiah(context, project.anggaran),
                            Color(0xFF4CAF50)
                        )
                    }

                    InfoCard(title = "Timeline Proyek") {
                        ProjectDetailInfoItem(Icons.Default.CalendarToday, "Tanggal Mulai", FormatterUtils.formatTanggal(project.tanggalMulai))
                        ProjectDetailInfoItem(Icons.Default.Event, "Deadline", FormatterUtils.formatTanggal(project.deadline))

                        val startMillis = project.tanggalMulai?.time ?: 0L
                        val endMillis = project.deadline?.time ?: 0L
                        val durationDays = ((endMillis - startMillis) / (1000 * 60 * 60 * 24)).toInt()
                        ProjectDetailInfoItem(Icons.Default.Timeline, "Durasi", "$durationDays hari")
                    }

                    // --- TOMBOL AKSI DI BAWAH (SINGLE CARD) ---
                    Card(
                        Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(16.dp),
                        colors = CardDefaults.cardColors(Color.White),
                        elevation = CardDefaults.cardElevation(4.dp)
                    ) {
                        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                            Text("Aksi Proyek", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))

                            // Tombol Edit
                            Button(
                                onClick = { onEdit(id) },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.buttonColors(Color(0xFF3949AB))
                            ) {
                                Icon(Icons.Default.Edit, null, Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Edit Proyek", fontWeight = FontWeight.SemiBold)
                            }

                            // Tombol Hapus
                            OutlinedButton(
                                onClick = { showDeleteDialog = true },
                                modifier = Modifier.fillMaxWidth().height(50.dp),
                                shape = RoundedCornerShape(12.dp),
                                colors = ButtonDefaults.outlinedButtonColors(contentColor = Color(0xFFD32F2F)),
                                border = androidx.compose.foundation.BorderStroke(1.5.dp, Color(0xFFD32F2F))
                            ) {
                                Icon(Icons.Default.Delete, null, Modifier.size(20.dp))
                                Spacer(Modifier.width(8.dp))
                                Text("Hapus Proyek", fontWeight = FontWeight.SemiBold)
                            }
                        }
                    }
                }
                Spacer(Modifier.height(30.dp))
            }
        }
    }

    // Dialog Konfirmasi Hapus tetap dipertahankan
    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Hapus Proyek", fontWeight = FontWeight.Bold) },
            text = { Text("Apakah Anda yakin ingin menghapus proyek ini?") },
            confirmButton = {
                Button(
                    onClick = {
                        coroutineScope.launch {
                            viewModel.deleteProject()
                            showDeleteDialog = false
                            onBack()
                        }
                    },
                    colors = ButtonDefaults.buttonColors(Color(0xFFD32F2F))
                ) { Text("Hapus") }
            },
            dismissButton = {
                TextButton(onClick = { showDeleteDialog = false }) { Text("Batal") }
            }
        )
    }
}

@Composable
private fun InfoCard(title: String, content: @Composable ColumnScope.() -> Unit) {
    Card(
        Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(Modifier.padding(20.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
            Text(title, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
            content()
        }
    }
}

@Composable
private fun ProjectDetailInfoItem(icon: ImageVector, label: String, value: String, valueColor: Color = Color(0xFF1A237E)) {
    Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
        Box(Modifier.size(40.dp).clip(RoundedCornerShape(8.dp)).background(Color(0xFF3949AB).copy(alpha = 0.1f)), contentAlignment = Alignment.Center) {
            Icon(icon, null, tint = Color(0xFF3949AB), modifier = Modifier.size(20.dp))
        }
        Spacer(Modifier.width(12.dp))
        Column(Modifier.weight(1f)) {
            Text(text = label, style = MaterialTheme.typography.bodySmall, color = Color.Gray)
            Text(text = value, style = MaterialTheme.typography.bodyLarge, fontWeight = FontWeight.Bold, color = valueColor)
        }
    }
}
