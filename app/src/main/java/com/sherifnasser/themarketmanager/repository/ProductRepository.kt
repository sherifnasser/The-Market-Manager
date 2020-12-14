package com.sherifnasser.themarketmanager.repository

import com.sherifnasser.themarketmanager.database.dao.ProductDao
import com.sherifnasser.themarketmanager.database.model.Product
import javax.inject.Inject

/*
productDao: the Dao of products table
*/
class ProductRepository
@Inject
constructor(private val productDao:ProductDao){

    /*
    _allProducts: the all products existing in the database (it's liveData of pagedList of products to achieve paging).

    _allUnavailableProducts: the all unavailable products existing in the database (it's liveData of pagedList of products to achieve paging).
     */
    private val _allProducts by lazy{productDao.getAllProducts()}
    
    private val _allUnavailableProducts by lazy{productDao.getAllUnavailableProducts()}
    
    // Insert new product to database.
    suspend fun insert(product:Product)=productDao.insert(product)

    // Update existing product.
    suspend fun update(product:Product)=productDao.update(product)

    // Search products by productName.
    fun getProductByName(nameQuery:String)=productDao.getProductByName(nameQuery)

    // Getter for _products (it returns liveData of pagedList of products to achieve paging).
    val allProducts get()=_allProducts
    
    // Getter for _allUnavailableProducts (it returns liveData of pagedList of products to achieve paging).
    val allUnavailableProducts get()=_allUnavailableProducts
    
}
