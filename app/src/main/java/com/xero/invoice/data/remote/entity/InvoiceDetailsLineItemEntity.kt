package com.xero.invoice.data.remote.entity

import com.google.gson.annotations.SerializedName

/**
 * Entity for a single line item within an invoice.
 */
data class InvoiceDetailsLineItemEntity(
    @SerializedName("id")
    val id: String,

    @SerializedName("name")
    val name: String,

    @SerializedName("quantity")
    val quantity: Int,

    @SerializedName("priceinCents")
    val priceInCents: Int
)
