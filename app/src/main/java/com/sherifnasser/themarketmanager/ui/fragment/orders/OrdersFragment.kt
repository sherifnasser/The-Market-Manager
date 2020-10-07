package com.sherifnasser.themarketmanager.ui.fragment.orders

import android.os.Bundle
import android.util.Log
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.databinding.FragmentOrdersBinding
import com.sherifnasser.themarketmanager.ui.adapter.OrdersRecyclerViewAdapter
import com.sherifnasser.themarketmanager.ui.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class OrdersFragment:Fragment(){


    private var binding:FragmentOrdersBinding?=null
    private val orderViewModel by activityViewModels<OrderViewModel>()
    @Inject lateinit var mAdapter:OrdersRecyclerViewAdapter

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?{
        binding=FragmentOrdersBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView(){
        binding=null
        super.onDestroyView()
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)

        setupRecyclerView()
        orderViewModel.orderInfo.observe(viewLifecycleOwner){
            // todo -> navigate to order info fragment
            if(it!=null){

            }
        }

        binding!!.addOrderFab.setOnClickListener{
            // todo
        }
    }

    private fun setupRecyclerView(){
        // When user click any item in the recyclerView
        mAdapter.setOnItemClickListener{
            orderViewModel.setOrderInfo(it)
        }

        // RecyclerView
        binding!!.ordersRecyclerView.apply{
            adapter=mAdapter
            layoutManager=LinearLayoutManager(context)
        }

        /*
        It will show any orders in recyclerView.
         */
        orderViewModel.allOrders.observe(viewLifecycleOwner){list->
            // Submit the new list and display it.
            mAdapter.submitList(list){
                // Scroll to first item when list submitted
                binding!!.ordersRecyclerView.let{
                    it.post{it.scrollToPosition(0)}
                }
            }
        }
    }

}