package com.sherifnasser.themarketmanager.ui.fragment.store

import android.app.Dialog
import android.os.Bundle
import android.text.InputType
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.EditorInfo
import android.widget.Toast
import androidx.core.view.ViewCompat
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.navArgs
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetDialogFragment
import com.google.android.material.shape.MaterialShapeDrawable
import com.google.android.material.shape.ShapeAppearanceModel
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.databinding.FragmentBottomSheetDialogUpdateProductInfoBinding
import com.sherifnasser.themarketmanager.notifyUi
import com.sherifnasser.themarketmanager.ui.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class UpdateProductInfoBottomSheetDialogFragment:BottomSheetDialogFragment(){

    private var binding:FragmentBottomSheetDialogUpdateProductInfoBinding?=null
    private val productViewModel:ProductViewModel by activityViewModels()
    private val args: UpdateProductInfoBottomSheetDialogFragmentArgs by navArgs()

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?{
        binding=FragmentBottomSheetDialogUpdateProductInfoBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView(){
        binding=null
        super.onDestroyView()
    }

    override fun onCreateDialog(savedInstanceState:Bundle?):Dialog{
        val dialog=super.onCreateDialog(savedInstanceState)
        dialog.setOnShowListener{
            val bottomSheet=dialog.findViewById<View>(com.google.android.material.R.id.design_bottom_sheet)

            BottomSheetBehavior.from(bottomSheet).apply{
                state=BottomSheetBehavior.STATE_EXPANDED
                /* I think that when dismissing the dialog it will be nice like when dismiss the dialog of changing name in Whatsapp.
                So I didn't set the peekHeight to 0. */
                addBottomSheetCallback(object:BottomSheetBehavior.BottomSheetCallback(){
                    override fun onSlide(bottomSheet:View,slideOffset:Float){}
                    override fun onStateChanged(bottomSheet:View,newState:Int){
                        if(newState==BottomSheetBehavior.STATE_COLLAPSED)dismiss()
                    }
                })
            }
            // Add rounded corners to dialog
            val currentMaterialShapeDrawable=bottomSheet.background as MaterialShapeDrawable
            val shapeAppearanceModel=ShapeAppearanceModel
                .builder(requireContext(),0,R.style.BottomSheetDialogCornerStyle)
                .build()
            val newMaterialShapeDrawable=MaterialShapeDrawable(shapeAppearanceModel)
            newMaterialShapeDrawable.fillColor=currentMaterialShapeDrawable.fillColor
            ViewCompat.setBackground(bottomSheet,newMaterialShapeDrawable)
        }
        return dialog
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        setupTitle()
        setupEditText()
        binding!!.saveBtn.setOnClickListener{updateProduct()}
        binding!!.cancelBtn.setOnClickListener{dismiss()}
    }

    private fun setupTitle(){
        val title=getString(args.titleResId)
        binding!!.titleTextView.text=getString(R.string.enter_s,title)
    }

    private fun setupEditText(){
        val product=productViewModel.productInfo.value!!
        with(binding!!.productInfoEditText){
            // Setup the text & input type
            when(args.titleResId){
                R.string.price->{
                    setText(product.price.toString())
                    inputType=InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_DECIMAL
                }
                R.string.available_quantity->{
                    setText(product.availableQuantity.toString())
                    inputType=InputType.TYPE_CLASS_NUMBER or InputType.TYPE_NUMBER_FLAG_SIGNED
                }
            }
            // Select all text
            selectAll()
            // When user hit the Enter key
            setOnEditorActionListener{_,actionId,_->
                var handled=false
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    updateProduct()
                    handled=true
                }
                handled
            }
        }
    }

    private fun updateProduct(){
        val product=productViewModel.productInfo.value!!
        with(binding!!.productInfoEditText.text.toString()){
            when{
                isEmpty()->{
                    toast(R.string.error_field_cannot_be_empty)
                    return
                }
                else->{
                    when(args.titleResId){
                        R.string.price->{
                            // validate price
                            if(toDouble()==0.0){
                                toast(R.string.error_price_cannot_be_zero)
                                return
                            }
                            product.price=toDouble()
                        }
                        R.string.available_quantity->{
                            // validate available quantity
                            if(toInt()<0){
                                toast(R.string.error_quantity_cannot_be_negative)
                                return
                            }
                            product.availableQuantity=toInt()
                        }
                    }
                    productViewModel.productInfo.notifyUi()
                    productViewModel.update(product)
                    dismiss()
                }
            }
        }
    }

    private fun toast(messageResId:Int)=Toast.makeText(context,messageResId,Toast.LENGTH_SHORT).show()

}