package com.sherifnasser.themarketmanager.database.dao

import androidx.room.*
import com.sherifnasser.themarketmanager.database.model.OrderProductCrossRef

@Dao
interface OrderProductCrossRefDao{

    @Insert
    suspend fun insertAll(orderProductCrossRefs:List<OrderProductCrossRef>)

    @Update
    suspend fun updateAll(orderProductCrossRefs:List<OrderProductCrossRef>)

    @Delete
    suspend fun deleteAll(orderProductCrossRefs:List<OrderProductCrossRef>)

    @Query("""
        DELETE FROM orders_products_cross_ref_table
        WHERE orderId=:orderId
        """)
    suspend fun deleteAllWhere(orderId:Int)

    @Query(
        """
        SELECT soldQuantity FROM orders_products_cross_ref_table
        WHERE orderId=:orderId
        AND productId=:productId
        """
    )
    suspend fun getQuantityOfSoldProduct(orderId:Int,productId:Int):Int
}