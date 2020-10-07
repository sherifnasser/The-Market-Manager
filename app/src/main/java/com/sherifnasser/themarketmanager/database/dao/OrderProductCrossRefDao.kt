package com.sherifnasser.themarketmanager.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.sherifnasser.themarketmanager.database.model.OrderProductCrossRef

@Dao
interface OrderProductCrossRefDao{

    @Insert
    suspend fun insertAll(orderProductCrossRefs:List<OrderProductCrossRef>)

    @Query(
        """
        SELECT soldQuantity FROM orders_products_cross_ref_table
        WHERE orderId=:orderId
        AND productId=:productId
        """
    )
    suspend fun getQuantityOfSoldProduct(orderId:Int,productId:Int):Int
}