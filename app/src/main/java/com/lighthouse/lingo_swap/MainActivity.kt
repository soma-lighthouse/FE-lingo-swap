package com.lighthouse.lingo_swap

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.lighthouse.lingo_swap.databinding.ActivityMainBinding
import com.lighthouse.navigation.NavigationFlow
import com.lighthouse.navigation.Navigator
import com.lighthouse.navigation.ToFlowNavigatable
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity(), ToFlowNavigatable {
    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                com.lighthouse.android.home.R.id.homeFragment -> showBottomNavBar()
                com.lighthouse.android.home.R.id.filterFragment -> hideBottomNavBar()
            }

        }

        navigator.navController = navController
        binding.bottomNav.setupWithNavController(navController)

    }

    override fun navigateToFlow(flow: NavigationFlow) {
        navigator.navigateToFlow(flow)
    }

    fun hideBottomNavBar() {
        binding.bottomNav.visibility = View.GONE
    }

    fun showBottomNavBar() {
        binding.bottomNav.visibility = View.VISIBLE
    }
}