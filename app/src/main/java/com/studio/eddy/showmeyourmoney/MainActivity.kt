package com.studio.eddy.showmeyourmoney

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.android.billingclient.api.*
import kotlinx.android.synthetic.main.activity_main.*
import java.lang.StringBuilder

class MainActivity : AppCompatActivity(), PurchasesUpdatedListener {
    private lateinit var billingClient: BillingClient
    private lateinit var productAdapter: ProductAdapter
    private val TAG: String = "SHOW ME YOUR MONEY"
    override fun onPurchasesUpdated(
        p0: BillingResult?,
        p1: MutableList<Purchase>?
    ) {
        val sb = StringBuilder()

        if (p0?.responseCode == BillingClient.BillingResponseCode.OK) {
            for (i in p1!!) {
                sb.append("${i.originalJson} \n")
            }

            val consumeParams =
                ConsumeParams.newBuilder().setDeveloperPayload(p1?.get(0).developerPayload)
                    .setPurchaseToken(p1?.get(0).purchaseToken)
                    .build()

            billingClient.consumeAsync(consumeParams) { billingResult, outToken ->
                if (billingResult.responseCode == BillingClient.BillingResponseCode.OK) {
                    // Handle the success of the consume operation.
                    // For example, increase the number of coins inside the user's basket.
                    history_tv.text = "Thank you for donation \n ${p1?.get(0).orderId}"
                }
            }
        } else {
            Toast.makeText(baseContext, "Failed", Toast.LENGTH_LONG).show()
        }

    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setupBillingClinet()
        setupViews()

    }

    private fun setupBillingClinet() {
        billingClient = BillingClient.newBuilder(this)
            .setListener(this)
            .enablePendingPurchases()
            .build()

        billingClient.startConnection(object : BillingClientStateListener {
            override fun onBillingServiceDisconnected() {
                Log.d(TAG, "billing service disconnected")
            }

            override fun onBillingSetupFinished(p0: BillingResult?) {
                Log.d(TAG, "setup finished")
                setupBilling()
            }

        })
    }

    private fun setupViews() {
        productAdapter = ProductAdapter(emptyList())
        productAdapter.onProductClickListener = object : ProductAdapter.OnProductClickListener {
            override fun onClicked(skuDetails: SkuDetails) {
                billingClient.launchBillingFlow(
                    this@MainActivity,
                    BillingFlowParams.newBuilder().setSkuDetails(skuDetails).build()
                )
            }
        }
        product_recycler_view.apply {
            adapter = productAdapter
            layoutManager = LinearLayoutManager(baseContext, LinearLayoutManager.VERTICAL, false)
        }
    }

    private fun setupBilling() {
        val skuParams = SkuDetailsParams
            .newBuilder()
            .setSkusList(MerchantSku.getSkus())
            .setType(BillingClient.SkuType.INAPP)
            .build()

        billingClient.querySkuDetailsAsync(skuParams) { p0, p1 ->
            if (p0?.responseCode == BillingClient.BillingResponseCode.OK) {
                val l = p1!!.sortedWith(compareBy { it.price.substring(1,it.price.length-1).toFloat() })
                productAdapter.list = l
                productAdapter.notifyDataSetChanged()
            } else {
                Toast.makeText(this@MainActivity, "fail to get sku", Toast.LENGTH_LONG)
                    .show()
            }
        }

//        billingClient.queryPurchaseHistoryAsync(
//            BillingClient.SkuType.INAPP,
//            object : PurchaseHistoryResponseListener {
//                override fun onPurchaseHistoryResponse(
//                    p0: BillingResult?,
//                    p1: MutableList<PurchaseHistoryRecord>?
//                ) {
//                    if (p0?.responseCode == BillingClient.BillingResponseCode.OK) {
//                        val sb = StringBuilder()
//
//                        if (p1 != null) {
//                            for (record in p1) {
//                                sb.append("${record.sku} \n")
//
//                            }
//                        }
//                        history_tv.text = sb.toString()
//                    }
//                }
//            })

//        billingClient.queryPurchases(BillingClient.SkuType.INAPP).purchasesList.apply {
//            val sb = StringBuilder()
//
//            for (record in this) {
//                sb.append("${record.purchaseTime} \n")
//            }
//            history_tv.text = sb.toString()
//
//        }
    }
}
