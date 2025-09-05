package com.xero.invoice.domain.common


import com.google.gson.stream.MalformedJsonException
import retrofit2.HttpException
import java.net.UnknownHostException

fun Throwable.toInvoiceError(): InvoiceError = when (this) {
    is UnknownHostException -> InvoiceError.Network
    is HttpException -> InvoiceError.Network
    is MalformedJsonException -> InvoiceError.MalformedData
    is IllegalArgumentException -> InvoiceError.NotFound(message)
    else -> InvoiceError.Unknown(message)
}


