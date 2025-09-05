package com.xero.invoice.domain.common

/**
 * Domain-level error types independent of UI and transport layers.
 */
sealed class InvoiceError {
    object Network : InvoiceError()
    object MalformedData : InvoiceError()
    data class NotFound(val message: String? = null) : InvoiceError()
    data class Unknown(val message: String? = null) : InvoiceError()
}


