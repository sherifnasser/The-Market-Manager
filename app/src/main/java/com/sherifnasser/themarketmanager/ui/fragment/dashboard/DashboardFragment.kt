package com.sherifnasser.themarketmanager.ui.fragment.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.sherifnasser.themarketmanager.R

class DashboardFragment:Fragment(){

    override fun onCreateView(inflater:LayoutInflater,container:ViewGroup?,savedInstanceState:Bundle?):View?=
        inflater.inflate(R.layout.fragment_dashboard,container,false)
}