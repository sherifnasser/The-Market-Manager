package com.sherifnasser.themarketmanager.ui.fragment.orders

import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.database.model.SoldProduct
import com.sherifnasser.themarketmanager.databinding.FragmentDialogAddOrderBinding
import com.sherifnasser.themarketmanager.notifyUi
import com.sherifnasser.themarketmanager.ui.adapter.OrderProductsRecyclerViewAdapter
import com.sherifnasser.themarketmanager.ui.fragment.OnBackPressedFragmentCallback
import com.sherifnasser.themarketmanager.ui.viewmodel.OrderViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.text.DateFormat
import javax.inject.Inject

@AndroidEntryPoint
class AddOrderDialogFragment():DialogFragment(),OnBackPressedFragmentCallback{

    private var binding:FragmentDialogAddOrderBinding?=null
    private val orderViewModel by activityViewModels<OrderViewModel>()
    @Inject lateinit var dateFormat:DateFormat
    @Inject lateinit var mAdapter:OrderProductsRecyclerViewAdapter

    private val itemTouchHelper by lazy{
        ItemTouchHelper(
            object:ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT or ItemTouchHelper.RIGHT){
                override fun onMove(recyclerView:RecyclerView,viewHolder:RecyclerView.ViewHolder,target:RecyclerView.ViewHolder)=false

                override fun onSwiped(viewHolder:RecyclerView.ViewHolder,direction:Int){
                    with(orderViewModel.orderInfo){
                        value!!.removeFromSoldProductAt(viewHolder.adapterPosition)
                        notifyUi()
                    }
                }
            }
        )
    }

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        setStyle(STYLE_NORMAL,R.style.FullScreenDialogFragmentStyle)
    }

    override fun onStart(){
        super.onStart()
        dialog?.window?.apply{
            val matchParent=ViewGroup.LayoutParams.MATCH_PARENT
            setLayout(matchParent,matchParent)
            setWindowAnimations(R.style.AppTheme_Slide)
        }
    }

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?{
        binding=FragmentDialogAddOrderBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView(){
        binding=null
        super.onDestroyView()
    }

    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        setupToolbar()
        setupOrderInfo()
        setupRecyclerView()
        setupBottomAppBar()
    }

    private fun setupToolbar(){
        binding!!.toolbar.apply{
            setNavigationOnClickListener{showDiscardChangesDialog()}
            inflateMenu(R.menu.fragment_dialog_add_order_menu)
            setOnMenuItemClickListener{saveOrder();true}
        }
    }

    private fun setupOrderInfo(){
        with(orderViewModel.orderInfo){
            value!!.let{order->
                // setup order id text view
                binding!!.orderIdTextView.text=getString(R.string.id_colon,order.orderId.toString())
                //setup order date text view
                val orderDate=dateFormat.format(order.date)
                binding!!.orderDateTextView.text=getString(R.string.date_colon,orderDate)
            }

            observe(viewLifecycleOwner){order->
                val soldProductsList=order.soldProducts!!.toList()
                // Show "Tab + to add products" text view when list is empty
                binding!!.tabPlusTextView.visibility=if(soldProductsList.isEmpty())View.VISIBLE else View.GONE
                // Submit the new list and display it.
                mAdapter.submitList(soldProductsList){
                    // Scroll to first item when list submitted
                    binding!!.orderProductsRecyclerView.let{
                        it.post{it.scrollToPosition(soldProductsList.lastIndex)}
                    }
                }
                // Update total text view
                binding!!.orderTotalTextView.text=order.total.toString()
            }
        }
    }

    private fun setupRecyclerView(){
        mAdapter.setOnItemClickListener{
            val action=AddOrderDialogFragmentDirections
                .actionAddOrderDialogFragmentToProductSoldQuantityDialogFragment(selectedProduct=(it as SoldProduct).copy())
            findNavController().navigate(action)
        }


        with(binding!!.orderProductsRecyclerView){
            layoutManager=LinearLayoutManager(context).apply{reverseLayout=true;stackFromEnd=true}
            adapter=mAdapter
            itemTouchHelper.attachToRecyclerView(this)
        }
    }

    private fun setupBottomAppBar(){
        binding!!.bottomAppBar.setOnMenuItemClickListener{
            // navigate to add products to the order
            findNavController().navigate(R.id.action_addOrderDialogFragment_to_addOrderProductDialogFragment)
            true
        }
    }

    private fun saveOrder(){
        if(orderViewModel.orderInfo.value!!.soldProducts!!.isEmpty())
            Toast.makeText(requireContext(),R.string.order_sold_products_cannot_be_empty,Toast.LENGTH_LONG).show()
        else{
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(R.string.confirm_order_information)
                .setMessage(R.string.order_info_confirm_dialog_message)
                .setPositiveButton(R.string.save_anyway){dialog,_->
                    with(orderViewModel){insert(orderInfo.value!!)}
                    dialog.dismiss()
                    dismiss()
                }
                .setNegativeButton(R.string.edit){dialog,_->dialog.dismiss()}
                .show()
        }
    }

    private val TAG="AddOrderDialogFragment"

    private fun showDiscardChangesDialog(onFinishCall:()->Unit={}){
        if(orderViewModel.areOrderInfoSoldProductsChanged){
            Log.d(TAG,"showDiscardChangesDialog: ${orderViewModel._orderInfoOldSoldProducts.value!!} and ${orderViewModel.orderInfo.value!!.soldProducts!!}")
            MaterialAlertDialogBuilder(requireContext())
                .setMessage(R.string.discard_changes)
                .setPositiveButton(R.string.discard){_,_->dismiss();onFinishCall.invoke()}
                .setNegativeButton(R.string.cancel){dialog,_->dialog.dismiss()}
                .show()
        }else{
            dismiss()
            onFinishCall.invoke()
        }
    }

    override fun onBackPressed(onFinishCall:()->Unit){
        showDiscardChangesDialog(onFinishCall)
    }

}