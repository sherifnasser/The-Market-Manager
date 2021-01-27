package com.sherifnasser.themarketmanager.ui.fragment.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.database.model.Order
import com.sherifnasser.themarketmanager.databinding.FragmentDashboardBinding
import com.sherifnasser.themarketmanager.ui.viewmodel.DashboardViewModel
import com.sherifnasser.themarketmanager.ui.viewmodel.OrderViewModel
import com.sherifnasser.themarketmanager.ui.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class DashboardFragment:Fragment(){

    private var binding:FragmentDashboardBinding?=null
    private val dashboardViewModel by activityViewModels<DashboardViewModel>()
    private val productViewModel by activityViewModels<ProductViewModel>()
    private val orderViewModel by activityViewModels<OrderViewModel>()
    private var lastOrder:Order?=null

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?{
        binding=FragmentDashboardBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView(){
        binding=null
        super.onDestroyView()
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)

        // live data
        with(dashboardViewModel){
            lastOrder.observe(viewLifecycleOwner){
                this@DashboardFragment.lastOrder=it
            }
            unavailableProductsCount.observe(viewLifecycleOwner){
                binding!!.unavailableProductsTv.text=it.toString()
            }
            todayRevenue.observe(viewLifecycleOwner){
                binding!!.todayRevenueTv.text=(it?:0.0).toString()
            }
            todayOrdersDoneCount.observe(viewLifecycleOwner){
                binding!!.todayOrdersDoneCountTv.text=(it?:0).toString()
            }
        }

        // click listeners
        with(binding!!){
            addProductBtn.setOnClickListener{navigateToAddProduct()}
            addOrderBtn.setOnClickListener{navigateToAddOrder()}
            lastOrderCard.setOnClickListener{navigateToSeeLastOrder()}
            storeCard.setOnClickListener{navigateToSeeStore()}
            statisticsCard.setOnClickListener{navigateToSeeStatistics()}
        }
    }

    private fun navigateToAddProduct(){
        productViewModel.setProductInfoToNew()
        findNavController().navigate(R.id.action_nav_dashboard_to_addProductDialogFragment)
    }

    private fun navigateToAddOrder(){
        orderViewModel.setOrderInfoToNew()
        val action=DashboardFragmentDirections.actionNavDashboardToOrderInfoDialogFragment(isAddOrderRequested=true)
        findNavController().navigate(action)
    }

    private fun navigateToSeeLastOrder(){
        if(lastOrder!=null)
            orderViewModel.setOrderInfo(lastOrder!!){
                findNavController().navigate(R.id.action_nav_dashboard_to_orderInfoDialogFragment)
            }
        else Toast.makeText(requireContext(),R.string.there_are_no_orders,Toast.LENGTH_LONG).show()
    }

    private fun navigateToSeeStore()=findNavController().navigate(R.id.action_nav_dashboard_to_nav_store)

    private fun navigateToSeeStatistics()=findNavController().navigate(R.id.action_nav_dashboard_to_nav_statistics)

}