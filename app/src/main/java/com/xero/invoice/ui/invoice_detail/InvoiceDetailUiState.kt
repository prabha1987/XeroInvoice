package com.xero.invoice.ui.invoice_detail

import com.xero.invoice.domain.model.Invoice
import com.xero.invoice.domain.common.InvoiceError

/**
 * UI state for the invoice detail screen.
 */
data class InvoiceDetailUiState(
    val isLoading: Boolean = false,
    val invoice: Invoice? = null,
    val error: InvoiceError? = null
) {
    val showContent: Boolean = invoice != null && !isLoading && error == null
    val showErrorState: Boolean = error != null && !isLoading
}
