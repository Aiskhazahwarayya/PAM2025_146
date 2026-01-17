package com.example.fhub.view.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Save
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
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
import androidx.compose.ui.unit.dp
import com.example.fhub.R
import com.example.fhub.viewmodel.AuthViewModel
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun EditProfileScreen(
    viewModel: AuthViewModel,
    onBack: () -> Unit
) {
    val authState by viewModel.uiState.collectAsState()
    val user = authState.currentUser
    val coroutineScope = rememberCoroutineScope()
    val snackbarHostState = remember { SnackbarHostState() }

    var nama by remember { mutableStateOf("") }
    var bisnis by remember { mutableStateOf("") }
    var alamat by remember { mutableStateOf("") }
    var isSaving by remember { mutableStateOf(false) }

    // Load data user saat pertama kali
    LaunchedEffect(user) {
        user?.let {
            nama = it.namaLengkap
            bisnis = it.namaBisnis
            alamat = it.alamat
        }
    }

    // Tampilkan pesan "Profil berhasil diperbarui"
    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess && isSaving) {
            coroutineScope.launch {
                snackbarHostState.showSnackbar(
                    message = "Profil berhasil diperbarui",
                    withDismissAction = true
                )
            }
            kotlinx.coroutines.delay(500)
            viewModel.resetState()
            isSaving = false
            onBack()
        }
    }

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = stringResource(R.string.title_edit_profile),
                        fontWeight = FontWeight.Bold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Kembali")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = Color(0xFF1A237E),
                    titleContentColor = Color.White,
                    navigationIconContentColor = Color.White
                )
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(paddingValues)
                .verticalScroll(rememberScrollState())
                .padding(dimensionResource(R.dimen.spacing_normal)),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // Nama Lengkap
            OutlinedTextField(
                value = nama,
                onValueChange = { nama = it },
                label = { Text(stringResource(R.string.label_full_name)) },
                leadingIcon = {
                    Icon(Icons.Default.Person, null, tint = Color(0xFF3949AB))
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3949AB),
                    unfocusedBorderColor = Color(0xFFBDBDBD)
                ),
                singleLine = true
            )

            // Nama Bisnis
            OutlinedTextField(
                value = bisnis,
                onValueChange = { bisnis = it },
                label = { Text(stringResource(R.string.label_business_name)) },
                leadingIcon = {
                    Icon(Icons.Default.Business, null, tint = Color(0xFF3949AB))
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3949AB),
                    unfocusedBorderColor = Color(0xFFBDBDBD)
                ),
                singleLine = true
            )

            // Alamat
            OutlinedTextField(
                value = alamat,
                onValueChange = { alamat = it },
                label = { Text(stringResource(R.string.label_address)) },
                leadingIcon = {
                    Icon(Icons.Default.Home, null, tint = Color(0xFF3949AB))
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !isSaving,
                shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius)),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = Color(0xFF3949AB),
                    unfocusedBorderColor = Color(0xFFBDBDBD)
                ),
                minLines = 3,
                maxLines = 5
            )

            Spacer(modifier = Modifier.height(8.dp))

            // Simpan Button
            Button(
                onClick = {
                    isSaving = true
                    coroutineScope.launch {
                        viewModel.updateProfile(
                            namaLengkap = nama.trim(),
                            namaBisnis = bisnis.trim(),
                            alamat = alamat.trim()
                        )
                    }
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.button_height)),
                enabled = !isSaving && nama.isNotBlank() && bisnis.isNotBlank() && alamat.isNotBlank(),
                shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius)),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF1A237E),
                    disabledContainerColor = Color(0xFFBDBDBD)
                ),
                elevation = ButtonDefaults.buttonElevation(4.dp)
            ) {
                if (isSaving) {
                    CircularProgressIndicator(
                        modifier = Modifier.size(24.dp),
                        color = Color.White,
                        strokeWidth = 2.dp
                    )
                    Spacer(Modifier.width(8.dp))
                    Text(
                        "Menyimpan...",
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                } else {
                    Icon(Icons.Default.Save, null, modifier = Modifier.size(20.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        stringResource(R.string.btn_save),
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }

            // Batal Button
            OutlinedButton(
                onClick = onBack,
                modifier = Modifier
                    .fillMaxWidth()
                    .height(dimensionResource(R.dimen.button_height)),
                enabled = !isSaving,
                shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius)),
                colors = ButtonDefaults.outlinedButtonColors(
                    contentColor = Color(0xFF616161)
                )
            ) {
                Text(
                    stringResource(R.string.btn_cancel),
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.SemiBold
                )
            }
        }
    }
}