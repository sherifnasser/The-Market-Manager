package com.sherifnasser.themarketmanager.ui.fragment.store

import android.os.Bundle
import android.view.*
import android.view.inputmethod.EditorInfo
import android.widget.EditText
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sherifnasser.themarketmanager.*
import com.sherifnasser.themarketmanager.databinding.FragmentDialogAddProductBinding
import com.sherifnasser.themarketmanager.ui.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class AddProductDialogFragment:DialogFragment(){

    private var binding:FragmentDialogAddProductBinding?=null
    private val productViewModel:ProductViewModel by activityViewModels()
    private lateinit var productNameEditText:EditText
    private lateinit var productPriceEditText:EditText
    private lateinit var productAvailableQuantityEditText:EditText

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.DialogFragmentStyle)
    }

    override fun onStart(){
        super.onStart()
        dialog?.window?.apply{
            val matchParent=ViewGroup.LayoutParams.MATCH_PARENT
            setLayout(matchParent,matchParent)
            setWindowAnimations(R.style.AppTheme_Slide)
            setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_VISIBLE)
        }
    }

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?{
        binding=FragmentDialogAddProductBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView(){
        binding=null
        super.onDestroyView()
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        productNameEditText=binding!!.productNameTextInputLayout.editText!!
        productPriceEditText=binding!!.productPriceTextInputLayout.editText!!
        productAvailableQuantityEditText=binding!!.productAvailableQuantityTextInputLayout.editText!!
        setupToolbar()
        setupViewModelWithTextFields() // This will save the state of the fields.
        showKeyboard(binding!!.productNameTextInputLayout.editText!!)
    }

    private fun setupToolbar(){
        binding!!.toolbar.apply{
            setNavigationOnClickListener{
                hideKeyboard()
                // If any field is not empty show a dialog, otherwise dismiss this dialog fragment.
                if(productNameEditText.text.isNotEmpty()||productPriceEditText.text.isNotEmpty()
                    ||productAvailableQuantityEditText.text.isNotEmpty())
                    MaterialAlertDialogBuilder(context)
                        .setMessage(getString(R.string.discard_changes))
                        .setPositiveButton(getString(R.string.discard)){_,_->dismiss()}
                        .setNegativeButton(getString(R.string.cancel)){dialog,_->dialog.dismiss()}
                        .show()
                else dismiss()
            }
            inflateMenu(R.menu.fragment_dialog_add_product_menu)
            setOnMenuItemClickListener{saveProduct();true}
        }
    }

    // Save the state of the fields with view model.
    private fun setupViewModelWithTextFields(){
        val product=productViewModel.productInfo.value!!

        // Setup product name
       productNameEditText.apply{
            setText(product.name)
            // Change the product name when text changed.
            doOnTextChanged{text,_,_,_->
                product.name=text.toString().trim()
            }
        }

        // Setup product price
        productPriceEditText.apply{
            // If 0 (when restoring state) not to set the value.
            if(product.price!=0.0)setText(product.price.toString())

            // Change the product price when text changed.
            doOnTextChanged{text,_,_,_->
                try{
                    product.price=text.toString().toDouble()
                }catch(e:Exception){
                    product.price=0.0
                }
            }
        }

        // Setup product qty.
        productAvailableQuantityEditText.apply{
            // If 0 (when restoring state) not to set the value.
            if(product.availableQuantity!=0)setText(product.availableQuantity.toString())

            // Change the product qty when text changed.
            doOnTextChanged{text,_,_,_->
                try{
                    product.availableQuantity=text.toString().toInt()
                }catch(e:Exception){
                    product.availableQuantity=0
                }
            }

            // When user the hit Enter key
            setOnEditorActionListener{_,actionId,_->
                var handled=false
                if(actionId==EditorInfo.IME_ACTION_DONE){
                    saveProduct()
                    handled=true
                }
                handled
            }
        }
    }

    // To save the product to database.
    private fun saveProduct(){
        hideKeyboard()
        clearPreviousFieldsErrors()
        // If there no errors with values
        if(areFieldsValid()){
            /*
            Show dialog to make sure that product data are valid because the user cannot update the product name later.
            The product name will not be changed.
            Also the product cannot be deleted because this may affect some orders details that have that product.
             */

            /******************************/

            /*** I know that dialog message and title texts may be awful, but I spent a lot of time deciding what to show & how the dialog should appear.
                But the customer can decide what to display. ***/
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_product_information)
                .setMessage(R.string.add_product_confirm_dialog_message)
                .setNeutralButton(R.string.edit){dialog,_->dialog.dismiss()}
                .setPositiveButton(R.string.save_anyway){dialog,_->
                    productViewModel.apply{
                        insert(productInfo.value!!)
                    }
                    dialog.dismiss()
                    dismiss()
                }.show()
        }
    }

    // To clear any error
    private fun clearPreviousFieldsErrors(){
        binding!!.productNameTextInputLayout.clearError()
        binding!!.productPriceTextInputLayout.clearError()
        binding!!.productAvailableQuantityTextInputLayout.clearError()
    }

    /*
    Returning variables with the values from these methods instead of returning the methods with &&.
    This because these methods show an error if there is one.
    So if calling the methods with && when the first method return false, this method will return false and won't call the other methods.
    */
    private fun areFieldsValid():Boolean{
        val isNameFieldValid=isNameFieldValid()
        val isPriceFieldValid=isPriceFieldValid()
        val isQtyFieldValid=isQtyFieldValid()
        return isNameFieldValid&&isPriceFieldValid&&isQtyFieldValid
    }

    // Validate the product name field.
    private fun isNameFieldValid():Boolean{
        if(productNameEditText.text.isEmpty()){
            binding!!.productNameTextInputLayout.showError(getString(R.string.error_field_cannot_be_empty))
            return false
        }
        return true
    }

    // Validate the product price field.
    private fun isPriceFieldValid():Boolean{
        productPriceEditText.text.toString().apply{
            when{
                isEmpty()->{
                    binding!!.productPriceTextInputLayout.showError(getString(R.string.error_field_cannot_be_empty))
                    return@isPriceFieldValid false
                }
                toDouble()==0.0->{
                    binding!!.productPriceTextInputLayout.showError(getString(R.string.error_price_cannot_be_zero))
                    return@isPriceFieldValid false
                }
            }
        }
        return true
    }

    // Validate the product available quantity field.
    private fun isQtyFieldValid():Boolean{
        productAvailableQuantityEditText.text.toString().apply{
            when{
                isEmpty()->{
                    binding!!.productAvailableQuantityTextInputLayout.showError(getString(R.string.error_field_cannot_be_empty))
                    return@isQtyFieldValid false
                }
                toInt()<0->{
                    binding!!.productAvailableQuantityTextInputLayout.showError(getString(R.string.error_quantity_cannot_be_negative))
                    return@isQtyFieldValid false
                }
            }
        }
        return true
    }

    // Hide the keyboard when it stops
    override fun onStop(){
        hideKeyboard()
        super.onStop()
    }

    private fun hideKeyboard()=hideKeyboard(requireView())
}