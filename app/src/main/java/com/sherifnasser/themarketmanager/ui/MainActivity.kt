package com.sherifnasser.themarketmanager.ui

import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.GravityCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.navigateUp
import androidx.navigation.ui.setupActionBarWithNavController
import androidx.navigation.ui.setupWithNavController
import com.sherifnasser.themarketmanager.R
import com.sherifnasser.themarketmanager.databinding.ActivityMainBinding
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity:AppCompatActivity(){
    private lateinit var binding:ActivityMainBinding
    private lateinit var navHostFragment:Fragment
    private lateinit var navController:NavController
    private lateinit var appBarConfig:AppBarConfiguration
    @Inject lateinit var topLevelDestinationIds:Set<Int>

    override fun onCreate(savedInstanceState:Bundle?){
        super.onCreate(savedInstanceState)
        binding=ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)
        setSupportActionBar(binding.toolbar)

        // Setup drawer with navController
        navHostFragment=supportFragmentManager.findFragmentById(R.id.nav_host_fragment)!!
        navController=navHostFragment.findNavController()

        appBarConfig=AppBarConfiguration(topLevelDestinationIds,binding.drawerLayout)
        setupActionBarWithNavController(navController,appBarConfig)
        binding.navView.setupWithNavController(navController)

        // Setup search view
        binding.searchView.setupWithNavController(navController,topLevelDestinationIds,R.id.nav_store)
    }

    override fun onSupportNavigateUp():Boolean=navController.navigateUp(appBarConfig)||super.onSupportNavigateUp()

    override fun onBackPressed()=
        when{
            binding.drawerLayout.isDrawerOpen(GravityCompat.START)->binding.drawerLayout.closeDrawer(GravityCompat.START)
            binding.searchView.shouldClose->binding.searchView.closeSearchWithAnimation()
            else->super.onBackPressed()
        }

}