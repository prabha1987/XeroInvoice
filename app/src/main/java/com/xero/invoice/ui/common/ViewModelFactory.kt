package com.xero.invoice.ui.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.xero.invoice.ui.invoice_detail.InvoiceDetailViewModel
import com.xero.invoice.ui.invoice_list.InvoiceListViewModel
import org.kodein.di.DirectDI
import org.kodein.di.factory
import org.kodein.di.instance

internal class ViewModelFactory(
    private val di: DirectDI,
    private val args: Map<String, Any> = emptyMap()
) : ViewModelProvider.Factory {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when (modelClass) {
            InvoiceListViewModel::class.java -> {
                di.instance<ViewModel>(tag = "InvoiceListViewModel") as T
            }
            InvoiceDetailViewModel::class.java -> {
                val invoiceId = args["invoiceId"] as? String
                    ?: throw IllegalArgumentException("InvoiceDetailViewModel requires 'invoiceId' argument")
                val factory: (String) -> ViewModel = di.factory(tag = "InvoiceDetailViewModel")
                factory(invoiceId) as T
            }
            else -> throw IllegalArgumentException("Unknown ViewModel class: ${modelClass.name}")
        }
    }
    
    companion object {
        fun forInvoiceDetail(di: DirectDI, invoiceId: String): ViewModelFactory {
            return ViewModelFactory(di, mapOf("invoiceId" to invoiceId))
        }

        fun simple(di: DirectDI): ViewModelFactory {
            return ViewModelFactory(di)
        }
    }
}
