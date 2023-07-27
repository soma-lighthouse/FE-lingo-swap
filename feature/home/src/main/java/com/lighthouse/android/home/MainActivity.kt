package com.lighthouse.android.home

import android.os.Bundle
import android.util.Log
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.viewpager2.widget.ViewPager2
import com.lighthouse.android.home.adapter.HomePageAdapter
import com.lighthouse.android.home.databinding.ActivityMainBinding
import com.lighthouse.android.home.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import java.util.UUID
import javax.inject.Inject


@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {
    private lateinit var binding: ActivityMainBinding
    private val viewModel: MainViewModel by viewModels()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel
        val uuid = UUID.randomUUID().toString()
        Log.d("UUID", uuid)
        initBtnNav()
    }

    private fun initBtnNav() {
        binding.vpHomePager.apply {
            adapter = HomePageAdapter(supportFragmentManager, lifecycle)
            registerOnPageChangeCallback(
                object : ViewPager2.OnPageChangeCallback() {
                    override fun onPageSelected(position: Int) {
                        super.onPageSelected(position)
                        binding.bottomNav.menu.getItem(position).isChecked = true
                    }
                }
            )
        }
        binding.bottomNav.setOnItemSelectedListener {
            binding.vpHomePager.apply {
                when (it.itemId) {
                    R.id.item_home -> currentItem = 0
                    R.id.item_chat -> currentItem = 1
                    R.id.item_board -> currentItem = 2
                    R.id.item_profile -> currentItem = 3
                }
            }
            true
        }
    }
}