package com.xero.invoice.ui.invoice_list

import com.xero.invoice.domain.model.InvoiceListItem
import com.xero.invoice.domain.common.InvoiceError

/**
 * UI state for the invoice list screen.
 */
data class InvoiceListUiState(
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val invoices: List<InvoiceListItem> = emptyList(),
    val error: InvoiceError? = null,
    val isEmpty: Boolean = false
) {
    val hasData: Boolean = invoices.isNotEmpty()
    val showEmptyState: Boolean = !isLoading && !isRefreshing && isEmpty && error == null
    val showErrorState: Boolean = error != null && !isLoading && !isRefreshing
    val showContent: Boolean = hasData && error == null && !isLoading
    val showContentWithRefresh: Boolean = hasData && error == null && isRefreshing
    val showMainLoading: Boolean = isLoading && !isRefreshing
}
