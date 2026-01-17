package com.example.fhub.view.project

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.AttachMoney
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CalendarToday
import androidx.compose.material.icons.filled.Description
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Event
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.Save
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DatePicker
import androidx.compose.material3.DatePickerDialog
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.material3.rememberDatePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.room.util.copy
import com.example.fhub.R
import com.example.fhub.data.entity.KlienEntity
import com.example.fhub.utils.JenisStatus
import com.example.fhub.utils.ValidasiUtils
import com.example.fhub.viewmodel.DetailProjectModel
import java.util.Date
import kotlinx.coroutines.launch
import java.sql.Timestamp
import java.text.SimpleDateFormat
import java.util.Locale

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProjectFormScreen(
    isEdit: Boolean = false,
    detailProject: DetailProjectModel,
    klienList: List<KlienEntity>,
    onValueChange: (DetailProjectModel) -> Unit,
    onSaveClick: suspend () -> Unit,
    onBack: () -> Unit
) {
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }
    val dateFormatter = remember { SimpleDateFormat("dd/MM/yyyy", Locale("id", "ID")) }

    var expandedKlien by remember { mutableStateOf(false) }
    var expandedStatus by remember { mutableStateOf(false) }
    var showStartDatePicker by remember { mutableStateOf(false) }
    var showDeadlinePicker by remember { mutableStateOf(false) }

    // State Error
    var namaError by remember { mutableStateOf<String?>(null) }
    var anggaranError by remember { mutableStateOf<String?>(null) }
    var deadlineError by remember { mutableStateOf<String?>(null) }
    var tanggalMulaiError by remember { mutableStateOf<String?>(null) }

    val msgEmpty = stringResource(R.string.error_empty_field)
    val msgDateRange = stringResource(R.string.error_invalid_date_range)

    fun validateForm(): Boolean {
        var isValid = true
        if (detailProject.namaProject.isBlank()) { namaError = msgEmpty; isValid = false }
        if (!ValidasiUtils.isHargaValid(detailProject.anggaran.toString())) { anggaranError = msgEmpty; isValid = false }
        if (!ValidasiUtils.isTanggalInputValid(detailProject.tanggalMulai)) {
            tanggalMulaiError = "Min. hari ini"; isValid = false
        } else { tanggalMulaiError = null }
        if (!ValidasiUtils.isRangeTanggalValid(detailProject.tanggalMulai, detailProject.deadline)) {
            deadlineError = msgDateRange; isValid = false
        } else { deadlineError = null }
        return isValid
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
            // --- HEADER SECTION (FULL TOP) ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        Brush.verticalGradient(
                            colors = listOf(Color(0xFF1A237E), Color(0xFF3949AB))
                        )
                    )
                    .padding(top = 48.dp, bottom = 64.dp)
            ) {
                IconButton(
                    onClick = onBack,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Icon(Icons.Default.Work, null, tint = Color.White, modifier = Modifier.size(56.dp))
                    Spacer(Modifier.height(12.dp))
                    Text(
                        text = if (isEdit) "Edit Proyek" else "Proyek Baru",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold,
                            color = Color.White,
                            letterSpacing = 0.5.sp
                        )
                    )
                }
            }

            // --- FORM CARD (OVERLAP) ---
            Card(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(horizontal = 20.dp)
                    .offset(y = (-32).dp),
                shape = RoundedCornerShape(24.dp),
                colors = CardDefaults.cardColors(containerColor = Color.White),
                elevation = CardDefaults.cardElevation(defaultElevation = 8.dp)
            ) {
                Column(
                    modifier = Modifier.padding(24.dp),
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Informasi Proyek",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E)
                    )

                    // 1. DROPDOWN KLIEN
                    ExposedDropdownMenuBox(
                        expanded = expandedKlien,
                        onExpandedChange = { expandedKlien = !expandedKlien }
                    ) {
                        val selectedKlien = klienList.find { it.idKlien == detailProject.idKlien }
                        OutlinedTextField(
                            value = selectedKlien?.namaPerusahaan ?: "",
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Pilih Klien") },
                            leadingIcon = { Icon(Icons.Default.Business, null, tint = Color(0xFF3949AB)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedKlien) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A237E))
                        )
                        ExposedDropdownMenu(expanded = expandedKlien, onDismissRequest = { expandedKlien = false }) {
                            if (klienList.isEmpty()) {
                                DropdownMenuItem(text = { Text("Belum ada klien") }, onClick = { expandedKlien = false })
                            } else {
                                klienList.forEach { klien ->
                                    DropdownMenuItem(
                                        text = { Text(klien.namaPerusahaan) },
                                        onClick = {
                                            onValueChange(detailProject.copy(idKlien = klien.idKlien))
                                            expandedKlien = false
                                        }
                                    )
                                }
                            }
                        }
                    }

                    // 2. NAMA PROYEK
                    FormTextField(
                        value = detailProject.namaProject,
                        onValueChange = { onValueChange(detailProject.copy(namaProject = it)); namaError = null },
                        label = "Nama Proyek",
                        icon = Icons.Default.Work,
                        isError = namaError != null,
                        errorText = namaError
                    )

                    // 3. ANGGARAN
                    FormTextField(
                        value = if (detailProject.anggaran == 0.0) "" else detailProject.anggaran.toString(),
                        onValueChange = {
                            val filtered = it.filter { char -> char.isDigit() || char == '.' }
                            onValueChange(detailProject.copy(anggaran = filtered.toDoubleOrNull() ?: 0.0))
                            anggaranError = null
                        },
                        label = "Anggaran (Rp)",
                        icon = Icons.Default.AttachMoney,
                        isError = anggaranError != null,
                        errorText = anggaranError,
                        keyboardType = KeyboardType.Number
                    )

                    // 4. TANGGAL MULAI & DEADLINE
                    Row(horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = if (detailProject.tanggalMulai == null) "" else dateFormatter.format(detailProject.tanggalMulai),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Mulai") },
                                isError = tanggalMulaiError != null,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Box(Modifier.matchParentSize().clickable { showStartDatePicker = true })
                        }
                        Box(modifier = Modifier.weight(1f)) {
                            OutlinedTextField(
                                value = if (detailProject.deadline == null) "" else dateFormatter.format(detailProject.deadline),
                                onValueChange = {},
                                readOnly = true,
                                label = { Text("Deadline") },
                                isError = deadlineError != null,
                                modifier = Modifier.fillMaxWidth(),
                                shape = RoundedCornerShape(12.dp)
                            )
                            Box(Modifier.matchParentSize().clickable { showDeadlinePicker = true })
                        }
                    }

                    if (tanggalMulaiError != null || deadlineError != null) {
                        Text(
                            text = tanggalMulaiError ?: deadlineError!!,
                            color = Color.Red,
                            style = MaterialTheme.typography.bodySmall
                        )
                    }

                    // 5. STATUS
                    ExposedDropdownMenuBox(
                        expanded = expandedStatus,
                        onExpandedChange = { expandedStatus = !expandedStatus }
                    ) {
                        OutlinedTextField(
                            value = detailProject.status,
                            onValueChange = {},
                            readOnly = true,
                            label = { Text("Status Proyek") },
                            leadingIcon = { Icon(Icons.Default.Info, null, tint = Color(0xFF3949AB)) },
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expandedStatus) },
                            modifier = Modifier.menuAnchor().fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp)
                        )
                        ExposedDropdownMenu(expanded = expandedStatus, onDismissRequest = { expandedStatus = false }) {
                            JenisStatus.listStatusProyek.forEach { status ->
                                DropdownMenuItem(
                                    text = { Text(status) },
                                    onClick = {
                                        onValueChange(detailProject.copy(status = status))
                                        expandedStatus = false
                                    }
                                )
                            }
                        }
                    }

                    // 6. DESKRIPSI
                    OutlinedTextField(
                        value = detailProject.deskripsi,
                        onValueChange = { onValueChange(detailProject.copy(deskripsi = it)) },
                        label = { Text("Deskripsi Singkat") },
                        modifier = Modifier.fillMaxWidth().height(100.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(focusedBorderColor = Color(0xFF1A237E))
                    )

                    Spacer(Modifier.height(12.dp))

                    // TOMBOL SIMPAN
                    Button(
                        onClick = {
                            if (detailProject.idKlien == 0) {
                                coroutineScope.launch { snackbarHostState.showSnackbar("Pilih klien terlebih dahulu") }
                            } else if (validateForm()) {
                                coroutineScope.launch { onSaveClick(); onBack() }
                            }
                        },
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                    ) {
                        Icon(Icons.Default.Save, null)
                        Spacer(Modifier.width(10.dp))
                        Text("Simpan Proyek", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                    }
                }
            }
        }
    }

    // --- DATE PICKER DIALOGS (TETAP SAMA) ---
    if (showStartDatePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = detailProject.tanggalMulai?.time ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showStartDatePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onValueChange(detailProject.copy(tanggalMulai = Timestamp(it)))
                        tanggalMulaiError = null
                    }
                    showStartDatePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showStartDatePicker = false }) { Text("Batal") } }
        ) { DatePicker(state = datePickerState) }
    }

    if (showDeadlinePicker) {
        val datePickerState = rememberDatePickerState(
            initialSelectedDateMillis = detailProject.deadline?.time ?: System.currentTimeMillis()
        )
        DatePickerDialog(
            onDismissRequest = { showDeadlinePicker = false },
            confirmButton = {
                TextButton(onClick = {
                    datePickerState.selectedDateMillis?.let {
                        onValueChange(detailProject.copy(deadline = Timestamp(it)))
                        deadlineError = null
                    }
                    showDeadlinePicker = false
                }) { Text("OK") }
            },
            dismissButton = { TextButton(onClick = { showDeadlinePicker = false }) { Text("Batal") } }
        ) { DatePicker(state = datePickerState) }
    }
}

@Composable
private fun FormTextField(
    value: String,
    onValueChange: (String) -> Unit,
    label: String,
    icon: androidx.compose.ui.graphics.vector.ImageVector,
    isError: Boolean = false,
    errorText: String? = null,
    keyboardType: KeyboardType = KeyboardType.Text
) {
    Column {
        OutlinedTextField(
            value = value,
            onValueChange = onValueChange,
            label = { Text(label) },
            leadingIcon = { Icon(icon, null, tint = Color(0xFF3949AB)) },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1A237E),
                errorBorderColor = Color.Red
            )
        )
        if (isError && errorText != null) {
            Text(text = errorText, color = Color.Red, style = MaterialTheme.typography.bodySmall, modifier = Modifier.padding(start = 12.dp, top = 4.dp))
        }
    }
}