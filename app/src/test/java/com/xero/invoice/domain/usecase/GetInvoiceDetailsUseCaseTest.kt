package com.xero.invoice.domain.usecase

import com.xero.invoice.domain.common.InvoiceError
import com.xero.invoice.domain.common.InvoiceResult
import com.xero.invoice.domain.model.Invoice
import com.xero.invoice.domain.model.InvoiceDetailsLineItem
import com.xero.invoice.domain.repository.InvoiceRepository
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.time.LocalDate

class GetInvoiceDetailsUseCaseTest {
    
    private lateinit var useCase: GetInvoiceDetailsUseCase
    private lateinit var mockRepository: InvoiceRepository
    
    @Before
    fun setUp() {
        mockRepository = mockk()
        useCase = GetInvoiceDetailsUseCase(mockRepository)
    }
    
    @Test
    fun `invoke returns success when repository succeeds`() = runTest {
        // Given
        val invoiceId = "test-invoice-id"
        val mockInvoice = Invoice(
            id = invoiceId,
            index = 1,
            date = LocalDate.of(2024, 1, 1),
            description = "Test Invoice",
            items = listOf(
                InvoiceDetailsLineItem(
                    id = "item1",
                    name = "Service 1",
                    quantity = 2,
                    priceInCents = 5000
                )
            )
        )
        coEvery { mockRepository.getInvoiceById(invoiceId) } returns InvoiceResult.Success(mockInvoice)
        
        // When
        val result = useCase(invoiceId)
        
        // Then
        assertTrue(result is InvoiceResult.Success)
        assertEquals(mockInvoice, (result as InvoiceResult.Success).data)
    }
    
    @Test
    fun `invoke returns network error when repository fails with network error`() = runTest {
        // Given
        val invoiceId = "test-invoice-id"
        val error = InvoiceError.Network
        coEvery { mockRepository.getInvoiceById(invoiceId) } returns InvoiceResult.Failure(error)
        
        // When
        val result = useCase(invoiceId)
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertEquals(error, (result as InvoiceResult.Failure).error)
    }
    
    @Test
    fun `invoke returns not found error when invoice does not exist`() = runTest {
        // Given
        val invoiceId = "non-existent-id"
        val error = InvoiceError.NotFound("Invoice not found")
        coEvery { mockRepository.getInvoiceById(invoiceId) } returns InvoiceResult.Failure(error)
        
        // When
        val result = useCase(invoiceId)
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertTrue((result as InvoiceResult.Failure).error is InvoiceError.NotFound)
    }
    
    @Test
    fun `invoke returns malformed data error when repository fails with parsing error`() = runTest {
        // Given
        val invoiceId = "test-invoice-id"
        val error = InvoiceError.MalformedData
        coEvery { mockRepository.getInvoiceById(invoiceId) } returns InvoiceResult.Failure(error)
        
        // When
        val result = useCase(invoiceId)
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertEquals(error, (result as InvoiceResult.Failure).error)
    }
    
    @Test
    fun `invoke returns unknown error when repository fails with unknown error`() = runTest {
        // Given
        val invoiceId = "test-invoice-id"
        val error = InvoiceError.Unknown("Unexpected error")
        coEvery { mockRepository.getInvoiceById(invoiceId) } returns InvoiceResult.Failure(error)
        
        // When
        val result = useCase(invoiceId)
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertTrue((result as InvoiceResult.Failure).error is InvoiceError.Unknown)
    }
}
