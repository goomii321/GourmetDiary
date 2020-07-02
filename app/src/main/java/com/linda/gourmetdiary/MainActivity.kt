package com.linda.gourmetdiary

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.Navigation
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.google.android.material.navigation.NavigationView
import com.linda.gourmetdiary.databinding.ActivityMainBinding
import com.linda.gourmetdiary.util.CurrentFragmentType
import kotlinx.android.synthetic.main.activity_main.*

class MainActivity : AppCompatActivity() {

    private lateinit var binding : ActivityMainBinding
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val onNavigationItemSelectedListener = NavigationView.OnNavigationItemSelectedListener { menuItem ->
        when(menuItem.itemId){
            R.id.homeFragment -> {
                findNavController(R.id.myNavHostFragment).navigate(R.id.navigate_to_home)
                return@OnNavigationItemSelectedListener true
            }
            R.id.diarysFragment -> {
                findNavController(R.id.myNavHostFragment).navigate(R.id.navigate_to_diarys)
                return@OnNavigationItemSelectedListener  true
            }
            R.id.storesFragment -> {
                findNavController(R.id.myNavHostFragment).navigate(R.id.navigate_to_stores)
                return@OnNavigationItemSelectedListener  true
            }
            R.id.profileFragment -> {
                findNavController(R.id.myNavHostFragment).navigate(R.id.navigate_to_profile)
                return@OnNavigationItemSelectedListener  true
            }
        }
        false
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding= DataBindingUtil.setContentView(this,R.layout.activity_main)
        binding.lifecycleOwner = this

        binding.drawerNavView.setNavigationItemSelectedListener(onNavigationItemSelectedListener)
        setupDrawer()
    }

    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener { navController: NavController, _: NavDestination, _: Bundle? ->
//            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
//                R.id.homeFragment -> CurrentFragmentType.HOME
//                else -> viewModel.currentFragmentType.value
//            }
        }
    }

    private fun setupDrawer() {
        val navController = this.findNavController(R.id.myNavHostFragment)
        setSupportActionBar(toolbar)

        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.drawerNavView, navController)

        actionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ){
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
        }.apply {
            binding.drawerLayout.addDrawerListener(this)
            syncState()
        }

//        bindingNavHeader.viewModel = viewModel

        // Observe current drawer toggle to set the navigation icon and behavior
//        viewModel.currentDrawerToggleType.observe(this, Observer { type ->
//
//            actionBarDrawerToggle?.isDrawerIndicatorEnabled = type.indicatorEnabled
//            supportActionBar?.setDisplayHomeAsUpEnabled(!type.indicatorEnabled)
//            binding.toolbar.setNavigationIcon(
//                when (type) {
//                    DrawerToggleType.BACK -> R.drawable.toolbar_back
//                    else -> R.drawable.toolbar_menu
//                }
//            )
//            actionBarDrawerToggle?.setToolbarNavigationClickListener {
//                when (type) {
//                    DrawerToggleType.BACK -> onBackPressed()
//                    else -> {
//                    }
//                }
//            }
//        })
    }
}
