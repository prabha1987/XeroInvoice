package com.xero.invoice.domain.usecase

import com.xero.invoice.domain.model.InvoiceListItem
import com.xero.invoice.domain.repository.InvoiceRepository
import com.xero.invoice.domain.common.InvoiceResult
// import javax.inject.Inject

/**
 * Use case for fetching the list of invoices.
 */
class GetInvoicesUseCase(
    private val repository: InvoiceRepository
) {
    suspend operator fun invoke(): InvoiceResult<List<InvoiceListItem>> {
        return repository.getInvoices()
    }
}
