package com.xero.invoice.data.mapper

import com.xero.invoice.data.remote.entity.InvoiceEntity
import com.xero.invoice.data.remote.entity.InvoiceDetailsLineItemEntity
import com.xero.invoice.domain.model.Invoice
import com.xero.invoice.domain.model.InvoiceDetailsLineItem
import com.xero.invoice.domain.model.InvoiceListItem
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter

/**
 * Mapper for converting between entity and domain models.
 */
object InvoiceMapper {
    
    private val dateTimeFormatter = DateTimeFormatter.ISO_LOCAL_DATE_TIME
    
    /**
     * Maps InvoiceEntity to Invoice domain model.
     */
    fun mapToDomain(entity: InvoiceEntity, index: Int = 0): Invoice {
        return Invoice(
            id = entity.id,
            date = LocalDateTime.parse(entity.date, dateTimeFormatter).toLocalDate(),
            description = entity.description,
            items = entity.items.map { mapLineItemToDomain(it) },
            index = index
        )
    }
    
    /**
     * Maps Invoice domain model to InvoiceListItem.
     */
    fun mapToListItem(invoice: Invoice): InvoiceListItem {
        return InvoiceListItem(
            id = invoice.id,
            date = invoice.date,
            description = invoice.description,
            totalAmount = invoice.totalAmount,
            index = invoice.index
        )
    }
    
    /**
     * Maps InvoiceLineItemEntity to InvoiceLineItem domain model.
     */
    private fun mapLineItemToDomain(entity: InvoiceDetailsLineItemEntity): InvoiceDetailsLineItem {
        return InvoiceDetailsLineItem(
            id = entity.id,
            name = entity.name,
            quantity = entity.quantity,
            priceInCents = entity.priceInCents
        )
    }
}
