package com.lighthouse.android.home

import android.os.Bundle
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.lighthouse.android.home.adapter.PostAdapter
import com.lighthouse.android.home.databinding.ActivityMainBinding
import com.lighthouse.android.home.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity @Inject constructor() : AppCompatActivity() {
    private lateinit var postAdapter: PostAdapter
    private lateinit var binding: ActivityMainBinding
    val viewModel: MainViewModel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        binding.viewModel = viewModel

        postAdapter = PostAdapter()

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.adapter = postAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val response = viewModel.getPost()
        response.observe(this) {
            if(it != null) {
                postAdapter.differ.submitList(it.toList())
            }

        }
    }
}