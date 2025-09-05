package com.xero.invoice.data.remote.entity

import com.google.gson.annotations.SerializedName

data class InvoiceEntity(
    @SerializedName("id")
    val id: String,

    @SerializedName("date")
    val date: String,

    @SerializedName("description")
    val description: String?,

    @SerializedName("items")
    val items: List<InvoiceDetailsLineItemEntity>
)