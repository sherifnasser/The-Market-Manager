package com.sherifnasser.themarketmanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.database.model.Product
import com.sherifnasser.themarketmanager.databinding.AddOrderSelectedProductRecyclerViewItemLayoutBinding
import javax.inject.Inject

class AddOrderSelectedProductRecyclerViewAdapter
@Inject constructor():PagedListAdapter<Product,AddOrderSelectedProductRecyclerViewAdapter.SelectedProductItemViewHolder>(DIFF_CALLBACK){

    companion object{
        private val DIFF_CALLBACK=object:DiffUtil.ItemCallback<Product>(){
            override fun areItemsTheSame(oldItem:Product,newItem:Product)=oldItem.productId==newItem.productId
            override fun areContentsTheSame(oldItem:Product,newItem:Product)=oldItem==newItem
        }
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int)=
        SelectedProductItemViewHolder(
            AddOrderSelectedProductRecyclerViewItemLayoutBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
        )

    override fun onBindViewHolder(holder:SelectedProductItemViewHolder, position:Int){
        getItem(position).let{
            if(it!=null)holder.bindTo(it)
        }
    }

    // Since there is no SAM-conversion with interface in kotlin, I decided to use higher-order function to avoid the ugly code.
    private lateinit var onItemClickListener:(Product)->Unit

    fun setOnItemClickListener(onItemClickListener:(Product)->Unit){
        this.onItemClickListener=onItemClickListener
    }

    inner class SelectedProductItemViewHolder(private val binding:AddOrderSelectedProductRecyclerViewItemLayoutBinding):RecyclerView.ViewHolder(binding.root){
        fun bindTo(product:Product){
            val context=binding.root.context
            binding.productNameTextView.text=product.name
            binding.productPriceTextView.text=product.price.toString()
            binding.productAvailableQuantityTextView.text=context.getString(R.string.available_colon,product.availableQuantity.toString())
            itemView.setOnClickListener{onItemClickListener(product)}
        }

    }
}