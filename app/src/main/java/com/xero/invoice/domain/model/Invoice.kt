package com.xero.invoice.domain.model

import java.time.LocalDate

/**
 * Domain model for a single invoice.
 */
data class Invoice(
    val id: String,
    val date: LocalDate,
    val description: String?,
    val items: List<InvoiceDetailsLineItem>,
    val index: Int = 0
) {
    val totalAmount: Double
        get() = items.sumOf { it.totalPrice }
}

