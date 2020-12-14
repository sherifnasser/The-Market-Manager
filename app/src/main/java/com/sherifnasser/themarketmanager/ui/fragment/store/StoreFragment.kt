package com.sherifnasser.themarketmanager.ui.fragment.store

import android.os.Bundle
import android.view.*
import androidx.core.view.get
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.database.model.Product
import com.sherifnasser.themarketmanager.databinding.FragmentStoreBinding
import com.sherifnasser.themarketmanager.ui.adapter.ProductsRecyclerViewAdapter
import com.sherifnasser.themarketmanager.ui.viewmodel.ProductViewModel
import com.sherifnasser.themarketmanager.ui.widget.AppSearchView
import com.sherifnasser.themarketmanager.util.hideKeyboard
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class StoreFragment:Fragment(){

    private var binding:FragmentStoreBinding?=null
    private val productViewModel:ProductViewModel by activityViewModels()
    @Inject lateinit var mAdapter:ProductsRecyclerViewAdapter

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View{
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
        setupChipGroup()
        setupRecyclerView()
        binding!!.addProductFab.setOnClickListener{
            productViewModel.productInfo.value=Product(0,"",0.0,0)
            findNavController().navigate(R.id.action_nav_store_to_addProductDialogFragment)
        }
    }


    private fun setupChipGroup(){

        binding!!.productsChipGroup.let{group->
            val lastCheckedFilter=productViewModel.productsFilter
            group.setOnCheckedChangeListener{_,checkedId->
                productViewModel.setProductsFilter(
                    if(checkedId==binding!!.allProductsChip.id)ProductViewModel.ALL_PRODUCTS_FILTER
                    else ProductViewModel.UNAVAILABLE_PRODUCTS_FILTER
                )
            }
            group.check(group[lastCheckedFilter].id)
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
            /*
            Show "There are no products" if there are no products added in the store
            Or show "No results found" if user searches for a product and there are no results
            Or show "All products are available" else (if there are no unavailable products).
            */
            with(binding!!.productsInfoTextView){
                if(list.isEmpty()){
                    visibility=View.VISIBLE
                    text=getString(
                        when{
                            !productViewModel.areThereProductsInStore->R.string.there_are_no_products
                            productViewModel.isThereQueryInSearch->R.string.no_results_found
                            else->R.string.all_products_are_available
                        }
                    )
                }
                else visibility=View.GONE
            }


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
            setupWithMenuItem(menu[0])
            setOnQueryTextChangedListener{
                productViewModel.getProductsByName(it.toString())
            }
            setSearchListener(object:AppSearchView.AppSearchListener{
                override fun onSearchOpened(){
                    binding!!.productsChipGroup.visibility=View.GONE
                }
                override fun onSearchShown()=Unit
                override fun onSearchHidden()=Unit
                override fun onSearchClosed(){
                    binding!!.productsChipGroup.visibility=View.VISIBLE
                }
            })
        }
        super.onCreateOptionsMenu(menu,inflater)
    }

    // To hide the keyboard if the search was opened.
    override fun onStop(){
        hideKeyboard(requireView())
        super.onStop()
    }
}