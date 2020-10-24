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
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.databinding.FragmentDialogProductSoldQuantityBinding
import com.sherifnasser.themarketmanager.hideKeyboard
import com.sherifnasser.themarketmanager.notifyUi
import com.sherifnasser.themarketmanager.showKeyboard
import com.sherifnasser.themarketmanager.ui.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class ProductSoldQuantityDialogFragment:DialogFragment(){

    private val args by navArgs<ProductSoldQuantityDialogFragmentArgs>()
    private val selectedProduct by lazy {args.selectedProduct}
    private var binding:FragmentDialogProductSoldQuantityBinding?=null
    private val orderViewModel by activityViewModels<OrderViewModel>()

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.DialogFragmentStyle)
    }

    override fun onStart() {
        super.onStart()
        val wrapContent=ViewGroup.LayoutParams.WRAP_CONTENT
        dialog?.window?.setLayout(wrapContent,wrapContent)
    }

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?{
        dialog?.window?.setBackgroundDrawable(ColorDrawable(Color.TRANSPARENT))
        binding=FragmentDialogProductSoldQuantityBinding.inflate(layoutInflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView(){
        binding=null
        super.onDestroyView()
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        with(selectedProduct){
            with(binding!!){
                selectedProductNameTextView.text=name
                selectedProductPriceTextView.text=getString(R.string.price_colon,price.toString())
                selectedProductAvailableQuantityTextView.text=getString(R.string.available_colon,availableQuantity.toString())
            }
        }
        with(binding!!.soldQtyEditText){
            showKeyboard(this)
            doOnTextChanged{text,_,_,_->
                binding!!.okBtn.isEnabled=text!!.isNotEmpty()&&text.toString().toInt()!=0
            }
            if(selectedProduct.soldQuantity!=0){
                setText(selectedProduct.soldQuantity.toString())
                selectAll()
            }
        }
        binding!!.cancelBtn.setOnClickListener{hideKeyboard();dismiss()}
        binding!!.okBtn.setOnClickListener{
            hideKeyboard()
            val soldQuantity=binding!!.soldQtyEditText.text.toString().toInt()
            if(selectedProduct.availableQuantity-soldQuantity<0)
                Toast.makeText(context,R.string.you_must_enter_valid_quantity,Toast.LENGTH_SHORT).show()
            else{
                selectedProduct.soldQuantity=soldQuantity
                with(orderViewModel.orderInfo){
                    value!!.addInSoldProducts(selectedProduct)
                    notifyUi()
                }
                dismiss()
            }
        }
    }

    override fun onStop(){
        hideKeyboard()
        super.onStop()
    }

    private fun hideKeyboard()=hideKeyboard(requireView())
}