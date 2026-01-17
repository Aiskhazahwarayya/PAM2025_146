package com.example.fhub.view.dasboard


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
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccessTime
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.PersonAdd
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.getValue
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import com.example.fhub.viewmodel.HomeViewModel
import com.example.fhub.R
import com.example.fhub.utils.FormatterUtils
import java.util.Calendar
import java.util.Date
import java.util.concurrent.TimeUnit

@Composable
fun HomeScreen(
    viewModel: HomeViewModel,
    onNavigateToKlien: () -> Unit,
    onNavigateToProject: () -> Unit,
    onNavigateToInvoice: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToAddKlien: () -> Unit,
    onNavigateToAddProject: () -> Unit,
    onNavigateToAddInvoice: () -> Unit
) {
    val homeUiState by viewModel.uiState.collectAsState()
    val upcomingDeadlines = homeUiState.listProyekMendesak

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp)
            {
                // Menu Navigasi Bawah
                NavigationBarItem(
                    selected = true,
                    onClick = { }, // Sudah di Home
                    icon = { Icon(Icons.Default.Home, null) },
                    label = { Text(stringResource(R.string.nav_home)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToKlien,
                    icon = { Icon(Icons.Default.People, null) },
                    label = { Text(stringResource(R.string.nav_clients)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProject,
                    icon = { Icon(Icons.Default.Work, null) },
                    label = { Text(stringResource(R.string.nav_projects)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToInvoice,
                    icon = { Icon(Icons.Default.Receipt, null) },
                    label = { Text(stringResource(R.string.nav_invoices)) }
                )
                NavigationBarItem(
                    selected = false,
                    onClick = onNavigateToProfile,
                    icon = { Icon(Icons.Default.Person, null) },
                    label = { Text(stringResource(R.string.nav_profile)) }
                )
            }
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(bottom = padding.calculateBottomPadding())

        ) {
            // ================= HEADER DASHBOARD =================
            item {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .background(
                            Brush.verticalGradient(
                                colors = listOf(Color(0xFF1A237E), Color(0xFF283593), Color(0xFF3949AB))
                            )
                        )
                        .padding(top = 50.dp, bottom = 38.dp)
                        .padding(horizontal = 20.dp)
                ) {
                    Column {
                        Text(
                            text = stringResource(
                                R.string.greeting_hello,
                                homeUiState.namaUser.ifEmpty { "User" }
                            ),
                            style = MaterialTheme.typography.headlineMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )

                        Spacer(Modifier.height(dimensionResource(R.dimen.spacing_large)))

                        // Statistik Ringkas
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(16.dp)
                        ) {
                            CompactStatCard(
                                modifier = Modifier.weight(1f),
                                count = homeUiState.totalKlien,
                                label = stringResource(R.string.home_total_klien),
                                icon = Icons.Default.People
                            )

                            CompactStatCard(
                                modifier = Modifier.weight(1f),
                                count = upcomingDeadlines.size,
                                label = stringResource(R.string.home_deadline_approaching),
                                icon = Icons.Default.Warning
                            )
                        }
                    }
                }
            }

            // ================= AKSI CEPAT  =================
            item {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 20.dp),
                ) {
                    // Spacer ini memberikan jarak (space) agar tidak naik menimpa biru
                    Spacer(Modifier.height(20.dp))

                    Text(
                        text = stringResource(R.string.home_quick_actions),
                        style = MaterialTheme.typography.titleLarge.copy(fontWeight = FontWeight.Bold),
                        color = Color(0xFF1A237E)
                    )

                    Column(verticalArrangement = Arrangement.spacedBy(15.dp)) {
                        QuickActionCard(
                            Icons.Default.PersonAdd,
                            "Tambah Klien",
                            "Tambahkan klien baru ke daftar",
                            listOf(Color(0xFF66BB6A), Color(0xFF43A047)),
                            onNavigateToAddKlien
                        )
                        QuickActionCard(
                            Icons.Default.Add,
                            "Tambah Proyek",
                            "Buat proyek baru untuk klien",
                            listOf(Color(0xFF42A5F5), Color(0xFF1E88E5)),
                            onNavigateToAddProject
                        )
                        QuickActionCard(
                            Icons.Default.Receipt,
                            "Buat Invoice",
                            "Buat tagihan untuk proyek",
                            listOf(Color(0xFFFFA726), Color(0xFFFB8C00)),
                            onNavigateToAddInvoice
                        )
                    }
                }
            }

            item { Spacer(Modifier.height(5.dp)) }

            // ================= DEADLINE MENDEKATI  =================
            if (upcomingDeadlines.isNotEmpty()) {
                item {
                    Column(modifier = Modifier.padding(horizontal = 24.dp)) {
                        Spacer(Modifier.height(12.dp))
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.SpaceBetween,
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Text(
                                text = stringResource(R.string.home_deadline_approaching),
                                style = MaterialTheme.typography.titleLarge,
                                fontWeight = FontWeight.Bold,
                                color = Color(0xFF1A237E)
                            )
                            Text(
                                text = stringResource(R.string.home_view_all),
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color(0xFF3949AB),
                                fontWeight = FontWeight.SemiBold,
                                modifier = Modifier.clickable { onNavigateToProject() }
                            )
                        }
                        Spacer(Modifier.height(12.dp))
                    }
                }

                items(upcomingDeadlines) { project ->
                    DeadlineCard(
                        modifier = Modifier.padding(horizontal = 24.dp, vertical = 6.dp),
                        projectName = project.namaProject,
                        deadline = project.deadline,
                        description = project.deskripsi ?: "-",
                        onClick = { }
                    )
                }
            }

            item { Spacer(Modifier.height(100.dp)) }
        }
    }
}

