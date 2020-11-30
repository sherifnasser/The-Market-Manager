package com.sherifnasser.themarketmanager.ui.fragment.orders

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.lifecycle.Observer
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sherifnasser.themarketmanager.R
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
        binding!!.addOrderFab.setOnClickListener{navigateToAddOrder()}
    }

    private fun setupRecyclerView() {
        // When user click any item in the recyclerView

        with(mAdapter){

            setOnItemClickListener{order->navigateToEditOrder(order)}

            setOnMoreOptionMenuItemClickListener{menuItemId,order->
                when(menuItemId){

                    R.id.popup_edit_order_menu_item->navigateToEditOrder(order)

                    R.id.popup_delete_order_menu_item->
                        MaterialAlertDialogBuilder(requireContext())
                            .setMessage(R.string.sure_to_delete)
                            .setPositiveButton(R.string.delete){dialog,_->
                                orderViewModel.delete(order){
                                    dialog.dismiss()
                                }
                            }
                            .setNegativeButton(R.string.cancel){dialog,_->dialog.dismiss()}
                            .show()
                }
            }
        }


        // RecyclerView
        binding!!.ordersRecyclerView.apply{
            adapter=mAdapter
            layoutManager=LinearLayoutManager(context)
        }

        /*
        It will show any orders in recyclerView.
         */
        orderViewModel.allOrders.observe(viewLifecycleOwner,Observer{list->
            // Submit the new list and display it.
            mAdapter.submitList(list){
                // Scroll to first item when list submitted
                binding!!.ordersRecyclerView.let{
                    it.post{it.scrollToPosition(0)}
                }
            }
        })
    }

    private fun navigateToAddOrder(){
        orderViewModel.setOrderInfoToNew()
        val action=OrdersFragmentDirections.actionNavOrdersToOrderInfoDialogFragment(isAddOrderRequested=true)
        findNavController().navigate(action)
    }

    private fun navigateToEditOrder(order:Order){
        orderViewModel.setOrderInfo(order){
            findNavController().navigate(R.id.action_nav_orders_to_orderInfoDialogFragment)
        }
    }
}