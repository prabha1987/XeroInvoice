package com.xero.invoice.ui.navigation

import androidx.compose.runtime.Composable
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.xero.invoice.ui.common.ViewModelFactory
import com.xero.invoice.ui.invoice_detail.InvoiceDetailScreen
import com.xero.invoice.ui.invoice_detail.InvoiceDetailViewModel
import com.xero.invoice.ui.invoice_list.InvoiceListScreen
import com.xero.invoice.ui.invoice_list.InvoiceListViewModel
import org.kodein.di.compose.localDI
import org.kodein.di.direct

object InvoiceRoutes {
    const val INVOICE_LIST = "invoice_list"
    const val INVOICE_DETAIL = "invoice_detail/{invoiceId}"
    
    fun invoiceDetail(invoiceId: String) = "invoice_detail/$invoiceId"
}

@Composable
fun InvoiceNavigation(navController: NavHostController) {
    val di = localDI()
    
    NavHost(
        navController = navController,
        startDestination = InvoiceRoutes.INVOICE_LIST
    ) {
        composable(route = InvoiceRoutes.INVOICE_LIST) {
            val viewModel: InvoiceListViewModel = viewModel(
                factory = ViewModelFactory.simple(di.direct)
            )
            
            InvoiceListScreen(
                onInvoiceClick = { invoiceId ->
                    navController.navigate(InvoiceRoutes.invoiceDetail(invoiceId))
                },
                viewModel = viewModel
            )
        }
        
        composable(
            route = InvoiceRoutes.INVOICE_DETAIL,
            arguments = listOf(
                navArgument("invoiceId") {
                    type = NavType.StringType
                }
            )
        ) { backStackEntry ->
            val invoiceId = backStackEntry.arguments?.getString("invoiceId") ?: ""
            val viewModel: InvoiceDetailViewModel = viewModel(
                factory = ViewModelFactory.forInvoiceDetail(di.direct, invoiceId)
            )
            
            InvoiceDetailScreen(
                onBackClick = {
                    navController.popBackStack()
                },
                viewModel = viewModel
            )
        }
    }
}
