package com.sherifnasser.themarketmanager.database.dao

import androidx.paging.DataSource
import androidx.room.*
import com.sherifnasser.themarketmanager.database.model.Product

// The Data access object of products.
@Dao
interface ProductDao{
    @Insert
    fun insert(product:Product)

    @Update
    fun update(product:Product)

    @Query("SELECT * FROM products_table ORDER BY id DESC")
    fun getAllProducts():DataSource.Factory<Int,Product>

    @Query("""
        SELECT products_table.*
        FROM products_fts
        JOIN products_table
        ON (products_fts.rowId=id)
        WHERE products_fts MATCH '*'||:nameQuery||'*'
        ORDER BY id DESC""")
    fun getProductsByName(nameQuery:String):DataSource.Factory<Int,Product>
}