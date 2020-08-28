package com.linda.gourmetdiary

import android.os.Bundle
import android.view.View
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.ActionBarDrawerToggle
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.NavController
import androidx.navigation.NavDestination
import androidx.navigation.findNavController
import androidx.navigation.ui.AppBarConfiguration
import androidx.navigation.ui.NavigationUI
import com.linda.gourmetdiary.ext.getVmFactory
import com.bumptech.glide.Glide
import com.google.android.material.navigation.NavigationView
import com.google.firebase.auth.FirebaseAuth
import com.linda.gourmetdiary.databinding.ActivityMainBinding
import com.linda.gourmetdiary.util.CurrentFragmentType
import com.linda.gourmetdiary.util.UserManager
import kotlinx.android.synthetic.main.activity_main.*


class MainActivity : AppCompatActivity() {

    val viewModel by viewModels<MainViewModel> { getVmFactory() }

    private lateinit var binding: ActivityMainBinding
    private var actionBarDrawerToggle: ActionBarDrawerToggle? = null
    private lateinit var appBarConfiguration: AppBarConfiguration

    private val onNavigationItemSelectedListener =
        NavigationView.OnNavigationItemSelectedListener { menuItem ->
            when (menuItem.itemId) {
                R.id.homeFragment -> {
                    findNavController(R.id.myNavHostFragment).navigate(R.id.navigate_to_home)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.diariesFragment -> {
                    findNavController(R.id.myNavHostFragment).navigate(R.id.navigate_to_diaries)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.storesFragment -> {
                    findNavController(R.id.myNavHostFragment).navigate(R.id.navigate_to_stores)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.profileFragment -> {
                    findNavController(R.id.myNavHostFragment).navigate(R.id.navigate_to_profile)
                    return@OnNavigationItemSelectedListener true
                }
                R.id.logOutDialog -> {
                    findNavController(R.id.myNavHostFragment).navigate(R.id.navigate_to_log_Out)
                    return@OnNavigationItemSelectedListener true
                }
            }
            false
        }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.lifecycleOwner = this
        binding.viewModel = viewModel

        val navigationView = findViewById<View>(R.id.drawerNavView) as NavigationView
        val headerLayout = navigationView.inflateHeaderView(R.layout.nav_header_main)
        var ivHeaderPhoto: ImageView = headerLayout.findViewById(R.id.user_image)
        var userName: TextView = headerLayout.findViewById(R.id.user_name)

        binding.drawerNavView.setNavigationItemSelectedListener(onNavigationItemSelectedListener)

        UserManager.userData.userName = FirebaseAuth.getInstance().currentUser?.displayName
        UserManager.userData.userPhoto = FirebaseAuth.getInstance().currentUser?.photoUrl.toString()

        userName.text = UserManager.userData.userName
        Glide.with(navigationView).load(UserManager.userData.userPhoto).into(ivHeaderPhoto)
//        Log.d("checkDrawer","name = ${userName.text}")

        setupDrawer()
        setupNavController()
    }

    private fun setupNavController() {
        findNavController(R.id.myNavHostFragment).addOnDestinationChangedListener {
                navController: NavController, _: NavDestination, _: Bundle? ->
            viewModel.currentFragmentType.value = when (navController.currentDestination?.id) {
                R.id.homeFragment -> CurrentFragmentType.HOME
                R.id.diariesFragment -> CurrentFragmentType.DIARY
                R.id.storesFragment -> CurrentFragmentType.STORES
                R.id.profileFragment -> CurrentFragmentType.PROFILE
                R.id.addDiaryFragment -> CurrentFragmentType.ADD
                R.id.templateFragment -> CurrentFragmentType.TEMPLATE
                else -> viewModel.currentFragmentType.value
            }
        }
    }

    private fun setupDrawer() {
        val navController = this.findNavController(R.id.myNavHostFragment)
        setSupportActionBar(toolbar)

        //hiding action bar's system title
        supportActionBar?.title = null

        //setting actionbar and drawer's open event
        appBarConfiguration = AppBarConfiguration(navController.graph, binding.drawerLayout)
        NavigationUI.setupWithNavController(binding.drawerNavView, navController)

        actionBarDrawerToggle = object : ActionBarDrawerToggle(
            this,
            binding.drawerLayout,
            binding.toolbar,
            R.string.navigation_drawer_open,
            R.string.navigation_drawer_close
        ) {
            override fun onDrawerOpened(drawerView: View) {
                super.onDrawerOpened(drawerView)
            }
        }.apply {
            binding.drawerLayout.addDrawerListener(this)
            syncState()
        }

    }
}
