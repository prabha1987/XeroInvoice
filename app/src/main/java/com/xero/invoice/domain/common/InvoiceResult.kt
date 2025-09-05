package com.xero.invoice.domain.common

sealed class InvoiceResult<out T> {
    data class Success<T>(val data: T) : InvoiceResult<T>()
    data class Failure(val error: InvoiceError) : InvoiceResult<Nothing>()
}


