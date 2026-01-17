package com.example.fhub.view.invoice

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.ChevronRight
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.People
import androidx.compose.material.icons.filled.Person
import androidx.compose.material.icons.filled.Receipt
import androidx.compose.material.icons.filled.ReceiptLong
import androidx.compose.material.icons.filled.Work
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.setValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.dimensionResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.fhub.R
import com.example.fhub.data.entity.InvoiceEntity
import com.example.fhub.viewmodel.HomeViewModel
import com.example.fhub.utils.FormatterUtils
import com.example.fhub.utils.JenisStatus


@Composable
fun InvoiceListScreen(
    vm: HomeViewModel,
    onDetail: (Int) -> Unit,
    onAdd: () -> Unit,
    onHome: () -> Unit,
    onKlien: () -> Unit,
    onProject: () -> Unit,
    onProfile: () -> Unit
) {
    val homeUiState by vm.uiState.collectAsState()
    val allInvoices = homeUiState.listInvoice

    // Skenario 4: Filter Status
    var selectedFilter by remember { mutableStateOf("All") }

    val filteredInvoices = remember(allInvoices, selectedFilter) {
        when (selectedFilter) {
            "All" -> allInvoices
            JenisStatus.INVOICE_LUNAS -> allInvoices.filter { it.status == JenisStatus.INVOICE_LUNAS }
            JenisStatus.INVOICE_BELUM_BAYAR -> allInvoices.filter { it.status == JenisStatus.INVOICE_BELUM_BAYAR }
            else -> allInvoices
        }
    }

    Scaffold(
        bottomBar = {
            NavigationBar(containerColor = Color.White, tonalElevation = 8.dp) {
                NavigationBarItem(selected = false, onClick = onHome, icon = { Icon(Icons.Default.Home, null) }, label = { Text(stringResource(R.string.nav_home)) })
                NavigationBarItem(selected = false, onClick = onKlien, icon = { Icon(Icons.Default.People, null) }, label = { Text(stringResource(R.string.nav_clients)) })
                NavigationBarItem(selected = false, onClick = onProject, icon = { Icon(Icons.Default.Work, null) }, label = { Text(stringResource(R.string.nav_projects)) })
                NavigationBarItem(selected = true, onClick = {}, icon = { Icon(Icons.Default.Receipt, null) }, label = { Text(stringResource(R.string.nav_invoices)) })
                NavigationBarItem(selected = false, onClick = onProfile, icon = { Icon(Icons.Default.Person, null) }, label = { Text(stringResource(R.string.nav_profile)) })
            }
        },
        floatingActionButton = {
            FloatingActionButton(
                onClick = onAdd,
                containerColor = Color(0xFF1A237E), contentColor = Color.White
            ) {
                Icon(Icons.Default.Add, contentDescription = stringResource(R.string.action_create_invoice))
            }
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .background(Color(0xFFF5F5F5))
                .padding(padding)
        ) {
            // Header
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Brush.verticalGradient(listOf(Color(0xFF1A237E), Color(0xFF283593), Color(0xFF3949AB))))
                    .padding(dimensionResource(R.dimen.spacing_large))
            ) {
                Column {
                    Text(
                        text = stringResource(R.string.nav_invoices),
                        style = MaterialTheme.typography.headlineMedium,
                        fontWeight = FontWeight.Bold,
                        color = Color.White
                    )
                    Text(
                        text = "${filteredInvoices.size} invoice",
                        style = MaterialTheme.typography.bodyMedium,
                        color = Color.White.copy(alpha = 0.8f)
                    )
                }
            }

            // Filter Chips
            LazyRow(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 12.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                val filters = listOf("All", JenisStatus.INVOICE_BELUM_BAYAR, JenisStatus.INVOICE_LUNAS)
                items(filters) { filter ->
                    FilterChip(
                        selected = selectedFilter == filter,
                        onClick = { selectedFilter = filter },
                        label = {
                            Text(
                                text = when (filter) {
                                    "All" -> stringResource(R.string.filter_all)
                                    JenisStatus.INVOICE_BELUM_BAYAR -> stringResource(R.string.status_unpaid)
                                    JenisStatus.INVOICE_LUNAS -> stringResource(R.string.status_paid)
                                    else -> filter
                                },
                                fontWeight = if (selectedFilter == filter) FontWeight.Bold else FontWeight.Normal
                            )
                        },
                        leadingIcon = if (selectedFilter == filter) { { Icon(Icons.Default.Check, null, Modifier.size(18.dp)) } } else null,
                        colors = FilterChipDefaults.filterChipColors(selectedContainerColor = Color(0xFF3949AB), selectedLabelColor = Color.White, selectedLeadingIconColor = Color.White)
                    )
                }
            }

            // List Invoice
            if (filteredInvoices.isEmpty()) {
                Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.spacedBy(16.dp)) {
                        Icon(Icons.Default.ReceiptLong, null, Modifier.size(80.dp), tint = Color(0xFFBDBDBD))
                        Text(
                            text = stringResource(R.string.empty_invoice_list),
                            style = MaterialTheme.typography.titleMedium, color = Color(0xFF757575)
                        )
                    }
                }
            } else {
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = 16.dp, vertical = 8.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    items(filteredInvoices) { invoice ->
                        InvoiceCard(invoice = invoice, onClick = { onDetail(invoice.idInvoice) })
                    }
                }
            }
        }
    }
}

@Composable
private fun InvoiceCard(invoice: InvoiceEntity, onClick: () -> Unit) {
    val context = LocalContext.current
    val statusColor = if (invoice.status == JenisStatus.INVOICE_LUNAS) Color(0xFF4CAF50) else Color(0xFFD32F2F)

    Card(
        modifier = Modifier.fillMaxWidth().clickable(onClick = onClick),
        shape = RoundedCornerShape(dimensionResource(R.dimen.card_radius)),
        colors = CardDefaults.cardColors(containerColor = Color.White),
        elevation = CardDefaults.cardElevation(4.dp)
    ) {
        Column(modifier = Modifier.fillMaxWidth().padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)) {
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.Top) {
                Column(modifier = Modifier.weight(1f)) {
                    Text(text = invoice.invoiceNumber, style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF1A237E))
                    Text(
                        text = "${stringResource(R.string.label_due_date)}: ${FormatterUtils.formatTanggal(invoice.dueDate)}",
                        style = MaterialTheme.typography.bodySmall, color = Color.Gray
                    )
                }
                Surface(shape = RoundedCornerShape(8.dp), color = statusColor.copy(alpha = 0.1f)) {
                    Text(
                        text = if (invoice.status == JenisStatus.INVOICE_LUNAS) stringResource(R.string.status_paid) else stringResource(R.string.status_unpaid),
                        modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                        style = MaterialTheme.typography.labelSmall, color = statusColor, fontWeight = FontWeight.Bold
                    )
                }
            }
            Divider()
            Row(modifier = Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                Column {
                    Text(text = stringResource(R.string.label_total), style = MaterialTheme.typography.labelSmall, color = Color.Gray)

                    Text(text = FormatterUtils.formatRupiah(context, invoice.total), style = MaterialTheme.typography.titleMedium, fontWeight = FontWeight.Bold, color = Color(0xFF4CAF50))
                }
                Icon(Icons.Default.ChevronRight, null, tint = Color(0xFFBDBDBD))
            }
        }
    }
}
