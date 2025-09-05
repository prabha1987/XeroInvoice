package com.xero.invoice.ui.invoice_list

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.material3.pulltorefresh.PullToRefreshBox
import com.xero.invoice.R
import com.xero.invoice.domain.model.InvoiceListItem
import com.xero.invoice.domain.common.InvoiceError
import com.xero.invoice.ui.common.errorMessage
import com.xero.invoice.ui.common.errorTitle
import java.time.LocalDate
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.automirrored.filled.List
import java.time.format.DateTimeFormatter

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoiceListScreen(
    onInvoiceClick: (String) -> Unit,
    viewModel: InvoiceListViewModel
) {
    val uiState by viewModel.uiState.collectAsState()
    val listState = rememberLazyListState()
    
    Scaffold(
        topBar = {
            InvoicesListTopBar(
                onSelect = { option -> viewModel.selectEndpoint(option) }
            )
        }
    ) { paddingValues ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
        ) {
            when {
                uiState.showMainLoading -> LoadingState()
                uiState.showErrorState -> ErrorState(
                    error = uiState.error,
                    onRetry = { viewModel.retry() }
                )
                uiState.showEmptyState -> EmptyState()
                uiState.showContent || uiState.showContentWithRefresh -> {
                    PullToRefreshBox(
                        isRefreshing = uiState.isRefreshing,
                        onRefresh = { viewModel.loadInvoices() },
                        modifier = Modifier.fillMaxSize()
                    ) {
                        InvoiceListContent(
                            invoices = uiState.invoices,
                            onInvoiceClick = onInvoiceClick,
                            listState = listState
                        )
                    }
                }
            }
        }
    }
}

@Composable
private fun LoadingState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorState(
    error: InvoiceError?,
    onRetry: () -> Unit
) {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Text(
                text = "⚠️",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.error
            )
            
            Text(
                text = errorTitle(error),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = errorMessage(error),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Button(onClick = onRetry) {
                Text(stringResource(R.string.retry))
            }
        }
    }
}

@Composable
private fun EmptyState() {
    Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            Icon(
                imageVector = Icons.AutoMirrored.Filled.List,
                contentDescription = null,
                modifier = Modifier.size(48.dp),
                tint = MaterialTheme.colorScheme.onSurfaceVariant
            )
            
            Text(
                text = stringResource(R.string.no_invoices_found),
                style = MaterialTheme.typography.headlineSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            Text(
                text = stringResource(R.string.no_invoices_message),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}

@Composable
private fun InvoiceListContent(
    invoices: List<InvoiceListItem>,
    onInvoiceClick: (String) -> Unit,
    listState: androidx.compose.foundation.lazy.LazyListState
) {
    LazyColumn(
        modifier = Modifier.fillMaxSize(),
        state = listState,
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        items(
            items = invoices.groupBy { it.date }.toList(),
            key = { (date, _) -> date.toString() }
        ) { (date, dateInvoices) ->
            InvoiceDateCard(
                date = date,
                invoices = dateInvoices,
                onInvoiceClick = onInvoiceClick
            )
        }
    }
}

@Composable
private fun InvoiceDateCard(
    date: LocalDate,
    invoices: List<InvoiceListItem>,
    onInvoiceClick: (String) -> Unit
) {
    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = date.format(DateTimeFormatter.ofPattern("EEEE, MMMM d, yyyy")),
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.primary
            )
            
            Spacer(modifier = Modifier.height(8.dp))
            
            invoices.forEachIndexed { index, invoice ->
                InvoiceRow(
                    invoice = invoice,
                    onClick = { onInvoiceClick(invoice.id) }
                )
                
                if (invoice != invoices.last()) {
                    HorizontalDivider(modifier = Modifier.padding(vertical = 8.dp))
                }
            }
        }
    }
}

@Composable
private fun InvoiceRow(
    invoice: InvoiceListItem,
    onClick: () -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .clickable { onClick() }
            .padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Column(
            modifier = Modifier.weight(1f)
        ) {
            Text(
                text = stringResource(id = R.string.invoice_number_title, invoice.index),
                style = MaterialTheme.typography.titleSmall,
                color = MaterialTheme.colorScheme.onSurface
            )
            
            invoice.description?.let { description ->
                Text(
                    text = description,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }
        }

        Text(
            text = "$${
                String.format(
                    java.util.Locale.US,
                    "%.2f",
                    invoice.totalAmount
                )
            }",
            style = MaterialTheme.typography.titleSmall,
            color = MaterialTheme.colorScheme.primary
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun InvoicesListTopBar(
    onSelect: (InvoiceListMenuOption) -> Unit
) {
    TopAppBar(
        title = { Text(stringResource(R.string.xero_invoices_title)) },
        actions = {
            var expanded by remember { mutableStateOf(false) }
            IconButton(onClick = { expanded = true }) {
                Icon(Icons.Filled.MoreVert, contentDescription = stringResource(R.string.menu))
            }
            DropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }) {
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.normal_invoices)) },
                    onClick = {
                        expanded = false
                        onSelect(InvoiceListMenuOption.Normal)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.malformed_invoices)) },
                    onClick = {
                        expanded = false
                        onSelect(InvoiceListMenuOption.Malformed)
                    }
                )
                DropdownMenuItem(
                    text = { Text(stringResource(R.string.empty_invoices)) },
                    onClick = {
                        expanded = false
                        onSelect(InvoiceListMenuOption.Empty)
                    }
                )
            }
        }
    )
}
