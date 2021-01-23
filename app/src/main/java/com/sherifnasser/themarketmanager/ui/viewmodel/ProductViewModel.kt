package com.sherifnasser.themarketmanager.ui.viewmodel

import androidx.hilt.lifecycle.ViewModelInject
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.switchMap
import androidx.lifecycle.viewModelScope
import androidx.paging.PagedList
import androidx.paging.toLiveData
import com.sherifnasser.themarketmanager.database.model.Product
import com.sherifnasser.themarketmanager.repository.ProductRepository
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.launch

class ProductViewModel
@ViewModelInject
constructor(
    private val repository:ProductRepository,
    private val ioDispatcher:CoroutineDispatcher,
    private val productNameQuery:MutableLiveData<String>,
    private val _productsFilter:MutableLiveData<Int>,
    /*
     _productInfo: the product should be displayed when user selects a product from recycler view & display its info.
     Or to handle the state of text fields when adding product.
     */
    private val _productInfo:MutableLiveData<Product>,
    /*
    pagedListConfig: The configuration of the paged list to achieve paging when displaying products.
    */
    pagedListConfig:PagedList.Config
):ViewModel(){

    private val allProducts by lazy{repository.allProducts.toLiveData(pagedListConfig)}
    
    private val _allUnavailableProducts by lazy{repository.allUnavailableProducts.toLiveData(pagedListConfig)}
    
    /*
     _products: The products should be added to the recycler view (it's liveData of pagedList of products to achieve paging).
     It may be all the products or the the result of search.
     */
    private val _products by lazy{
        productNameQuery.switchMap{query->
            if(query.isNullOrEmpty()){
                if(productsFilter==ALL_PRODUCTS_FILTER)allProducts
                else _allUnavailableProducts
            }else repository.getProductByName(query).toLiveData(pagedListConfig)
        }
    }

    // Insert new product.
    fun insert(product:Product)=
        viewModelScope.launch(ioDispatcher){repository.insert(product)}

    // Update existing product.
    fun update(product:Product)=
        viewModelScope.launch(ioDispatcher){repository.update(product)}

    // Search in products by productName.
    fun getProductsByName(nameQuery:String?){
        productNameQuery.value=nameQuery
    }

    fun setProductsFilter(productsFilter:Int){
        _productsFilter.value=productsFilter
        getProductsByName(productNameQuery.value)
    }

    // Getter for _products (it returns liveData of pagedList of products to achieve paging).
    val products get()=_products

    // Getter for _productInfo
    val productInfo get()=_productInfo

    // Getter for _productsFilter
    val productsFilter get()=_productsFilter.value!!

    // True if there is a query entered by user
    val isThereQueryInSearch get()=!productNameQuery.value.isNullOrEmpty()

    val areThereProductsInStore get()=allProducts.value!!.isNotEmpty()

    companion object{
        const val ALL_PRODUCTS_FILTER=0
        const val UNAVAILABLE_PRODUCTS_FILTER=1
    }

}
