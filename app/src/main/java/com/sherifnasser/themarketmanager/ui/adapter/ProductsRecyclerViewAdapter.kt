package com.sherifnasser.themarketmanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.database.model.Product
import com.sherifnasser.themarketmanager.databinding.ProductsRecyclerViewItemLayoutBinding
import javax.inject.Inject

class ProductsRecyclerViewAdapter
@Inject constructor():PagedListAdapter<Product,ProductsRecyclerViewAdapter.ProductItemViewHolder>(diffCallBack){

    companion object{
        private val diffCallBack=object:DiffUtil.ItemCallback<Product>(){
            override fun areItemsTheSame(oldItem:Product,newItem:Product):Boolean=oldItem.id==newItem.id
            override fun areContentsTheSame(oldItem:Product,newItem:Product):Boolean=oldItem==newItem
        }
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int):ProductItemViewHolder=
        ProductItemViewHolder(
            ProductsRecyclerViewItemLayoutBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
        )

    override fun onBindViewHolder(holder:ProductItemViewHolder,position:Int){
        getItem(position).let{
            if(it!=null)holder.bindTo(it)
        }
    }

    // Since there is no SAM-conversion with interface in kotlin, I decided to use higher-order function to avoid the ugly code.
    private lateinit var onItemClickListener:(Product)->Unit

    fun setOnItemClickListener(onItemClickListener:(Product)->Unit){
        this.onItemClickListener=onItemClickListener
    }

    inner class ProductItemViewHolder(private val binding:ProductsRecyclerViewItemLayoutBinding):RecyclerView.ViewHolder(binding.root){

        fun bindTo(product:Product){
            val context=binding.root.context
            binding.productNameTextView.text=product.name
            binding.productPriceTextView.text=context.getString(R.string.price_colon,product.price.toString())
            binding.productAvailableQuantityTextView.text=context.getString(R.string.available_quantity_colon,product.availableQuantity.toString())
            itemView.setOnClickListener{
                onItemClickListener(product)
            }
        }

    }
}