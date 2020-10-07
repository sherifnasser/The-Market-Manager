package com.sherifnasser.themarketmanager.ui.fragment.store

import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.ui.adapter.ProductsRecyclerViewAdapter
import com.sherifnasser.themarketmanager.databinding.FragmentStoreBinding
import com.sherifnasser.themarketmanager.hideKeyboard
import com.sherifnasser.themarketmanager.database.model.Product
import com.sherifnasser.themarketmanager.ui.widget.AppSearchView
import com.sherifnasser.themarketmanager.ui.viewmodel.ProductViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StoreFragment:Fragment(){

    private var binding:FragmentStoreBinding?=null
    private val productViewModel:ProductViewModel by activityViewModels()
    @Inject lateinit var mAdapter:ProductsRecyclerViewAdapter

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?{
        binding=FragmentStoreBinding.inflate(inflater,container,false)
        return binding!!.root
    }

    override fun onDestroyView(){
        binding=null
        super.onDestroyView()
    }


    override fun onViewCreated(view:View,savedInstanceState:Bundle?){
        super.onViewCreated(view,savedInstanceState)
        setHasOptionsMenu(true)
        setupRecyclerView()
        binding!!.addProductFab.setOnClickListener{
            findNavController().navigate(R.id.action_nav_store_to_addProductDialogFragment)
        }
    }

    private fun setupRecyclerView(){

        // When user click any item in the recyclerView
        mAdapter.setOnItemClickListener{
            productViewModel.productInfo.value=Product(it.productId,it.name,it.price,it.availableQuantity)
            findNavController().navigate(R.id.action_nav_store_to_productInfoFragment)
        }

        // RecyclerView
        binding!!.productsRecyclerView.apply{
            adapter=mAdapter
            layoutManager=LinearLayoutManager(context)
        }

        /*
        It will show any products in recyclerView.
         */
        productViewModel.products.observe(viewLifecycleOwner){list->
            // Submit the new list and display it.
            mAdapter.submitList(list){
                // Scroll to first item when list submitted
                binding!!.productsRecyclerView.let{
                    it.post{it.scrollToPosition(0)}
                }
            }
        }
    }

    override fun onCreateOptionsMenu(menu:Menu,inflater:MenuInflater){
        inflater.inflate(R.menu.fragment_store_menu,menu)
        val searchView=requireActivity().findViewById<AppSearchView>(R.id.search_view)
        searchView?.apply{
            setupWithMenuItem(menu.findItem(R.id.search_products_menu_item))
            setOnQueryTextChangedListener{
                productViewModel.getProductsByName(it.toString())
            }
        }
        super.onCreateOptionsMenu(menu,inflater)
    }

    // To hide the keyboard if the search was opened.
    override fun onStop(){
        hideKeyboard(requireView())
        super.onStop()
    }
}