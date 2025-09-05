package com.xero.invoice.domain.model

import java.time.LocalDate

/**
 * Domain model for invoice list item.
 */
data class InvoiceListItem(
    val id: String,
    val date: LocalDate,
    val description: String?,
    val totalAmount: Double,
    val index: Int = 0
)