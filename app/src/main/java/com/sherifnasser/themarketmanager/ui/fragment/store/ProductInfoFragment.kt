package com.sherifnasser.themarketmanager.ui.fragment.store

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.databinding.FragmentProductInfoBinding
import com.sherifnasser.themarketmanager.ui.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductInfoFragment:Fragment(){

    private var binding:FragmentProductInfoBinding?=null
    private val productViewModel:ProductViewModel by activityViewModels()

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?{
        binding=FragmentProductInfoBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView() {
        binding=null
        super.onDestroyView()
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        setupViewModelWithTextViews()

        with(binding!!){

            productPriceCardView.setOnClickListener{
                showUpdateProductInfoBottomSheetDialog(R.string.price)
            }

            productAvailableQuantityCardView.setOnClickListener{
                showUpdateProductInfoBottomSheetDialog(R.string.available_quantity)
            }

        }
    }

    private fun setupViewModelWithTextViews(){
        productViewModel.productInfo.observe(viewLifecycleOwner){product->
            with(binding!!){
                productNameTextView.text=product.name
                productPriceTextView.text=product.price.toString()
                productAvailableQuantityTextView.text=product.availableQuantity.toString()
            }
        }
    }

    private fun showUpdateProductInfoBottomSheetDialog(titleResId:Int){
        val action=ProductInfoFragmentDirections.actionProductInfoFragmentToUpdateProductInfoBottomSheetDialogFragment(titleResId)
        findNavController().navigate(action)
    }
}