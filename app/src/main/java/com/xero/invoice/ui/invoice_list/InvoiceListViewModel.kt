package com.xero.invoice.ui.invoice_list

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.xero.invoice.domain.usecase.GetInvoicesUseCase
import com.xero.invoice.domain.common.InvoiceError
import com.xero.invoice.domain.common.InvoiceResult
import com.xero.invoice.domain.model.InvoiceListItem
import com.xero.invoice.data.remote.ApiEndpoints
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import kotlinx.coroutines.delay

/**
 * ViewModel for the invoice list screen.
 */
class InvoiceListViewModel(
    private val getInvoicesUseCase: GetInvoicesUseCase
) : ViewModel() {

    private val _uiState = MutableStateFlow(InvoiceListUiState())
    val uiState: StateFlow<InvoiceListUiState> = _uiState.asStateFlow()

    init {
        loadInvoices()
    }

    fun loadInvoices() {
        viewModelScope.launch {
            setLoadingState(isRefresh = _uiState.value.invoices.isNotEmpty())
            // Add slight delay to show loading state
            delay(500)
            when (val result = getInvoicesUseCase()) {
                is InvoiceResult.Success -> {
                    setSuccessState(result.data)
                }

                is InvoiceResult.Failure -> {
                    setErrorState(result.error)
                }
            }
        }
    }

    private fun setLoadingState(isRefresh: Boolean = false) {
        _uiState.value = _uiState.value.copy(
            isLoading = true,
            isRefreshing = isRefresh,
            error = null
        )
    }

    private fun setSuccessState(invoices: List<InvoiceListItem>) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isRefreshing = false,
            invoices = invoices,
            isEmpty = invoices.isEmpty(),
            error = null
        )
    }

    private fun setErrorState(error: InvoiceError) {
        _uiState.value = _uiState.value.copy(
            isLoading = false,
            isRefreshing = false,
            error = error
        )
    }

    fun retry() {
        loadInvoices()
    }

    fun selectEndpoint(option: InvoiceListMenuOption) {
        // Switch API endpoint for testing different scenarios
        ApiEndpoints.currentInvoiceUrl = when (option) {
            InvoiceListMenuOption.Normal -> ApiEndpoints.NORMAL_INVOICES
            InvoiceListMenuOption.Malformed -> ApiEndpoints.MALFORMED_INVOICES
            InvoiceListMenuOption.Empty -> ApiEndpoints.EMPTY_INVOICES
        }
        // Clear current data and show main loading for endpoint change
        _uiState.value = _uiState.value.copy(
            invoices = emptyList(),
            isEmpty = false,
            error = null
        )
        loadInvoices()
    }

}

enum class InvoiceListMenuOption { Normal, Malformed, Empty }
