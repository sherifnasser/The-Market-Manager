package com.sherifnasser.themarketmanager.ui.fragment.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.snackbar.Snackbar
import com.sherifnasser.themarketmanager.R
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

    private val itemTouchHelper by lazy{
        ItemTouchHelper(
            object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(recyclerView:RecyclerView,viewHolder:RecyclerView.ViewHolder,target:RecyclerView.ViewHolder)=false

                override fun onSwiped(viewHolder:RecyclerView.ViewHolder,direction:Int){
                    val selectedOrderPosition=viewHolder.adapterPosition
                    orderViewModel.setOrderInfo(orderViewModel.allOrders.value!![selectedOrderPosition]!!)
                    orderViewModel.deleteOrderFromUiAt(selectedOrderPosition)

                    Snackbar.make(binding!!.addOrderFab,getString(R.string.order_is_deleted),Snackbar.LENGTH_LONG)
                        .addCallback(object:Snackbar.Callback(){
                            override fun onDismissed(transientBottomBar:Snackbar?,event:Int){
                                orderViewModel.delete(orderViewModel.orderInfo.value!!)
                            }
                        })
                        .setAction(getString(R.string.undo)){
                            orderViewModel.undoOrderInUiAt(selectedOrderPosition)
                        }
                        .show()
                }
            }
        )
    }

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

        binding!!.addOrderFab.setOnClickListener{
            orderViewModel.setOrderInfoToNew()
            findNavController().navigate(R.id.action_nav_orders_to_addOrderDialogFragment)
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
            itemTouchHelper.attachToRecyclerView(this)
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