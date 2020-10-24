package com.sherifnasser.themarketmanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.ListAdapter
import androidx.recyclerview.widget.RecyclerView
import com.sherifnasser.themarketmanager.database.model.Product
import com.sherifnasser.themarketmanager.database.model.SoldProduct
import com.sherifnasser.themarketmanager.databinding.OrderProductsRecyclerViewItemLayoutBinding
import javax.inject.Inject

class OrderProductsRecyclerViewAdapter
@Inject constructor():ListAdapter<Product,OrderProductsRecyclerViewAdapter.SoldProductItemViewHolder>(DIFF_CALLBACK){

    companion object{
        private val DIFF_CALLBACK=object:DiffUtil.ItemCallback<Product>(){
            override fun areItemsTheSame(oldItem:Product,newItem:Product)=oldItem.productId==newItem.productId
            override fun areContentsTheSame(oldItem:Product,newItem:Product)=oldItem==newItem
        }
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int)=
        SoldProductItemViewHolder(
            OrderProductsRecyclerViewItemLayoutBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
        )

    override fun onBindViewHolder(holder:SoldProductItemViewHolder,position:Int){
        getItem(position).let{
            holder.bindTo(it)
        }
    }

    // Since there is no SAM-conversion with interface in kotlin, I decided to use higher-order function to avoid the ugly code.
    private lateinit var onItemClickListener:(Product)->Unit

    fun setOnItemClickListener(onItemClickListener:(Product)->Unit){
        this.onItemClickListener=onItemClickListener
    }

    inner class SoldProductItemViewHolder(private val binding:OrderProductsRecyclerViewItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bindTo(product:Product){
            val solProduct=(product as SoldProduct)
            binding.productNameTextView.text=product.name
            binding.productPriceTextView.text=product.price.toString()
            binding.productSoldQuantityTextView.text=solProduct.soldQuantity.toString()
            binding.productTotalPriceTextView.text=(solProduct.soldQuantity*solProduct.price).toString()
            itemView.setOnClickListener{onItemClickListener(product)}
        }

    }

}