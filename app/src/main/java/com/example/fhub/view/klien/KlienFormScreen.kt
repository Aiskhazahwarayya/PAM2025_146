package com.example.fhub.view.klien

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Phone
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
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
import com.example.fhub.utils.ValidasiUtils
import com.example.fhub.viewmodel.DetailKlien
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun KlienFormScreen(
    isEdit: Boolean = false,
    detailKlien: DetailKlien,
    isEntryValid: Boolean,
    isSaveSuccess: Boolean = false,
    errorMessageId: Int? = null,
    onValueChange: (DetailKlien) -> Unit,
    onSaveClick: () -> Unit,
    onNavigateBack: () -> Unit
) {
    val snackbarHostState = remember { SnackbarHostState() }
    val context = LocalContext.current

    // Validasi resmi dari ValidasiUtils
    val isEmailError = detailKlien.email.isNotEmpty() && !ValidasiUtils.isEmailValid(detailKlien.email)
    val isPhoneError = detailKlien.telepon.isNotEmpty() && !ValidasiUtils.isTeleponValid(detailKlien.telepon)

    LaunchedEffect(isSaveSuccess) {
        if (isSaveSuccess) onNavigateBack()
    }

    LaunchedEffect(errorMessageId) {
        errorMessageId?.let { id ->
            snackbarHostState.showSnackbar(context.getString(id))
        }
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
            // --- HEADER SECTION ---
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF1A237E), Color(0xFF3949AB))))
                    .padding(top = 48.dp, bottom = 64.dp)
            ) {
                IconButton(
                    onClick = onNavigateBack,
                    modifier = Modifier.padding(start = 8.dp, top = 8.dp)
                ) {
                    Icon(Icons.Default.ArrowBack, "Kembali", tint = Color.White)
                }

                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Text(
                        text = if (isEdit) "Edit Data" else "Tambah Klien Baru",
                        style = MaterialTheme.typography.headlineSmall.copy(
                            fontWeight = FontWeight.ExtraBold, color = Color.White
                        )
                    )
                }
            }

            // --- FORM CARD (URUTAN: NAMA, PERUSAHAAN, TELEPON, EMAIL, ALAMAT) ---
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
                    Text("Informasi Dasar", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))

                    // 1. Nama Lengkap
                    FormTextField(
                        value = detailKlien.namaLengkap,
                        onValueChange = { onValueChange(detailKlien.copy(namaLengkap = it)) },
                        label = "Nama Lengkap",
                        icon = Icons.Default.Person
                    )

                    // 2. Nama Perusahaan
                    FormTextField(
                        value = detailKlien.namaPerusahaan,
                        onValueChange = { onValueChange(detailKlien.copy(namaPerusahaan = it)) },
                        label = "Nama Perusahaan",
                        icon = Icons.Default.Business
                    )

                    // 3. Nomor Telepon (Validasi 08xxx)
                    FormTextField(
                        value = detailKlien.telepon,
                        onValueChange = { onValueChange(detailKlien.copy(telepon = it)) },
                        label = "Nomor Telepon",
                        icon = Icons.Default.Phone,
                        isError = isPhoneError,
                        errorText = "Harus angka 08 (12-13 digit)",
                        keyboardType = KeyboardType.Phone
                    )

                    // 4. Email Klien
                    FormTextField(
                        value = detailKlien.email,
                        onValueChange = { onValueChange(detailKlien.copy(email = it)) },
                        label = "Email Klien",
                        icon = Icons.Default.Email,
                        isError = isEmailError,
                        errorText = "Format email tidak valid",
                        keyboardType = KeyboardType.Email
                    )

                    // 5. Alamat Kantor / Rumah
                    OutlinedTextField(
                        value = detailKlien.alamat,
                        onValueChange = { onValueChange(detailKlien.copy(alamat = it)) },
                        label = { Text("Alamat Kantor / Rumah") },
                        modifier = Modifier.fillMaxWidth().height(110.dp),
                        shape = RoundedCornerShape(12.dp),
                        colors = OutlinedTextFieldDefaults.colors(
                            focusedBorderColor = Color(0xFF1A237E),
                            unfocusedBorderColor = Color(0xFFE0E0E0)
                        )
                    )

                    Spacer(Modifier.height(8.dp))

                    // Tombol Simpan
                    Button(
                        onClick = onSaveClick,
                        modifier = Modifier.fillMaxWidth().height(56.dp),
                        enabled = isEntryValid && !isEmailError && !isPhoneError,
                        shape = RoundedCornerShape(16.dp),
                        colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                    ) {
                        Icon(Icons.Default.Check, null)
                        Spacer(Modifier.width(8.dp))
                        Text("Simpan", fontWeight = FontWeight.Bold)
                    }
                }
            }
        }
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
            leadingIcon = { Icon(icon, null, tint = if (isError) Color.Red else Color(0xFF3949AB)) },
            modifier = Modifier.fillMaxWidth(),
            isError = isError,
            singleLine = true,
            shape = RoundedCornerShape(12.dp),
            keyboardOptions = KeyboardOptions(keyboardType = keyboardType),
            colors = OutlinedTextFieldDefaults.colors(
                focusedBorderColor = Color(0xFF1A237E),
                unfocusedBorderColor = Color(0xFFE0E0E0),
                errorBorderColor = Color.Red
            )
        )
        if (isError && errorText != null) {
            Text(
                text = errorText,
                color = Color.Red,
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(start = 12.dp, top = 4.dp)
            )
        }
    }
}