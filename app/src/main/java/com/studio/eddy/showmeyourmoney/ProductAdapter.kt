package com.studio.eddy.showmeyourmoney

import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView
import com.android.billingclient.api.SkuDetails

class ProductAdapter(var list: List<SkuDetails>) :
    RecyclerView.Adapter<ProductAdapter.ProductVH>() {

    var onProductClickListener: OnProductClickListener? = null

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ProductVH {
        val v = LayoutInflater.from(parent.context)
            .inflate(R.layout.product_item_view_layout, parent, false)
        return ProductVH(v)
    }

    override fun getItemCount(): Int {
        return list.size
    }

    override fun onBindViewHolder(holder: ProductVH, position: Int) {
        holder.bind(list[position], onProductClickListener)
    }

    class ProductVH(itemView: View) : RecyclerView.ViewHolder(itemView) {
        private val text = itemView.findViewById<TextView>(R.id.product_button_name)

        fun bind(skuDetails: SkuDetails, onProductClickListener: OnProductClickListener?) {
            text.text = "Donate ${skuDetails.price}"
            text.setOnClickListener {
                onProductClickListener?.onClicked(skuDetails)
            }
        }
    }

    interface OnProductClickListener {
        fun onClicked(skuDetails: SkuDetails)
    }
}