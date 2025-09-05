package com.xero.invoice.data.remote

/**
 * Defines the network endpoints for fetching invoice data.
 */
object ApiEndpoints {
    const val NORMAL_INVOICES = "https://storage.googleapis.com/xmm-homework/invoices.json"
    const val MALFORMED_INVOICES = "https://storage.googleapis.com/xmm-homework/invoices_malformed.json"
    const val EMPTY_INVOICES = "https://storage.googleapis.com/xmm-homework/invoices_empty.json"
    @Volatile var currentInvoiceUrl: String = NORMAL_INVOICES
}