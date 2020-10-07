package com.sherifnasser.themarketmanager.ui.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagedListAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.databinding.OrdersRecyclerViewItemLayoutBinding
import javax.inject.Inject
import java.text.DateFormat

class OrdersRecyclerViewAdapter
@Inject constructor(
    private val dateFormat:DateFormat
):PagedListAdapter<Order,OrdersRecyclerViewAdapter.OrderItemViewHolder>(DIFF_CALLBACK){
    companion object{
        val DIFF_CALLBACK=object:DiffUtil.ItemCallback<Order>(){
            override fun areItemsTheSame(oldItem:Order,newItem:Order)=oldItem.orderId==newItem.orderId
            override fun areContentsTheSame(oldItem:Order,newItem:Order)=oldItem==newItem
        }
    }

    override fun onCreateViewHolder(parent:ViewGroup,viewType:Int)=
        OrderItemViewHolder(
            OrdersRecyclerViewItemLayoutBinding
                .inflate(LayoutInflater.from(parent.context),parent,false)
        )

    override fun onBindViewHolder(holder:OrderItemViewHolder,position:Int){
        getItem(position).let{
            if(it!=null)holder.bindTo(it)
        }
    }

    // Since there is no SAM-conversion with interface in kotlin, I decided to use higher-order function to avoid the ugly code.
    private lateinit var onItemClickListener:(Order)->Unit

    fun setOnItemClickListener(onItemClickListener:(Order)->Unit){
        this.onItemClickListener=onItemClickListener
    }

    inner class OrderItemViewHolder(private val binding:OrdersRecyclerViewItemLayoutBinding):RecyclerView.ViewHolder(binding.root){

        fun bindTo(order:Order){
            val context=binding.root.context
            binding.orderIdTextView.text=context.getString(R.string.id_colon,order.orderId)
            val orderDate=dateFormat.format(order.date)
            binding.orderDateTextView.text=context.getString(R.string.date_colon,orderDate)
            binding.orderTotalTextView.text=context.getString(R.string.total_colon,order.total.toString())
            itemView.setOnClickListener{onItemClickListener(order)}
        }
    }
}