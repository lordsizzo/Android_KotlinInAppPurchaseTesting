package lou.sizzo.kotlininapppurchasetesting.ui

import android.app.Activity
import android.content.Context
import android.view.Gravity
import android.view.View
import android.widget.LinearLayout
import androidx.appcompat.widget.AppCompatButton
import androidx.appcompat.widget.AppCompatTextView
import androidx.cardview.widget.CardView
import com.android.billingclient.api.BillingFlowParams
import com.android.billingclient.api.SkuDetails
import lou.sizzo.kotlininapppurchasetesting.R
import lou.sizzo.kotlininapppurchasetesting.extensions.toast
import lou.sizzo.kotlininapppurchasetesting.purchase.PurchaseInit

class Views {

    private fun LinearLayoutView(): LinearLayout.LayoutParams {

        val ApCompTV = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        return ApCompTV
    }

    private fun LinearLayoutViewWithMargin(left: Int, top: Int, right: Int, bottom: Int): LinearLayout.LayoutParams{

        val lp = LinearLayout.LayoutParams(
            LinearLayout.LayoutParams.MATCH_PARENT,
            LinearLayout.LayoutParams.WRAP_CONTENT
        )
        lp.setMargins(left, top, right, bottom)

        return lp
    }
    fun ViewToBuy(context: Context, activity: Activity, skuDetails: SkuDetails?): View {

        val cardViewContainer = CardView(context)
        cardViewContainer.layoutParams = LinearLayoutViewWithMargin(15, 15, 15, 15)
        cardViewContainer.cardElevation = 5.0f
        cardViewContainer.setPadding(10, 10, 10, 10)

        val linearLayoutCardView = LinearLayout(context)
        linearLayoutCardView.setPadding(10, 10, 10, 10)
        linearLayoutCardView.orientation = LinearLayout.VERTICAL
        linearLayoutCardView.layoutParams = LinearLayoutViewWithMargin(5, 5, 5, 5)
        linearLayoutCardView.gravity = Gravity.CENTER

        val textTitle = AppCompatTextView(context);
        textTitle.layoutParams = LinearLayoutView()
        textTitle.gravity = Gravity.CENTER_HORIZONTAL
        textTitle.text = skuDetails!!.title
        textTitle.setPadding(10, 10, 10, 10)

        linearLayoutCardView.addView(textTitle)

        val textDescrption = AppCompatTextView(context);
        textDescrption.layoutParams = LinearLayoutView()
        textDescrption.gravity = Gravity.CENTER_HORIZONTAL
        textDescrption.text = skuDetails.description
        textDescrption.setPadding(10, 10, 10, 10)

        linearLayoutCardView.addView(textDescrption)

        val btnBuy = AppCompatButton(context)
        btnBuy.layoutParams = LinearLayoutViewWithMargin(15, 15, 15, 15)
        btnBuy.text = "Buy"
        btnBuy.setBackgroundColor(context.resources.getColor(R.color.colorAccent))
        btnBuy.setPadding(10, 10, 10, 10)
        btnBuy.setOnClickListener{
            skuDetails?.let {
                val billingFlowParams = BillingFlowParams.newBuilder()
                    .setSkuDetails(it)
                    .build()
                PurchaseInit.billingClient?.launchBillingFlow(activity, billingFlowParams)?.responseCode
            }?: context.toast("Nothing to buy")
        }
        linearLayoutCardView.addView(btnBuy)

        cardViewContainer.addView(linearLayoutCardView)
        return cardViewContainer
    }
}