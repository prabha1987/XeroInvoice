package com.xero.invoice.data.remote.service

import com.xero.invoice.data.remote.entity.InvoicesResponseEntity
import retrofit2.http.GET
import retrofit2.http.Url

interface InvoiceApiService {

    /**
     * Fetches the list of invoices from the specified URL.
     * The URL can be dynamically provided to fetch normal, malformed, or empty responses.
     */
    @GET
    suspend fun getInvoices(@Url url: String): InvoicesResponseEntity
}
