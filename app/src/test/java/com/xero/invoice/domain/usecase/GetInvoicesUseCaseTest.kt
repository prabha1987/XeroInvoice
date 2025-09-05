package com.xero.invoice.domain.usecase

import com.xero.invoice.domain.common.InvoiceError
import com.xero.invoice.domain.common.InvoiceResult
import com.xero.invoice.domain.model.InvoiceListItem
import com.xero.invoice.domain.repository.InvoiceRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetInvoicesUseCaseTest {
    
    private lateinit var useCase: GetInvoicesUseCase
    private lateinit var mockRepository: InvoiceRepository
    
    @Before
    fun setUp() {
        mockRepository = mockk()
        useCase = GetInvoicesUseCase(mockRepository)
    }
    
    @Test
    fun `invoke returns success with invoices when repository succeeds`() = runTest {
        // Given
        val mockInvoices = listOf(
            InvoiceListItem(
                id = "1",
                index = 1001,
                date = LocalDate.of(2024, 1, 1),
                description = "Test Invoice",
                totalAmount = 100.0
            )
        )
        coEvery { mockRepository.getInvoices() } returns InvoiceResult.Success(mockInvoices)
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result is InvoiceResult.Success)
        assertEquals(mockInvoices, (result as InvoiceResult.Success).data)
    }
    
    @Test
    fun `invoke returns network error when repository fails with network error`() = runTest {
        // Given
        val error = InvoiceError.Network
        coEvery { mockRepository.getInvoices() } returns InvoiceResult.Failure(error)
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertEquals(error, (result as InvoiceResult.Failure).error)
    }
    
    @Test
    fun `invoke returns malformed data error when repository fails with parsing error`() = runTest {
        // Given
        val error = InvoiceError.MalformedData
        coEvery { mockRepository.getInvoices() } returns InvoiceResult.Failure(error)
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertEquals(error, (result as InvoiceResult.Failure).error)
    }
    
    @Test
    fun `invoke returns unknown error when repository fails with unknown error`() = runTest {
        // Given
        val error = InvoiceError.Unknown("Unexpected error")
        coEvery { mockRepository.getInvoices() } returns InvoiceResult.Failure(error)
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertTrue((result as InvoiceResult.Failure).error is InvoiceError.Unknown)
    }
    
    @Test
    fun `invoke returns empty list when repository returns empty`() = runTest {
        // Given
        val emptyList = emptyList<InvoiceListItem>()
        coEvery { mockRepository.getInvoices() } returns InvoiceResult.Success(emptyList)
        
        // When
        val result = useCase()
        
        // Then
        assertTrue(result is InvoiceResult.Success)
        assertEquals(emptyList, (result as InvoiceResult.Success).data)
    }
}
