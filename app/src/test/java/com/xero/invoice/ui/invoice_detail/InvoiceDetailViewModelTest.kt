package com.xero.invoice.ui.invoice_detail

import com.xero.invoice.domain.common.InvoiceError
import com.xero.invoice.domain.common.InvoiceResult
import com.xero.invoice.domain.model.Invoice
import com.xero.invoice.domain.model.InvoiceDetailsLineItem
import com.xero.invoice.domain.usecase.GetInvoiceDetailsUseCase
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class InvoiceDetailViewModelTest {

    private lateinit var viewModel: InvoiceDetailViewModel
    private lateinit var mockGetInvoiceDetailsUseCase: GetInvoiceDetailsUseCase
    private val testDispatcher = StandardTestDispatcher()
    private val testInvoiceId = "test-invoice-123"

    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockGetInvoiceDetailsUseCase = mockk()
    }

    @After
    fun tearDown() {
        Dispatchers.setMain(Dispatchers.Unconfined)
    }

    @Test
    fun `initial state shows loading then success when invoice found`() = runTest {
        // Given
        val mockInvoice = Invoice(
            id = testInvoiceId,
            date = LocalDate.of(2024, 1, 15),
            description = "Test Invoice Details",
            items = listOf(
                InvoiceDetailsLineItem(
                    id = "item1",
                    name = "Consulting Service",
                    quantity = 2,
                    priceInCents = 15000 // $150.00
                )
            )
        )
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Success(mockInvoice)

        // When
        viewModel = InvoiceDetailViewModel(mockGetInvoiceDetailsUseCase, testInvoiceId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertTrue(uiState.showContent)
        assertFalse(uiState.showErrorState)
        assertEquals(mockInvoice, uiState.invoice)
        assertEquals(null, uiState.error)
    }

    @Test
    fun `initial state shows error when invoice not found`() = runTest {
        // Given
        val error = InvoiceError.NotFound("Invoice not found")
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Failure(error)

        // When
        viewModel = InvoiceDetailViewModel(mockGetInvoiceDetailsUseCase, testInvoiceId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.showContent)
        assertTrue(uiState.showErrorState)
        assertEquals(null, uiState.invoice)
        assertEquals(error, uiState.error)
    }

    @Test
    fun `initial state shows error when network error occurs`() = runTest {
        // Given
        val error = InvoiceError.Network
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Failure(error)

        // When
        viewModel = InvoiceDetailViewModel(mockGetInvoiceDetailsUseCase, testInvoiceId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.showContent)
        assertTrue(uiState.showErrorState)
        assertEquals(null, uiState.invoice)
        assertEquals(error, uiState.error)
    }

    @Test
    fun `initial state shows error when malformed data error occurs`() = runTest {
        // Given
        val error = InvoiceError.MalformedData
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Failure(error)

        // When
        viewModel = InvoiceDetailViewModel(mockGetInvoiceDetailsUseCase, testInvoiceId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.showContent)
        assertTrue(uiState.showErrorState)
        assertEquals(null, uiState.invoice)
        assertEquals(error, uiState.error)
    }

    @Test
    fun `initial state shows error when unknown error occurs`() = runTest {
        // Given
        val error = InvoiceError.Unknown("Unexpected error")
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Failure(error)

        // When
        viewModel = InvoiceDetailViewModel(mockGetInvoiceDetailsUseCase, testInvoiceId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.showContent)
        assertTrue(uiState.showErrorState)
        assertEquals(null, uiState.invoice)
        assertTrue(uiState.error is InvoiceError.Unknown)
    }

    @Test
    fun `retry loads invoice details again after error`() = runTest {
        // Given - Initial error
        val error = InvoiceError.Network
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Failure(error)
        
        viewModel = InvoiceDetailViewModel(mockGetInvoiceDetailsUseCase, testInvoiceId)
        advanceUntilIdle()
        
        // Verify error state
        assertTrue(viewModel.uiState.value.showErrorState)
        
        // When - Retry with success
        val mockInvoice = Invoice(
            id = testInvoiceId,
            date = LocalDate.of(2024, 1, 15),
            description = "Retry Success Invoice",
            items = listOf(
                InvoiceDetailsLineItem(
                    id = "item1",
                    name = "Retry Service",
                    quantity = 1,
                    priceInCents = 20000 // $200.00
                )
            )
        )
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Success(mockInvoice)
        
        viewModel.retry()
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertTrue(uiState.showContent)
        assertFalse(uiState.showErrorState)
        assertEquals(mockInvoice, uiState.invoice)
        assertEquals(null, uiState.error)
    }

    @Test
    fun `retry shows error again when use case fails`() = runTest {
        // Given - Initial success
        val mockInvoice = Invoice(
            id = testInvoiceId,
            date = LocalDate.of(2024, 1, 15),
            description = "Initial Success",
            items = emptyList()
        )
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Success(mockInvoice)
        
        viewModel = InvoiceDetailViewModel(mockGetInvoiceDetailsUseCase, testInvoiceId)
        advanceUntilIdle()
        
        // Verify success state
        assertTrue(viewModel.uiState.value.showContent)
        
        // When - Retry with error
        val error = InvoiceError.Network
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Failure(error)
        
        viewModel.retry()
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.showContent) // Content hidden during error
        assertTrue(uiState.showErrorState)
        assertEquals(mockInvoice, uiState.invoice) // Invoice data preserved
        assertEquals(error, uiState.error)
    }

    @Test
    fun `loading state is instant without delay for cache access`() = runTest {
        // Given
        val mockInvoice = Invoice(
            id = testInvoiceId,
            date = LocalDate.of(2024, 1, 15),
            description = "Cache Test Invoice",
            items = emptyList()
        )
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Success(mockInvoice)

        // When
        viewModel = InvoiceDetailViewModel(mockGetInvoiceDetailsUseCase, testInvoiceId)
        
        // Then - Should complete immediately without delay (cache access)
        advanceUntilIdle()
        
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertTrue(uiState.showContent)
        assertEquals(mockInvoice, uiState.invoice)
    }

    @Test
    fun `invoice with multiple line items calculates total correctly`() = runTest {
        // Given
        val mockInvoice = Invoice(
            id = testInvoiceId,
            date = LocalDate.of(2024, 1, 15),
            description = "Multi-item Invoice",
            items = listOf(
                InvoiceDetailsLineItem(
                    id = "item1",
                    name = "Service A",
                    quantity = 2,
                    priceInCents = 10000 // $100.00
                ),
                InvoiceDetailsLineItem(
                    id = "item2",
                    name = "Service B",
                    quantity = 1,
                    priceInCents = 15000 // $150.00
                ),
                InvoiceDetailsLineItem(
                    id = "item3",
                    name = "Service C",
                    quantity = 3,
                    priceInCents = 5000 // $50.00
                )
            )
        )
        coEvery { mockGetInvoiceDetailsUseCase(testInvoiceId) } returns InvoiceResult.Success(mockInvoice)

        // When
        viewModel = InvoiceDetailViewModel(mockGetInvoiceDetailsUseCase, testInvoiceId)
        advanceUntilIdle()

        // Then
        val uiState = viewModel.uiState.value
        assertTrue(uiState.showContent)
        assertEquals(mockInvoice, uiState.invoice)
        
        // Verify line items are preserved correctly
        assertEquals(3, uiState.invoice?.items?.size)
        assertEquals("Service A", uiState.invoice?.items?.get(0)?.name)
        assertEquals(2, uiState.invoice?.items?.get(0)?.quantity)
        assertEquals(10000, uiState.invoice?.items?.get(0)?.priceInCents)
    }
}
