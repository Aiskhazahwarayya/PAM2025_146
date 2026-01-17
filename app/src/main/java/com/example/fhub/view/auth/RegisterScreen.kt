package com.example.fhub.view.auth

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.expandVertically
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.shrinkVertically
import androidx.compose.animation.slideInVertically
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Business
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Email
import androidx.compose.material.icons.filled.ErrorOutline
import androidx.compose.material.icons.filled.Lock
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material.icons.filled.VisibilityOff
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.sp
import com.example.fhub.viewmodel.AuthViewModel
import com.example.fhub.R
import com.example.fhub.utils.ValidasiUtils

@Composable
fun RegisterScreen(
    viewModel: AuthViewModel,
    onRegisterSuccess: () -> Unit,
    onNavigateBack: () -> Unit
) {
    var namaLengkap by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var namaBisnis by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var passwordVisible by remember { mutableStateOf(false) }

    val authState by viewModel.uiState.collectAsState()
    var showSuccessDialog by remember { mutableStateOf(false) }
    var showContent by remember { mutableStateOf(false) }

    LaunchedEffect(Unit) { showContent = true }

    LaunchedEffect(authState.isSuccess) {
        if (authState.isSuccess) showSuccessDialog = true
    }

    val isEmailError = email.isNotEmpty() && !ValidasiUtils.isEmailValid(email)
    val isPasswordError = password.isNotEmpty() && !ValidasiUtils.isPasswordValid(password)
    val isFormValid = namaLengkap.isNotBlank() &&
            ValidasiUtils.isEmailValid(email) &&
            namaBisnis.isNotBlank() &&
            ValidasiUtils.isPasswordValid(password)

    if (showSuccessDialog) {
        SuccessDialog(onConfirm = {
            showSuccessDialog = false
            viewModel.resetState()
            onNavigateBack()
        })
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(
                Brush.verticalGradient(
                    colors = listOf(Color(0xFF1A237E), Color(0xFF283593), Color(0xFF3949AB))
                )
            )
    ) {
        RegisterFloatingCircles()

        Column(
            modifier = Modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .padding(horizontal = 24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Spacer(Modifier.height(70.dp))

            // --- HEADER DENGAN ICON BESAR ---
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(800)) + scaleIn(initialScale = 0.8f)
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    // ICON BESAR (DITAMBAHKAN KEMBALI)
                    Icon(
                        Icons.Default.PersonAdd,
                        contentDescription = null,
                        modifier = Modifier.size(80.dp),
                        tint = Color.White
                    )

                    Spacer(Modifier.height(16.dp))

                    Text(
                        text = stringResource(R.string.title_register),
                        style = MaterialTheme.typography.headlineMedium.copy(
                            fontWeight = FontWeight.ExtraBold,
                            fontSize = 26.sp
                        ),
                        color = Color.White
                    )
                    Text(
                        text = "Bergabung dengan FreelanceHub",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.7f)
                    )
                }
            }

            Spacer(Modifier.height(32.dp))

            // --- FORM CARD ---
            AnimatedVisibility(
                visible = showContent,
                enter = fadeIn(tween(800, 200)) + slideInVertically(initialOffsetY = { 50 })
            ) {
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = Color.White),
                    elevation = CardDefaults.cardElevation(defaultElevation = 10.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(24.dp),
                        verticalArrangement = Arrangement.spacedBy(16.dp)
                    ) {
                        Text(
                            text = "Informasi Akun",
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color(0xFF1A237E)
                        )

                        // Input Nama
                        OutlinedTextField(
                            value = namaLengkap,
                            onValueChange = { namaLengkap = it },
                            label = { Text(stringResource(R.string.label_full_name)) },
                            leadingIcon = { Icon(Icons.Default.Person, null, tint = Color(0xFF3949AB)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Input Email
                        OutlinedTextField(
                            value = email,
                            onValueChange = { email = it },
                            label = { Text(stringResource(R.string.label_email)) },
                            leadingIcon = { Icon(Icons.Default.Email, null, tint = Color(0xFF3949AB)) },
                            isError = isEmailError,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            supportingText = if (isEmailError) { { Text(stringResource(R.string.error_email_invalid), color = Color.Red) } } else null
                        )

                        // Input Bisnis
                        OutlinedTextField(
                            value = namaBisnis,
                            onValueChange = { namaBisnis = it },
                            label = { Text(stringResource(R.string.label_business_name)) },
                            leadingIcon = { Icon(Icons.Default.Business, null, tint = Color(0xFF3949AB)) },
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true
                        )

                        // Input Password
                        OutlinedTextField(
                            value = password,
                            onValueChange = { password = it },
                            label = { Text(stringResource(R.string.label_password)) },
                            leadingIcon = { Icon(Icons.Default.Lock, null, tint = Color(0xFF3949AB)) },
                            trailingIcon = {
                                // Icon mata muncul hanya jika field tidak kosong
                                if (password.isNotEmpty()) {
                                    IconButton(onClick = { passwordVisible = !passwordVisible }) {
                                        Icon(
                                            if (passwordVisible) Icons.Default.VisibilityOff else Icons.Default.Visibility,
                                            contentDescription = null
                                        )
                                    }
                                }
                            },
                            visualTransformation = if (passwordVisible) VisualTransformation.None else PasswordVisualTransformation(),
                            isError = isPasswordError,
                            modifier = Modifier.fillMaxWidth(),
                            shape = RoundedCornerShape(12.dp),
                            singleLine = true,
                            supportingText = if (isPasswordError) { { Text(stringResource(R.string.error_password_too_short), color = Color.Red) } } else null
                        )

                        // Error dari ViewModel
                        if (authState.errorMessageId != null) {
                            ErrorMessageBox(message = stringResource(authState.errorMessageId!!))
                        }

                        Spacer(Modifier.height(8.dp))

                        // Tombol Daftar
                        Button(
                            onClick = { viewModel.register(namaLengkap.trim(), namaBisnis.trim(), email.trim(), password) },
                            modifier = Modifier.fillMaxWidth().height(54.dp),
                            enabled = isFormValid && !authState.isLoading,
                            shape = RoundedCornerShape(14.dp),
                            colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))
                        ) {
                            if (authState.isLoading) {
                                CircularProgressIndicator(Modifier.size(24.dp), Color.White, strokeWidth = 2.dp)
                            } else {
                                Text("Daftar Sekarang", fontWeight = FontWeight.Bold, fontSize = 16.sp)
                            }
                        }

                        // Footer ke Login
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.Center,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text("Sudah punya akun?", color = Color.Gray)
                            TextButton(onClick = onNavigateBack) {
                                Text("Masuk", fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                            }
                        }
                    }
                }
            }
            Spacer(Modifier.height(40.dp))
        }
    }
}

