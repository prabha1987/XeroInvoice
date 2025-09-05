package com.xero.invoice.ui.invoice_list

import com.xero.invoice.domain.common.InvoiceError
import com.xero.invoice.domain.common.InvoiceResult
import com.xero.invoice.domain.model.InvoiceListItem
import com.xero.invoice.domain.usecase.GetInvoicesUseCase
import com.xero.invoice.data.remote.ApiEndpoints
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

@OptIn(ExperimentalCoroutinesApi::class)
class InvoiceListViewModelTest {
    
    private lateinit var viewModel: InvoiceListViewModel
    private lateinit var mockGetInvoicesUseCase: GetInvoicesUseCase
    private val testDispatcher = StandardTestDispatcher()
    
    @Before
    fun setUp() {
        Dispatchers.setMain(testDispatcher)
        mockGetInvoicesUseCase = mockk()
        // Mock the initial call that happens in init
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Success(emptyList())
        viewModel = InvoiceListViewModel(mockGetInvoicesUseCase)
    }
    
    @Test
    fun `initial state shows empty after setup completes`() = runTest {
        // Given - ViewModel is already created in setUp with empty list mock
        advanceUntilIdle()
        
        // Then - Should show empty state after init completes
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isRefreshing)
        assertTrue(uiState.showEmptyState)
        assertTrue(uiState.isEmpty)
    }
    
    @Test
    fun `loadInvoices sets success state when use case succeeds`() = runTest {
        // Given
        val mockInvoices = listOf(
            InvoiceListItem(
                id = "1",
                index = 1001,
                date = LocalDate.of(2024, 1, 1),
                description = "Test Invoice 1",
                totalAmount = 100.0
            ),
            InvoiceListItem(
                id = "2",
                index = 1002,
                date = LocalDate.of(2024, 1, 2),
                description = "Test Invoice 2",
                totalAmount = 200.0
            )
        )
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Success(mockInvoices)
        
        // When
        viewModel.loadInvoices()
        advanceUntilIdle()
        
        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertTrue(uiState.showContent)
        assertFalse(uiState.showErrorState)
        assertFalse(uiState.showEmptyState)
        assertFalse(uiState.isEmpty)
        assertEquals(mockInvoices, uiState.invoices)
        assertEquals(null, uiState.error)
    }
    
    @Test
    fun `loadInvoices sets empty state when use case returns empty list`() = runTest {
        // Given
        val emptyList = emptyList<InvoiceListItem>()
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Success(emptyList)
        
        // When
        viewModel.loadInvoices()
        advanceUntilIdle()
        
        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isRefreshing)
        assertFalse(uiState.showContent)
        assertFalse(uiState.showErrorState)
        assertTrue(uiState.showEmptyState)
        assertTrue(uiState.isEmpty)
        assertEquals(emptyList, uiState.invoices)
        assertEquals(null, uiState.error)
    }
    
    @Test
    fun `loadInvoices sets error state when use case fails with network error`() = runTest {
        // Given
        val error = InvoiceError.Network
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Failure(error)
        
        // When
        viewModel.loadInvoices()
        advanceUntilIdle()
        
        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isRefreshing)
        assertFalse(uiState.showContent)
        assertTrue(uiState.showErrorState)
        assertFalse(uiState.showEmptyState)
        assertEquals(error, uiState.error)
    }
    
    @Test
    fun `loadInvoices sets error state when use case fails with malformed data error`() = runTest {
        // Given
        val error = InvoiceError.MalformedData
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Failure(error)
        
        // When
        viewModel.loadInvoices()
        advanceUntilIdle()
        
        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isRefreshing)
        assertFalse(uiState.showContent)
        assertTrue(uiState.showErrorState)
        assertFalse(uiState.showEmptyState)
        assertEquals(error, uiState.error)
    }
    
    @Test
    fun `loadInvoices sets error state when use case fails with unknown error`() = runTest {
        // Given
        val error = InvoiceError.Unknown("Unexpected error")
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Failure(error)
        
        // When
        viewModel.loadInvoices()
        advanceUntilIdle()
        
        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.showContent)
        assertTrue(uiState.showErrorState)
        assertFalse(uiState.showEmptyState)
        assertTrue(uiState.error is InvoiceError.Unknown)
    }
    
    @Test
    fun `retry calls loadInvoices again`() = runTest {
        // Given
        val error = InvoiceError.Network
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Failure(error)
        
        // When - First load fails
        viewModel.loadInvoices()
        advanceUntilIdle()
        
        // Verify error state
        assertTrue(viewModel.uiState.value.showErrorState)
        
        // Given - Second call succeeds
        val mockInvoices = listOf(
            InvoiceListItem(
                id = "1",
                index = 1001,
                date = LocalDate.of(2024, 1, 1),
                description = "Test Invoice",
                totalAmount = 100.0
            )
        )
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Success(mockInvoices)
        
        // When - Retry
        viewModel.retry()
        advanceUntilIdle()
        
        // Then
        val uiState = viewModel.uiState.value
        assertFalse(uiState.isLoading)
        assertFalse(uiState.isRefreshing)
        assertTrue(uiState.showContent)
        assertFalse(uiState.showErrorState)
        assertEquals(mockInvoices, uiState.invoices)
    }
    
    
    @Test
    fun `selectEndpoint clears data immediately and shows main loading`() = runTest {
        // Given - Start with some existing data
        val initialInvoices = listOf(
            InvoiceListItem(
                id = "1",
                index = 1001,
                date = LocalDate.of(2024, 1, 1),
                description = "Initial Invoice",
                totalAmount = 100.0
            )
        )
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Success(initialInvoices)
        viewModel.loadInvoices()
        advanceUntilIdle()
        
        // Verify initial state has data
        assertTrue(viewModel.uiState.value.showContent)
        assertEquals(initialInvoices, viewModel.uiState.value.invoices)
        
        // When - Switch endpoint
        val newInvoices = listOf(
            InvoiceListItem(
                id = "2",
                index = 1002,
                date = LocalDate.of(2024, 1, 2),
                description = "Malformed Invoice",
                totalAmount = 200.0
            )
        )
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Success(newInvoices)
        
        viewModel.selectEndpoint(InvoiceListMenuOption.Malformed)
        
        // Allow the coroutine to start
        testScheduler.advanceTimeBy(1)
        
        // Then - Data should be cleared immediately, main loading should show
        val immediateState = viewModel.uiState.value
        assertTrue(immediateState.invoices.isEmpty()) // Data cleared immediately
        assertTrue(immediateState.showMainLoading) // Main loading shows
        assertFalse(immediateState.isRefreshing) // Not a refresh
        assertFalse(immediateState.showContent) // No content during endpoint change
        
        // After async completes
        advanceUntilIdle()
        
        val finalState = viewModel.uiState.value
        assertEquals(ApiEndpoints.MALFORMED_INVOICES, ApiEndpoints.currentInvoiceUrl)
        assertFalse(finalState.isLoading)
        assertFalse(finalState.isRefreshing)
        assertTrue(finalState.showContent)
        assertEquals(newInvoices, finalState.invoices)
    }
    
    @Test
    fun `loadInvoices with existing data sets refresh state correctly`() = runTest {
        // Given - Start with existing data
        val initialInvoices = listOf(
            InvoiceListItem(
                id = "1",
                index = 1001,
                date = LocalDate.of(2024, 1, 1),
                description = "Initial Invoice",
                totalAmount = 100.0
            )
        )
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Success(initialInvoices)
        viewModel.loadInvoices()
        advanceUntilIdle()
        
        // Verify initial state
        assertTrue(viewModel.uiState.value.showContent)
        
        // When - Load invoices again (simulating pull-to-refresh)
        val refreshedInvoices = listOf(
            InvoiceListItem(
                id = "2",
                index = 1002,
                date = LocalDate.of(2024, 1, 2),
                description = "Refreshed Invoice",
                totalAmount = 200.0
            )
        )
        coEvery { mockGetInvoicesUseCase() } returns InvoiceResult.Success(refreshedInvoices)
        
        viewModel.loadInvoices()
        
        // Allow the coroutine to start
        testScheduler.advanceTimeBy(1)
        
        // Then - Should be in refresh state (not main loading)
        val duringRefreshState = viewModel.uiState.value
        assertTrue(duringRefreshState.isLoading)
        assertTrue(duringRefreshState.isRefreshing) // This should be true for refresh
        assertFalse(duringRefreshState.showMainLoading) // No main loading during refresh
        assertTrue(duringRefreshState.showContentWithRefresh) // Content visible during refresh
        
        // After refresh completes
        advanceUntilIdle()
        
        val afterRefreshState = viewModel.uiState.value
        assertFalse(afterRefreshState.isLoading)
        assertFalse(afterRefreshState.isRefreshing)
        assertTrue(afterRefreshState.showContent)
        assertEquals(refreshedInvoices, afterRefreshState.invoices)
    }
}
