package com.xero.invoice.ui.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.res.stringResource
import com.xero.invoice.R
import com.xero.invoice.domain.common.InvoiceError

@Composable
fun errorTitle(error: InvoiceError?): String {
    return when (error) {
        is InvoiceError.Network -> stringResource(R.string.network_error)
        is InvoiceError.MalformedData -> stringResource(R.string.invalid_data)
        is InvoiceError.NotFound -> error.message ?: stringResource(R.string.something_went_wrong)
        is InvoiceError.Unknown -> stringResource(R.string.something_went_wrong)
        null -> stringResource(R.string.something_went_wrong)
    }
}

@Composable
fun errorMessage(error: InvoiceError?): String {
    return when (error) {
        is InvoiceError.Network -> stringResource(R.string.check_internet_connection)
        is InvoiceError.MalformedData -> stringResource(R.string.data_invalid)
        else -> stringResource(R.string.please_try_again)
    }
}


