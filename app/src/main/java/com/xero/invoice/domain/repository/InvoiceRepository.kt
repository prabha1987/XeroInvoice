package com.xero.invoice.domain.repository

import com.xero.invoice.domain.model.Invoice
import com.xero.invoice.domain.model.InvoiceListItem
import com.xero.invoice.domain.common.InvoiceResult

/**
 * Repository interface for invoice data operations.
 */
interface InvoiceRepository {
    /**
     * Fetches all invoices from network.
     */
    suspend fun getInvoices(): InvoiceResult<List<InvoiceListItem>>
    
    /**
     * Fetches a specific invoice by ID from cache or network.
     */
    suspend fun getInvoiceById(id: String): InvoiceResult<Invoice>
}
