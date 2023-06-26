package com.example.lingo_talk

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import androidx.databinding.DataBindingUtil
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.example.lingo_talk.databinding.ActivityMainBinding
import com.example.lingo_talk.presentation.adapter.PostRecyclerAdapter
import com.example.lingo_talk.presentation.viewModel.PostViewModel
import com.example.lingo_talk.presentation.viewModel.PostViewModelFactory
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : AppCompatActivity() {
    @Inject
    lateinit var factory: PostViewModelFactory
    @Inject
    lateinit var postAdapter: PostRecyclerAdapter
    private lateinit var binding: ActivityMainBinding
    private lateinit var viewModel: PostViewModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = DataBindingUtil.setContentView(this, R.layout.activity_main)
        viewModel = ViewModelProvider(this, factory)[PostViewModel::class.java]
        binding.viewModel = viewModel

        initRecyclerView()
    }

    private fun initRecyclerView() {
        binding.recyclerView.adapter = postAdapter
        binding.recyclerView.layoutManager = LinearLayoutManager(this)
        val response = viewModel.getPost()
        response.observe(this) {
            if(it != null) {
                postAdapter.differ.submitList(it?.toList())
            }

        }
    }
}