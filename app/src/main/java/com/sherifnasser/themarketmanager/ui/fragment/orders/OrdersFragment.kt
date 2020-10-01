package com.sherifnasser.themarketmanager.ui.fragment.orders

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.sherifnasser.themarketmanager.R

class OrdersFragment:Fragment(){

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?{
        return inflater.inflate(R.layout.fragment_orders,container,false)
    }
}