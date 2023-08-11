package com.lighthouse.lingo_swap

import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.navigation.fragment.NavHostFragment
import androidx.navigation.ui.setupWithNavController
import com.google.firebase.messaging.FirebaseMessaging
import com.lighthouse.lingo_swap.databinding.ActivityMainBinding
import com.lighthouse.navigation.NavigationFlow
import com.lighthouse.navigation.Navigator
import com.lighthouse.navigation.ToFlowNavigatable
import com.sendbird.android.SendbirdChat
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity(), ToFlowNavigatable {
    lateinit var binding: ActivityMainBinding

    @Inject
    lateinit var navigator: Navigator

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        FirebaseMessaging.getInstance().token.addOnSuccessListener { task ->

            SendbirdChat.registerPushToken(task) { status, e ->
                if (e != null) {
                    Log.d("MESSAGING", "onInitSucceed: $e")
                }

                // ...
            }

        }

        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        val navHostFragment =
            supportFragmentManager.findFragmentById(R.id.nav_host_fragment) as NavHostFragment
        val navController = navHostFragment.navController

        navController.addOnDestinationChangedListener { _, destination, _ ->
            when (destination.id) {
                com.lighthouse.android.home.R.id.homeFragment -> showBottomNavBar()
                com.lighthouse.android.home.R.id.filterFragment -> hideBottomNavBar()
                com.lighthouse.board.R.id.boardFragment -> showBottomNavBar()
                com.lighthouse.board.R.id.addFragment -> hideBottomNavBar()
            }
        }

        navigator.navController = navController
        binding.bottomNav.setupWithNavController(navController)

    }

    override fun navigateToFlow(flow: NavigationFlow) {
        navigator.navigateToFlow(flow)
    }

    private fun hideBottomNavBar() {
        binding.bottomNav.visibility = View.GONE
    }

    fun showBottomNavBar() {
        binding.bottomNav.visibility = View.VISIBLE
    }
}