@Composable
private fun ErrorMessageBox(message: String) {
    Row(
        modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(8.dp)).background(Color(0xFFFFEBEE)).padding(12.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Icon(Icons.Default.ErrorOutline, null, tint = Color.Red, modifier = Modifier.size(18.dp))
        Spacer(Modifier.width(8.dp))
        Text(text = message, color = Color.Red, fontSize = 12.sp)
    }
}

@Composable
private fun SuccessDialog(onConfirm: () -> Unit) {
    AlertDialog(
        onDismissRequest = {},
        icon = { Icon(Icons.Default.CheckCircle, null, tint = Color(0xFF4CAF50), modifier = Modifier.size(50.dp)) },
        title = { Text("Berhasil!", fontWeight = FontWeight.Bold) },
        text = { Text("Akun Anda telah terdaftar. Silakan login untuk melanjutkan.", textAlign = TextAlign.Center) },
        confirmButton = {
            Button(onClick = onConfirm, modifier = Modifier.fillMaxWidth(), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF1A237E))) {
                Text("Ke Halaman Login")
            }
        },
        shape = RoundedCornerShape(20.dp)
    )
}

@Composable
private fun RegisterFloatingCircles() {
    Box(modifier = Modifier.fillMaxSize()) {
        Box(Modifier.size(200.dp).offset(x = 220.dp, y = (-40).dp).clip(RoundedCornerShape(100.dp))
            .background(Brush.radialGradient(listOf(Color.White.copy(0.1f), Color.Transparent))))
        Box(Modifier.size(150.dp).offset(x = (-50).dp, y = 500.dp).clip(RoundedCornerShape(75.dp))
            .background(Brush.radialGradient(listOf(Color.White.copy(0.07f), Color.Transparent))))
    }
}