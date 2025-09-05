package com.xero.invoice.di

import androidx.lifecycle.ViewModel
import com.google.gson.GsonBuilder
import com.xero.invoice.data.remote.service.InvoiceApiService
import com.xero.invoice.data.repository.InvoiceRepositoryImpl
import com.xero.invoice.domain.repository.InvoiceRepository
import com.xero.invoice.domain.usecase.GetInvoiceDetailsUseCase
import com.xero.invoice.domain.usecase.GetInvoicesUseCase
import com.xero.invoice.ui.invoice_detail.InvoiceDetailViewModel
import com.xero.invoice.ui.invoice_list.InvoiceListViewModel
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import org.kodein.di.DI
import org.kodein.di.bindFactory
import org.kodein.di.bindSingleton
import org.kodein.di.instance
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

/**
 * Network module
 */
val networkModule = DI.Module("NetworkModule") {
    
    bindSingleton<OkHttpClient> {
        OkHttpClient.Builder()
            .addInterceptor(HttpLoggingInterceptor().apply {
                level = HttpLoggingInterceptor.Level.BODY
            })
            .build()
    }
    
    bindSingleton<Retrofit> {
        val gson = GsonBuilder()
            .setLenient()
            .create()
            
        Retrofit.Builder()
            .baseUrl("https://storage.googleapis.com/")
            .client(instance())
            .addConverterFactory(GsonConverterFactory.create(gson))
            .build()
    }
    
    bindSingleton<InvoiceApiService> {
        instance<Retrofit>().create(InvoiceApiService::class.java)
    }
}

/**
 * Repository module
 */
val repositoryModule = DI.Module("RepositoryModule") {
    
    bindSingleton<InvoiceRepository> {
        InvoiceRepositoryImpl(instance())
    }
}

/**
 * Use case module
 */
val useCaseModule = DI.Module("UseCaseModule") {
    
    bindSingleton<GetInvoicesUseCase> {
        GetInvoicesUseCase(instance())
    }
    
    bindSingleton<GetInvoiceDetailsUseCase> {
        GetInvoiceDetailsUseCase(instance())
    }
}

/**
 * ViewModel module - binding ViewModels with tags for ViewModelFactory
 */
val viewModelModule = DI.Module("ViewModelModule") {
    
    bindSingleton<ViewModel>(tag = "InvoiceListViewModel") {
        InvoiceListViewModel(instance())
    }
    
    bindFactory<String, ViewModel>(tag = "InvoiceDetailViewModel") { invoiceId ->
        InvoiceDetailViewModel(instance(), invoiceId)
    }
}

/**
 * Complete DI container with all modules.
 */
val appDI = DI {
    import(networkModule)
    import(repositoryModule)
    import(useCaseModule)
    import(viewModelModule)
}
