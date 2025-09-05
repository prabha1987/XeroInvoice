package com.xero.invoice.data.remote.entity

import com.google.gson.annotations.SerializedName

/**
 * Entity for the root response from the invoices API.
 */
data class InvoicesResponseEntity(
    @SerializedName("items")
    val items: List<InvoiceEntity>
)