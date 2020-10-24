package com.sherifnasser.themarketmanager.ui.fragment.orders

import android.graphics.Color
import android.graphics.drawable.ColorDrawable
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.database.model.SoldProduct
import com.sherifnasser.themarketmanager.databinding.FragmentDialogAddOrderProductBinding
import com.sherifnasser.themarketmanager.hideKeyboard
import com.sherifnasser.themarketmanager.ui.adapter.AddOrderSelectedProductRecyclerViewAdapter
import com.sherifnasser.themarketmanager.ui.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class AddOrderProductDialogFragment:DialogFragment(){

    private var binding:FragmentDialogAddOrderProductBinding?=null
    private val productViewModel by viewModels<ProductViewModel>()
    @Inject lateinit var mAdapter:AddOrderSelectedProductRecyclerViewAdapter

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.DialogFragmentStyle)
    }

    override fun onStart(){
        super.onStart()
        val wrapContent=ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(wrapContent,wrapContent)
    }

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View{
        dialog?.apply{
            window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
            setCanceledOnTouchOutside(true)
        }
        binding=FragmentDialogAddOrderProductBinding.inflate(layoutInflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView(){
        binding=null
        super.onDestroyView()
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)

        setupRecyclerView()

        binding!!.searchProductTextInputLayout.editText!!.doOnTextChanged{query,_,_,_ ->
            productViewModel.getProductsByName(query.toString())
        }
    }


    private fun setupRecyclerView(){
        // When user click any item in the recyclerView
        mAdapter.setOnItemClickListener{product->
            if(product.availableQuantity==0)
                Toast.makeText(context,getString(R.string.the_product_is_not_available),Toast.LENGTH_SHORT).show()
            else{
                val soldProduct=SoldProduct(
                    productId=product.productId,
                    name=product.name,
                    price=product.price,
                    availableQuantity=product.availableQuantity,
                    soldQuantity=0
                )
                val action=AddOrderProductDialogFragmentDirections
                    .actionAddOrderProductDialogFragmentToProductSoldQuantityDialogFragment(selectedProduct=soldProduct)
                findNavController().navigate(action)
            }
        }

        // RecyclerView
        binding!!.selectedProductRecyclerView.apply{
            adapter=mAdapter
            layoutManager=LinearLayoutManager(context)
        }

        productViewModel.products.observe(viewLifecycleOwner){list->
            // Submit the new list and display it.
            mAdapter.submitList(list){
                // Scroll to first item when list submitted
                binding!!.selectedProductRecyclerView.let{
                    it.post{it.scrollToPosition(0)}
                }
            }
        }
    }

    override fun onStop(){
        hideKeyboard()
        super.onStop()
    }

    private fun hideKeyboard()=hideKeyboard(requireView())
}