// ================= KOMPONEN UI PENDUKUNG =================

@Composable
fun CompactStatCard(
    modifier: Modifier = Modifier,
    count: Int,
    label: String,
    icon: ImageVector
) {
    Card(
        modifier = modifier.height(100.dp),
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = Color.White.copy(alpha = 0.15f)
        ),
        elevation = CardDefaults.cardElevation(0.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(12.dp),
            verticalArrangement = Arrangement.SpaceBetween
        ) {
            Icon(
                icon,
                contentDescription = null,
                tint = Color.White,
                modifier = Modifier.size(24.dp)
            )

            Column {
                Text(
                    text = "$count",
                    style = MaterialTheme.typography.headlineMedium,
                    fontWeight = FontWeight.Bold,
                    color = Color.White
                )
                Text(
                    text = label,
                    style = MaterialTheme.typography.bodySmall,
                    color = Color.White.copy(alpha = 0.9f)
                )
            }
        }
    }
}

@Composable
fun QuickActionCard(
    icon: ImageVector,
    label: String,
    description: String,
    gradient: List<Color>,
    onClick: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.Transparent),
        elevation = CardDefaults.cardElevation(6.dp)
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .background(Brush.horizontalGradient(gradient))
                .padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    modifier = Modifier.weight(1f)
                ) {
                    Box(
                        modifier = Modifier
                            .size(56.dp)
                            .clip(CircleShape)
                            .background(Color.White.copy(alpha = 0.3f)),
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            icon,
                            contentDescription = null,
                            tint = Color.White,
                            modifier = Modifier.size(28.dp)
                        )
                    }

                    Spacer(Modifier.width(16.dp))

                    Column(modifier = Modifier.weight(1f)) {
                        Text(
                            text = label,
                            style = MaterialTheme.typography.titleMedium,
                            fontWeight = FontWeight.Bold,
                            color = Color.White
                        )
                        Text(
                            text = description,
                            style = MaterialTheme.typography.bodySmall,
                            color = Color.White.copy(alpha = 0.9f)
                        )
                    }
                }

                Icon(
                    Icons.Default.ArrowForward,
                    contentDescription = null,
                    tint = Color.White,
                    modifier = Modifier.size(24.dp)
                )
            }
        }
    }
}

@Composable
fun DeadlineCard(
    modifier: Modifier = Modifier,
    projectName: String,
    deadline: Any?,
    description: String,
    onClick: () -> Unit
) {
    // Menghitung sisa hari
    val daysUntil = getDaysUntilDeadline(deadline)

    // Menentukan warna urgensi (Merah = Lewat/Hari ini, Oranye = < 2 hari, Kuning = < 5 hari)
    val urgencyColor = when {
        daysUntil <= 0 -> Color(0xFFD32F2F)
        daysUntil <= 2 -> Color(0xFFFF5722)
        daysUntil <= 5 -> Color(0xFFFF9800)
        else -> Color(0xFFFFC107)
    }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .clickable(enabled = false) { onClick() },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Box(
                    modifier = Modifier
                        .size(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(urgencyColor.copy(alpha = 0.1f)),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        Icons.Default.Warning,
                        contentDescription = null,
                        tint = urgencyColor,
                        modifier = Modifier.size(24.dp)
                    )
                }

                Spacer(Modifier.width(12.dp))

                Column(modifier = Modifier.weight(1f)) {
                    Text(
                        text = projectName,
                        style = MaterialTheme.typography.titleMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color(0xFF1A237E),
                        maxLines = 1
                    )
                    Text(
                        text = description,
                        style = MaterialTheme.typography.bodySmall,
                        color = Color.Gray,
                        maxLines = 1
                    )
                    Spacer(Modifier.height(4.dp))
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        Icon(
                            Icons.Default.AccessTime,
                            contentDescription = null,
                            tint = urgencyColor,
                            modifier = Modifier.size(16.dp)
                        )
                        Spacer(Modifier.width(4.dp))
                        Text(
                            text = if(daysUntil <= 0) "Segera / Lewat" else "$daysUntil hari lagi",
                            style = MaterialTheme.typography.bodySmall,
                            color = urgencyColor,
                            fontWeight = FontWeight.Bold
                        )
                    }
                }
            }

            Box(
                modifier = Modifier
                    .clip(RoundedCornerShape(8.dp))
                    .background(urgencyColor.copy(alpha = 0.1f))
                    .padding(horizontal = 12.dp, vertical = 6.dp)
            ) {
                // Menggunakan FormatterUtils untuk format tanggal
                Text(
                    text = FormatterUtils.formatTanggal(deadline),
                    style = MaterialTheme.typography.bodySmall,
                    fontWeight = FontWeight.SemiBold,
                    color = urgencyColor
                )
            }
        }
    }
}

// Helper Functions
private fun getDaysUntilDeadline(deadline: Any?): Int {
    return try {
        // Konversi aman dari berbagai tipe (Long/Timestamp/Date)
        val deadlineDate = when (deadline) {
            is Long -> Date(deadline)
            is java.sql.Timestamp -> Date(deadline.time)
            is java.sql.Date -> Date(deadline.time)
            is Date -> deadline
            else -> return 0
        }

        val today = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }.time

        val diffInMillis = deadlineDate.time - today.time
        TimeUnit.MILLISECONDS.toDays(diffInMillis).toInt()
    } catch (e: Exception) {
        0
    }
}