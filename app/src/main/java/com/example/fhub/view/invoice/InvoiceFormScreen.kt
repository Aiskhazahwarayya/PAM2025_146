package com.example.fhub.view.invoice



import androidx.compose.foundation.background
import androidx.compose.foundation.clickable

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

import androidx.compose.foundation.lazy.LazyColumn

import androidx.compose.foundation.lazy.itemsIndexed

import androidx.compose.foundation.rememberScrollState

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions

import androidx.compose.foundation.verticalScroll

import androidx.compose.material.icons.Icons

import androidx.compose.material.icons.filled.Add

import androidx.compose.material.icons.filled.ArrowBack

import androidx.compose.material.icons.filled.CalendarToday

import androidx.compose.material.icons.filled.Delete

import androidx.compose.material.icons.filled.ErrorOutline

import androidx.compose.material.icons.filled.Event

import androidx.compose.material.icons.filled.Info

import androidx.compose.material.icons.filled.Numbers

import androidx.compose.material.icons.filled.Receipt

import androidx.compose.material.icons.filled.Save

import androidx.compose.material.icons.filled.Work

import androidx.compose.material3.Button

import androidx.compose.material3.ButtonDefaults

import androidx.compose.material3.Card

import androidx.compose.material3.CardDefaults

import androidx.compose.material3.DatePicker

import androidx.compose.material3.DatePickerDialog

import androidx.compose.material3.DateRangePicker

import androidx.compose.material3.DropdownMenuItem

import androidx.compose.material3.ExperimentalMaterial3Api

import androidx.compose.material3.ExposedDropdownMenuBox

import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.HorizontalDivider

import androidx.compose.material3.Icon

import androidx.compose.material3.IconButton

import androidx.compose.material3.MaterialTheme

import androidx.compose.material3.OutlinedTextField

import androidx.compose.material3.OutlinedTextFieldDefaults

import androidx.compose.material3.Scaffold

import androidx.compose.material3.Snackbar

import androidx.compose.material3.SnackbarHost

import androidx.compose.material3.SnackbarHostState

import androidx.compose.material3.Text

import androidx.compose.material3.TextButton

import androidx.compose.material3.TopAppBar

import androidx.compose.material3.TopAppBarDefaults

import androidx.compose.material3.rememberDatePickerState

import androidx.compose.material3.rememberDateRangePickerState

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect

import androidx.compose.runtime.collectAsState

import androidx.compose.runtime.getValue

import androidx.compose.runtime.mutableStateOf

import androidx.compose.runtime.remember

import androidx.compose.runtime.rememberCoroutineScope

import androidx.compose.runtime.setValue

import androidx.compose.ui.Alignment

import androidx.compose.ui.Modifier

import androidx.compose.ui.graphics.Brush

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext

import androidx.compose.ui.res.dimensionResource

import androidx.compose.ui.res.stringResource

import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType

import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp

import com.example.fhub.R

import com.example.fhub.data.entity.InvoiceItemEntity

import com.example.fhub.utils.FormatterUtils
import com.example.fhub.utils.JenisStatus
import com.example.fhub.utils.ValidasiUtils

import com.example.fhub.viewmodel.EditViewModel

// REVISI IMPORT: Mengacu pada path viewmodel yang benar

import com.example.fhub.viewmodel.EntryViewModel

import com.example.fhub.viewmodel.HomeViewModel

import kotlinx.coroutines.launch

import java.text.SimpleDateFormat

import java.util.Date

