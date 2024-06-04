package com.ntg.vocabs.screens

import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.navigation.NavController
import com.ntg.vocabs.R
import com.ntg.vocabs.components.LoadingView
import com.ntg.vocabs.nav.Screens
import com.ntg.vocabs.util.toast
import com.ntg.vocabs.vm.LoginViewModel
import com.revenuecat.purchases.CustomerInfo
import com.revenuecat.purchases.models.StoreTransaction
import com.revenuecat.purchases.ui.revenuecatui.ExperimentalPreviewRevenueCatUIPurchasesAPI
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialog
import com.revenuecat.purchases.ui.revenuecatui.PaywallDialogOptions
import com.revenuecat.purchases.ui.revenuecatui.PaywallListener

@OptIn(ExperimentalPreviewRevenueCatUIPurchasesAPI::class)
@Composable
fun PaywallScreen(
    navController: NavController,
    loginViewModel: LoginViewModel
){


    LoadingView()

    val ctx = LocalContext.current
    PaywallDialog(
        PaywallDialogOptions.Builder()
            .setRequiredEntitlementIdentifier("VocabsMaster Plus")
            .setListener(
                object : PaywallListener {
                    override fun onPurchaseCompleted(customerInfo: CustomerInfo, storeTransaction: StoreTransaction) {
                        loginViewModel.setPurchase(true)
                        navController.navigate(Screens.SuccessPurchaseScreen.name + "?type=Purchase")
                    }
                    override fun onRestoreCompleted(customerInfo: CustomerInfo) {
                        if (customerInfo.allPurchasedProductIds.isNotEmpty()){
                            loginViewModel.setPurchase(true)
                            navController.navigate(Screens.SuccessPurchaseScreen.name + "?type=Restored")
                        }else{
                            ctx.toast(R.string.err_restoration)
                        }
                    }

                    override fun onPurchaseCancelled() {
                        super.onPurchaseCancelled()
                        navController.popBackStack()
                    }
                }
            )
            .build()
    )
}