package com.lighthouse.android.home.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.lifecycle.Lifecycle
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.lighthouse.android.home.view.BoardFragment
import com.lighthouse.android.home.view.ChatFragment
import com.lighthouse.android.home.view.HomeFragment
import com.lighthouse.android.home.view.ProfileFragment

class HomePageAdapter(fragment: FragmentManager, lifecycle: Lifecycle) :
    FragmentStateAdapter(fragment, lifecycle) {
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment {
        return when (position) {
            0 -> HomeFragment()
            1 -> ChatFragment()
            2 -> BoardFragment()
            3 -> ProfileFragment()
            else -> HomeFragment()
        }
    }
}