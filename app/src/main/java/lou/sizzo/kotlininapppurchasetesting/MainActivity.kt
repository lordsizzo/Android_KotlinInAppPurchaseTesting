package lou.sizzo.kotlininapppurchasetesting

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.android.billingclient.api.*
import lou.sizzo.kotlininapppurchasetesting.databinding.ActivityMainBinding
import lou.sizzo.kotlininapppurchasetesting.extensions.toast
import lou.sizzo.kotlininapppurchasetesting.purchase.PurchaseInit

class MainActivity : AppCompatActivity() {
    lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        startBillingClient()
        initListeners()
    }

    //Initial button listeners
    private fun initListeners() {
        /*binding.txtProductBuy.setOnClickListener {
            // Retrieve a value for "skuDetails" by calling querySkuDetailsAsync().
            binding.txtProductBuy.isEnabled = false
            PurchaseInit.skuDetails?.let {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(it)
                    .build()
                PurchaseInit.billingClient?.launchBillingFlow(this, billingFlowParams)?.responseCode
            }?:noSKUMessage()
        }*/
    }

    //Show a Toast if there is nothing to buy
    private fun noSKUMessage() {
        toast("Nothing to buy")
        //binding.txtProductBuy.isEnabled = true
    }

    //Start Billing Client
    fun startBillingClient() {
        PurchaseInit.billingClient = BillingClient.newBuilder(this)
            .setListener(purchaseUpdateListener)
            .enablePendingPurchases()
            .build()
        PurchaseInit().startConnection(binding, this, this)
    }


    //Purchase listener when everything goes alright
    private val purchaseUpdateListener =
        PurchasesUpdatedListener { billingResult, purchases ->
            //Cycling all the purchases returned
            when (billingResult.responseCode){
                BillingClient.BillingResponseCode.OK -> {
                    if (purchases != null) {
                        for (purchase in purchases) {

                            PurchaseInit().handleConsumedPurchases(purchase, this, binding)

                        }
                    }else{
                        toast("No items to purchase")
                    }
                }
                BillingClient.BillingResponseCode.USER_CANCELED -> {
                    toast("Purchase is cancelled ${billingResult.debugMessage}")
                    //binding.txtProductBuy.isEnabled = true
                }
                BillingClient.BillingResponseCode.ITEM_UNAVAILABLE -> {
                    toast("Item unavailable")
                    //binding.txtProductBuy.isEnabled = true
                }
                else -> {
                    toast("Unexpected error ${BillingClient.BillingResponseCode.ITEM_UNAVAILABLE}")
                }
            }
        }

}