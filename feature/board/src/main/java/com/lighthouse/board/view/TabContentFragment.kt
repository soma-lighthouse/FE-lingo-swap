package com.lighthouse.board.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.databinding.DataBindingUtil
import androidx.fragment.app.Fragment
import com.lighthouse.board.R
import com.lighthouse.board.databinding.FragmentTabContentBinding

class TabContentFragment : Fragment() {
    private lateinit var binding: FragmentTabContentBinding

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = DataBindingUtil.inflate(inflater, R.layout.fragment_tab_content, container, false)

        val content = arguments?.getInt("tab_pos") ?: "Default Content"

        binding.srBoard.setOnRefreshListener {
            Toast.makeText(context, "Hello!", Toast.LENGTH_SHORT).show()
            binding.srBoard.isRefreshing = false
        }

        return binding.root
    }
}