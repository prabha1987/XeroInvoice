package com.xero.invoice.data.repository

import com.xero.invoice.data.remote.entity.InvoiceDetailsLineItemEntity
import com.xero.invoice.data.remote.entity.InvoiceEntity
import com.xero.invoice.data.remote.entity.InvoicesResponseEntity
import com.xero.invoice.data.remote.service.InvoiceApiService
import com.xero.invoice.domain.common.InvoiceError
import com.xero.invoice.domain.common.InvoiceResult
import io.mockk.coEvery
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Assert.assertEquals
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Test
import java.io.IOException
import java.net.UnknownHostException
import com.google.gson.stream.MalformedJsonException
import retrofit2.HttpException

class InvoiceRepositoryImplTest {
    
    private lateinit var repository: InvoiceRepositoryImpl
    private lateinit var mockApiService: InvoiceApiService
    
    @Before
    fun setUp() {
        mockApiService = mockk()
        repository = InvoiceRepositoryImpl(mockApiService)
    }
    
    @Test
    fun `getInvoices returns success when api call succeeds`() = runTest {
        // Given
        val mockResponse = InvoicesResponseEntity(
            items = listOf(
                InvoiceEntity(
                    id = "1",
                    date = "2024-01-01T10:00:00",
                    description = "Test Invoice",
                    items = listOf(
                        InvoiceDetailsLineItemEntity(
                            id = "item1",
                            name = "Service 1",
                            quantity = 2,
                            priceInCents = 5000
                        )
                    )
                )
            )
        )
        coEvery { mockApiService.getInvoices(any()) } returns mockResponse
        
        // When
        val result = repository.getInvoices()
        
        // Then
        assertTrue(result is InvoiceResult.Success)
        val invoices = (result as InvoiceResult.Success).data
        assertEquals(1, invoices.size)
        assertEquals("1", invoices[0].id)
        assertEquals("Test Invoice", invoices[0].description)
    }
    
    @Test
    fun `getInvoices returns unknown error when api call throws IOException`() = runTest {
        // Given
        coEvery { mockApiService.getInvoices(any()) } throws IOException("Network error")
        
        // When
        val result = repository.getInvoices()
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertTrue((result as InvoiceResult.Failure).error is InvoiceError.Unknown)
    }
    
    @Test
    fun `getInvoices returns network error when api call throws UnknownHostException`() = runTest {
        // Given
        coEvery { mockApiService.getInvoices(any()) } throws UnknownHostException("No internet")
        
        // When
        val result = repository.getInvoices()
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertEquals(InvoiceError.Network, (result as InvoiceResult.Failure).error)
    }
    
    @Test
    fun `getInvoices returns network error when api call throws HttpException`() = runTest {
        // Given
        val httpException = mockk<HttpException>()
        coEvery { mockApiService.getInvoices(any()) } throws httpException
        
        // When
        val result = repository.getInvoices()
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertEquals(InvoiceError.Network, (result as InvoiceResult.Failure).error)
    }
    
    @Test
    fun `getInvoices returns malformed data error when api call throws MalformedJsonException`() = runTest {
        // Given
        coEvery { mockApiService.getInvoices(any()) } throws MalformedJsonException("Invalid JSON")
        
        // When
        val result = repository.getInvoices()
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertEquals(InvoiceError.MalformedData, (result as InvoiceResult.Failure).error)
    }
    
    @Test
    fun `getInvoiceById returns success when invoice exists in cache`() = runTest {
        // Given - First populate cache
        val mockResponse = InvoicesResponseEntity(
            items = listOf(
                InvoiceEntity(
                    id = "test-id",
                    date = "2024-01-01T10:00:00",
                    description = "Test Invoice",
                    items = listOf(
                        InvoiceDetailsLineItemEntity(
                            id = "item1",
                            name = "Service 1",
                            quantity = 2,
                            priceInCents = 5000
                        )
                    )
                )
            )
        )
        coEvery { mockApiService.getInvoices(any()) } returns mockResponse
        
        // Populate cache
        repository.getInvoices()
        
        // When
        val result = repository.getInvoiceById("test-id")
        
        // Then
        assertTrue(result is InvoiceResult.Success)
        val invoice = (result as InvoiceResult.Success).data
        assertEquals("test-id", invoice.id)
        assertEquals("Test Invoice", invoice.description)
    }
    
    @Test
    fun `getInvoiceById returns not found error when invoice does not exist`() = runTest {
        // Given - Populate cache with different invoice
        val mockResponse = InvoicesResponseEntity(
            items = listOf(
                InvoiceEntity(
                    id = "different-id",
                    date = "2024-01-01T10:00:00",
                    description = "Different Invoice",
                    items = emptyList()
                )
            )
        )
        coEvery { mockApiService.getInvoices(any()) } returns mockResponse
        
        // Populate cache
        repository.getInvoices()
        
        // When
        val result = repository.getInvoiceById("non-existent-id")
        
        // Then
        assertTrue(result is InvoiceResult.Failure)
        assertTrue((result as InvoiceResult.Failure).error is InvoiceError.NotFound)
    }
}
