package lou.sizzo.kotlininapppurchasetesting.purchase

import android.app.Activity
import android.content.Context
import android.util.Log
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.*
import com.android.billingclient.api.Purchase
import lou.sizzo.kotlininapppurchasetesting.MainActivity
import lou.sizzo.kotlininapppurchasetesting.R
import lou.sizzo.kotlininapppurchasetesting.databinding.ActivityMainBinding
import lou.sizzo.kotlininapppurchasetesting.extensions.toast
import lou.sizzo.kotlininapppurchasetesting.ui.Views
import lou.sizzo.kotlininapppurchasetesting.utils.Config

class PurchaseInit {

    /*
    ----> Testing responses <----

    * android.test.purchased
    * android.test.canceled
    * android.test.refunded
    * android.test.item_unavailable

    */
    companion object{
        var billingClient: BillingClient?=null
        //var skuDetails: SkuDetails?=null
    }

    //Starting connection with Billing Client
    fun startConnection(binding: ActivityMainBinding, context: Context, activity: Activity) {
        billingClient?.startConnection(object : BillingClientStateListener {
            override fun onBillingSetupFinished(billingResult: BillingResult) {
                if (billingResult.responseCode ==  BillingClient.BillingResponseCode.OK) {
                    // The BillingClient is ready. You can query purchases here.
                    Log.v("TAG_INAPP","Setup Billing Done")
                    var listItems = ArrayList<String>()
                    listItems.add(Config().skuPurchaseTesting)
                    listItems.add(Config().skuCanceledTesting)
                    listItems.add(Config().skuRefundedTesting)
                    listItems.add(Config().skuItemUnavailableTesting)
                    queryAvaliableProducts(binding, listItems, context, activity)
                }
            }
            override fun onBillingServiceDisconnected() {
                // Try to restart the connection on the next request to
                // Google Play by calling the startConnection() method.
            }
        })
    }

    //Check if there are avaliable products
    private fun queryAvaliableProducts(binding: ActivityMainBinding, skuList: ArrayList<String>, context: Context, activity: Activity) {

        val params = SkuDetailsParams.newBuilder()
        params.setSkusList(skuList).setType(BillingClient.SkuType.INAPP)
        billingClient?.querySkuDetailsAsync(params.build()) { billingResult, skuDetailsList ->
            // Process the result.
            if (billingResult.responseCode == BillingClient.BillingResponseCode.OK && !skuDetailsList.isNullOrEmpty()) {

                //println("Este result: ${skuDetailsList}")
                for (skuDetails in skuDetailsList) {
                    Log.v("TAG_INAPP","skuDetailsList : ${skuDetailsList}")
                    //This list should contain the products added above
                    binding.containerPurchases.addView(Views().ViewToBuy(context, activity, skuDetails))
                }
            }else{
                Log.v("TAG_INAPP","errorSku")
            }
        }
    }

    /*//Update UI if, in fact there are producs avaliables
    fun updateUI(skuDetails: SkuDetails?, binding: ActivityMainBinding) {
        skuDetails?.let {
            PurchaseInit.skuDetails = it
            binding.txtProductName.text = it.title
            binding.txtProductDescription.text = it.description
            showUIElements(binding)
        }
    }

    //Show them with titles and descriptions
    private fun showUIElements(binding: ActivityMainBinding) {
        binding.txtProductName.visibility = View.VISIBLE
        binding.txtProductDescription?.visibility = View.VISIBLE
        binding.txtProductBuy.visibility = View.VISIBLE
    }*/


    //Make purchase if it's a Consumed Purchase
    fun handleConsumedPurchases(purchase: Purchase, context: Context, binding: ActivityMainBinding) {
        Log.d("TAG_INAPP", "handleConsumablePurchasesAsync foreach it is $purchase")
        val params = ConsumeParams.newBuilder().setPurchaseToken(purchase.purchaseToken).build()
        billingClient?.consumeAsync(params) { billingResult, _ ->
            when (billingResult.responseCode) {
                BillingClient.BillingResponseCode.OK -> {
                    // Update the appropriate tables/databases to grant user the items
                    context.toast("Felicidades por tu compra!")
                    //binding.txtProductBuy.isEnabled = true
                }
                else -> {
                    Log.w("TAG_INAPP en result", billingResult.debugMessage)
                }
            }
        }
    }

    //Make purchase if it's a No Consumed Purchase
    fun handleNonConcumablePurchase(purchase: Purchase, context: Context) {
        Log.v("TAG_INAPP","handlePurchase : ${purchase}")
        if (purchase.purchaseState == Purchase.PurchaseState.PURCHASED) {
            if (!purchase.isAcknowledged) {
                val acknowledgePurchaseParams = AcknowledgePurchaseParams.newBuilder()
                    .setPurchaseToken(purchase.purchaseToken).build()
                billingClient?.acknowledgePurchase(acknowledgePurchaseParams) { billingResult ->
                    val billingResponseCode = billingResult.responseCode
                    val billingDebugMessage = billingResult.debugMessage

                    Log.v("TAG_INAPP","response code: $billingResponseCode")
                    Log.v("TAG_INAPP","debugMessage : $billingDebugMessage")

                }
            }
        }
    }
}