package com.sherifnasser.themarketmanager.database.dao

import androidx.lifecycle.LiveData
import androidx.paging.DataSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.sherifnasser.themarketmanager.database.model.Product

// The Data access object of products.
@Dao
interface ProductDao{
    @Insert
    suspend fun insert(product:Product)

    @Update
    suspend fun update(product:Product)

    @Update
    suspend fun updateAll(product:List<Product>)

    @Query("SELECT * FROM products_table ORDER BY productId DESC")
    fun getAllProducts():DataSource.Factory<Int,Product>

    @Query(
        """
            SELECT products_table.*
            FROM products_fts
            JOIN products_table
            ON (products_fts.rowId=productId)
            WHERE products_fts MATCH '*'||:nameQuery||'*'
            ORDER BY productId DESC
            """
    )
    fun getProductByName(nameQuery:String):DataSource.Factory<Int,Product>

    @Query(
        """
            SELECT products_table.*
            FROM products_fts
            JOIN products_table
            ON (products_fts.rowId=productId)
            WHERE products_fts MATCH 0
            ORDER BY productId DESC
            """
    )
    fun getAllUnavailableProducts():DataSource.Factory<Int,Product>

    @Query("SELECT COUNT(productId) FROM products_table WHERE availableQuantity=0")
    fun getUnavailableProductsCount():LiveData<Int>
}