import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceFormScreen(
    entryViewModel: EntryViewModel? = null,
    editViewModel: EditViewModel? = null,
    homeViewModel: HomeViewModel,
    onBack: () -> Unit
) {
    val isEdit = editViewModel != null
    val entryState = entryViewModel?.uiStateInvoice?.collectAsState()?.value
    val editState = if (isEdit) editViewModel!!.uiStateInvoice else null

    val detail = if (isEdit) editState!!.detailInvoice else entryState!!.detailInvoice
    val isValidEntry = if (isEdit) editState!!.isEntryValid else entryState!!.isEntryValid
    val errorMessage = if (isEdit) editState!!.errorMessageId else entryState!!.errorMessageId
    val listInvoiceItems = if (isEdit) editViewModel!!.listInvoiceItems else entryViewModel!!.listInvoiceItems

    val homeUiState by homeViewModel.uiState.collectAsState()
    val listProject = homeUiState.listProject

    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    var expandedProject by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }
    var showIssueDatePicker by remember { mutableStateOf(false) }
    var showDueDatePicker by remember { mutableStateOf(false) }

    var tempDeskripsi by remember { mutableStateOf("") }
    var tempHarga by remember { mutableStateOf("") }

    LaunchedEffect(errorMessage) {
        errorMessage?.let { id -> snackbarHostState.showSnackbar(context.getString(id)) }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        containerColor = Color(0xFFF5F5F5)
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = padding.calculateBottomPadding())
                .verticalScroll(rememberScrollState())
        ) {
            // --- HEADER SECTION  ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF1A237E), Color(0xFF3949AB))))
                    .padding(top = 48.dp, bottom = 64.dp)
            ) {
                IconButton(onClick = onBack, modifier = Modifier.padding(start = 8.dp, top = 8.dp)) {
                    Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Receipt, null, tint = Color.White, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (isEdit) "Edit Invoice" else "Buat Invoice",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold, color = Color.White, letterSpacing = 0.5.sp
                        )
                    )
                    Text(
                        text = "Kelola tagihan pembayaran proyek Anda",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            // --- FORM SECTION---
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-32).dp),
                verticalArrangement = Arrangement.spacedBy(20.dp)
            ) {
                // 1. KARTU INFORMASI UTAMA
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Informasi Tagihan", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))

                        // Dropdown Project
                        ExposedDropdownMenuBox(expanded = expandedProject, onExpandedChange = { expandedProject = !expandedProject }) {
                            val selectedProject = listProject.find { it.idProject == detail.idProject }
                            OutlinedTextField(
                                value = selectedProject?.namaProject ?: "", onValueChange = {}, readOnly = true,
                                label = { Text("Pilih Proyek") },
                                leadingIcon = { Icon(Icons.Default.Work, null, tint = Color(0xFF3949AB)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedProject) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(expanded = expandedProject, onDismissRequest = { expandedProject = false }) {
                                listProject.forEach { project ->
                                    DropdownMenuItem(
                                        text = { Text(project.namaProject) },
                                        onClick = {
                                            if (isEdit) editViewModel!!.updateUiStateInvoice(detail.copy(idProject = project.idProject, idKlien = project.idKlien))
                                            else entryViewModel!!.onProjectSelected(project)
                                            expandedProject = false
                                        }
                                    )
                                }
                            }
                        }

                        // Nomor Invoice
                        OutlinedTextField(
                            value = detail.invoiceNumber,
                            onValueChange = {
                                val d = detail.copy(invoiceNumber = it)
                                if (isEdit) editViewModel!!.updateUiStateInvoice(d) else entryViewModel!!.updateUiStateInvoice(d)
                            },
                            label = { Text("Nomor Invoice") },
                            leadingIcon = { Icon(Icons.Default.Numbers, null, tint = Color(0xFF3949AB)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )

                        // Tanggal Terbit & Jatuh Tempo
                        Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = FormatterUtils.formatTanggal(detail.issueDate), onValueChange = {}, readOnly = true,
                                    label = { Text("Terbit") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(Modifier.matchParentSize().clickable { showIssueDatePicker = true })
                            }
                            Box(modifier = Modifier.weight(1f)) {
                                OutlinedTextField(
                                    value = FormatterUtils.formatTanggal(detail.dueDate), onValueChange = {}, readOnly = true,
                                    label = { Text("Tempo") },
                                    modifier = Modifier.fillMaxWidth(),
                                    shape = RoundedCornerShape(12.dp)
                                )
                                Box(Modifier.matchParentSize().clickable { showDueDatePicker = true })
                            }
                        }

                        // Dropdown Status
                        ExposedDropdownMenuBox(expanded = expandedStatus, onExpandedChange = { expandedStatus = !expandedStatus }) {
                            OutlinedTextField(
                                value = detail.status, onValueChange = {}, readOnly = true,
                                label = { Text("Status Pembayaran") },
                                leadingIcon = { Icon(Icons.Default.Info, null, tint = Color(0xFF3949AB)) },
                                trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedStatus) },
                                modifier = Modifier.menuAnchor().fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            ExposedDropdownMenu(expanded = expandedStatus, onDismissRequest = { expandedStatus = false }) {
                                JenisStatus.listStatusInvoice.forEach { status ->
                                    DropdownMenuItem(text = { Text(status) }, onClick = {
                                        val d = detail.copy(status = status)
                                        if (isEdit) editViewModel!!.updateUiStateInvoice(d) else entryViewModel!!.updateUiStateInvoice(d)
                                        expandedStatus = false
                                    })
                                }
                            }
                        }
                    }
                }

                // 2. KARTU RINCIAN ITEM
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(8.dp)
                ) {
                    Column(modifier = Modifier.padding(24.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Text("Rincian Item Tagihan", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))

                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            OutlinedTextField(
                                value = tempDeskripsi, onValueChange = { tempDeskripsi = it },
                                label = { Text("Deskripsi") }, modifier = Modifier.weight(1.4f),
                                shape = RoundedCornerShape(12.dp)
                            )
                            OutlinedTextField(
                                value = tempHarga, onValueChange = { if(it.all { c -> c.isDigit() }) tempHarga = it },
                                label = { Text("Harga") }, modifier = Modifier.weight(1f),
                                shape = RoundedCornerShape(12.dp),
                                keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                            )
                        }

                        Button(
                            onClick = {
                                val harga = tempHarga.toDoubleOrNull() ?: 0.0
                                if (tempDeskripsi.isNotBlank() && harga > 0) {
                                    if (isEdit) editViewModel!!.tambahItemEdit(tempDeskripsi, harga) else entryViewModel!!.tambahItemInvoice(tempDeskripsi, harga)
                                    tempDeskripsi = ""; tempHarga = ""
                                }
                            },
                            modifier = Modifier.fillMaxWidth().height(48.dp),
                            shape = RoundedCornerShape(12.dp),
                            enabled = tempDeskripsi.isNotBlank() && (tempHarga.toDoubleOrNull() ?: 0.0) > 0,
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF3949AB))
                        ) {
                            Icon(Icons.Default.Add, null)
                            Spacer(Modifier.width(8.dp))
                            Text("Tambah Item")
                        }

                        // List Item yang sudah ditambahkan
                        listInvoiceItems.forEach { item ->
                            Card(
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp),
                                colors = CardDefaults.cardColors(containerColor = Color(0xFFE8EAF6))
                            ) {
                                Row(Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
                                    Column(Modifier.weight(1f)) {
                                        Text(item.deskripsi, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                                        Text(FormatterUtils.formatRupiah(context, item.harga), color = Color(0xFF2E7D32))
                                    }
                                    IconButton(onClick = { if (isEdit) editViewModel!!.hapusItemEdit(item) else entryViewModel!!.hapusItemInvoice(item) }) {
                                        Icon(Icons.Default.Delete, null, tint = Color.Red)
                                    }
                                }
                            }
                        }

                        // Total Terakumulasi
                        HorizontalDivider(thickness = 1.dp, color = Color(0xFFE0E0E0))
                        Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Total Tagihan", style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold)
                            Text(
                                FormatterUtils.formatRupiah(context, detail.total),
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.ExtraBold,
                                color = Color(0xFF1A237E)
                            )
                        }
                    }
                }

                // 3. TOMBOL SIMPAN UTAMA
                Button(
                    onClick = {
                        coroutineScope.launch {
                            if (isEdit) editViewModel!!.saveUpdateInvoiceLengkap() else entryViewModel!!.saveInvoiceLengkap()
                            if (errorMessage == null) onBack()
                        }
                    },
                    modifier = Modifier.fillMaxWidth().height(56.dp),
                    enabled = isValidEntry && listInvoiceItems.isNotEmpty(),
                    shape = RoundedCornerShape(16.dp),
                    colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                ) {
                    Icon(Icons.Default.Save, null)
                    Spacer(Modifier.width(10.dp))
                    Text("Simpan Invoice", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                }
            }
        }
    }

    // Dialog Picker Terbit
    if (showIssueDatePicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showIssueDatePicker = false },
            confirmButton = { TextButton({
                state.selectedDateMillis?.let {
                    val d = detail.copy(issueDate = java.sql.Date(it))
                    if (isEdit) editViewModel!!.updateUiStateInvoice(d) else entryViewModel!!.updateUiStateInvoice(d)
                }
                showIssueDatePicker = false
            }) { Text("OK") } },
            dismissButton = { TextButton({ showIssueDatePicker = false }) { Text("Batal") } }
        ) { DatePicker(state) }
    }

    // Dialog Picker Jatuh Tempo
    if (showDueDatePicker) {
        val state = rememberDatePickerState()
        DatePickerDialog(
            onDismissRequest = { showDueDatePicker = false },
            confirmButton = { TextButton({
                state.selectedDateMillis?.let {
                    val d = detail.copy(dueDate = java.sql.Date(it))
                    if (isEdit) editViewModel!!.updateUiStateInvoice(d) else entryViewModel!!.updateUiStateInvoice(d)
                }
                showDueDatePicker = false
            }) { Text("OK") } },
            dismissButton = { TextButton({ showDueDatePicker = false }) { Text("Batal") } }
        ) { DatePicker(state) }
    }
}