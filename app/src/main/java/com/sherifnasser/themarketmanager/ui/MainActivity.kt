package com.sherifnasser.themarketmanager.ui

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.TextView
import androidx.core.view.GravityCompat
import androidx.navigation.NavController
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity:AppCompatActivity(){
    private lateinit var binding:ActivityMainBinding
    private lateinit var navController:NavController
    private lateinit var appBarConfig:AppBarConfiguration
    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Setup drawer with navController
        navController=findNavController(R.id.nav_host_fragment)
        val topLevelDestinationIds=setOf(
            R.id.nav_dashboard,
            R.id.nav_store,
            R.id.nav_orders
        )
        appBarConfig=AppBarConfiguration(topLevelDestinationIds,binding.drawerLayout)
        setupActionBarWithNavController(navController,appBarConfig)
        binding.navView.setupWithNavController(navController)

        // Setup search view
        binding.searchView.setupWithNavController(navController,topLevelDestinationIds,R.id.nav_store)
    }

    override fun onSupportNavigateUp():Boolean=navController.navigateUp(appBarConfig)||super.onSupportNavigateUp()

    /*
    if the drawer was opened close it.
    Or if the search view was opened every where close it
    Otherwise execute the super.
     */
    override fun onBackPressed()=
        when{
            binding.drawerLayout.isDrawerOpen(GravityCompat.START)->binding.drawerLayout.closeDrawer(GravityCompat.START)
            binding.searchView.shouldClose->binding.searchView.closeSearchWithAnimation()
            else->super.onBackPressed()
        }
}