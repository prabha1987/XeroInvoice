package com.xero.invoice.ui.invoice_detail

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xero.invoice.domain.usecase.GetInvoiceDetailsUseCase
import com.xero.invoice.domain.common.InvoiceError
import com.xero.invoice.domain.common.InvoiceResult
import com.xero.invoice.domain.model.Invoice
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

/**
 * ViewModel for the invoice detail screen.
 */
class InvoiceDetailViewModel(
    private val getInvoiceDetailsUseCase: GetInvoiceDetailsUseCase,
    private val invoiceId: String
) : ViewModel() {
    
    private val _uiState = MutableStateFlow(InvoiceDetailUiState())
    val uiState: StateFlow<InvoiceDetailUiState> = _uiState.asStateFlow()

    
    init {
        loadInvoiceDetails()
    }
    
    private fun loadInvoiceDetails() {
        viewModelScope.launch {
            setLoadingState()
            when (val result = getInvoiceDetailsUseCase(invoiceId)) {
                is InvoiceResult.Success -> {
                    setSuccessState(result.data)
                }
                is InvoiceResult.Failure -> {
                   setErrorState(result.error)
                }
            }
        }
    }

    private fun setLoadingState() {
        _uiState.value = _uiState.value.copy(isLoading = true, error = null)
    }

    private fun setSuccessState(invoice: Invoice?) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            invoice = invoice,
            error = null
        )
    }

    private fun setErrorState(error: InvoiceError) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            error = error
        )
    }

    fun retry() {
        loadInvoiceDetails()
    }
}
