package com.xero.invoice.data.repository

import com.xero.invoice.data.mapper.InvoiceMapper
import com.xero.invoice.data.remote.ApiEndpoints
import com.xero.invoice.data.remote.entity.InvoiceEntity
import com.xero.invoice.data.remote.service.InvoiceApiService
import com.xero.invoice.domain.model.Invoice
import com.xero.invoice.domain.model.InvoiceListItem
import com.xero.invoice.domain.repository.InvoiceRepository
import com.xero.invoice.domain.common.InvoiceResult
import com.xero.invoice.domain.common.toInvoiceError
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Implementation of InvoiceRepository with in-memory caching.
 */
class InvoiceRepositoryImpl(
    private val apiService: InvoiceApiService
) : InvoiceRepository {

    // Simple in-memory cache - could be replaced with Room for persistence
    private var cachedInvoices: List<Invoice>? = null

    override suspend fun getInvoices(): InvoiceResult<List<InvoiceListItem>> {
        return try {
            val invoices = fetchAndCacheInvoices()
            val listItems = invoices.map { invoice -> InvoiceMapper.mapToListItem(invoice) }
            InvoiceResult.Success(listItems)
        } catch (e: Exception) {
            InvoiceResult.Failure(e.toInvoiceError())
        }
    }

    override suspend fun getInvoiceById(id: String): InvoiceResult<Invoice> {
        return try {
            cachedInvoices?.find { it.id == id }?.let {
                return InvoiceResult.Success(it)
            }
            val invoices = fetchAndCacheInvoices()
            val invoice = invoices.find { it.id == id }
            if (invoice != null) {
                InvoiceResult.Success(invoice)
            } else {
                InvoiceResult.Failure(
                    IllegalArgumentException("Invoice with id $id not found").toInvoiceError()
                )
            }
        } catch (e: Exception) {
            InvoiceResult.Failure(e.toInvoiceError())
        }
    }

    /**
     * Group invoices by Date
     */
    private fun groupInvoicesByDate(
        entities: List<InvoiceEntity>
    ): Map<LocalDate, List<InvoiceEntity>> {
        return entities.groupBy { entity ->
            LocalDateTime.parse(
                entity.date,
                DateTimeFormatter.ISO_LOCAL_DATE_TIME
            ).toLocalDate()
        }
    }

    /**
     * Helper function to fetch invoices from network, group by date, and cache them
     */
    private suspend fun fetchAndCacheInvoices(): List<Invoice> {
        val response = apiService.getInvoices(ApiEndpoints.currentInvoiceUrl)
        val groupedByDate = groupInvoicesByDate(response.items)
        val invoices = groupedByDate.flatMap { (_, dateInvoices) ->
            dateInvoices.mapIndexed { indexWithinDate, entity ->
                InvoiceMapper.mapToDomain(entity, indexWithinDate + 1)
            }
        }
        cachedInvoices = invoices
        return invoices
    }
}
