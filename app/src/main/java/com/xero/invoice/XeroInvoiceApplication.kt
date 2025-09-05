package com.xero.invoice

import android.app.Application
import com.xero.invoice.di.appDI
import org.kodein.di.DI
import org.kodein.di.DIAware

/**
 * Application class for the Xero Invoice app.
 */
class XeroInvoiceApplication : Application(), DIAware {
    
    override val di: DI = appDI
}
