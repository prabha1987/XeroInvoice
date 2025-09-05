package com.xero.invoice.domain.usecase

import com.xero.invoice.domain.model.Invoice
import com.xero.invoice.domain.repository.InvoiceRepository
import com.xero.invoice.domain.common.InvoiceResult

/**
 * Use case for fetching invoice details by ID.
 */
class GetInvoiceDetailsUseCase(
    private val repository: InvoiceRepository
) {
    
    /**
     * Executes the use case to get invoice details by ID.
     */
    suspend operator fun invoke(id: String): InvoiceResult<Invoice> {
        return repository.getInvoiceById(id)
    }
}
