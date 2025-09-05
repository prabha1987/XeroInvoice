package com.xero.invoice.domain.model

/**
 * Domain model for a single line item within an invoice.
 */
data class InvoiceDetailsLineItem(
    val id: String,
    val name: String,
    val quantity: Int,
    val priceInCents: Int
) {
    val totalPrice: Double
        get() = (quantity * priceInCents) / 100.0
}
