package com.lighthouse.android.home.adapter

import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.AsyncListDiffer
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.lighthouse.android.home.databinding.ListTileBinding

//class PostAdapter : RecyclerView.Adapter<PostAdapter.PostViewHolder>() {
//    private val callback = object : DiffUtil.ItemCallback<PostVO>() {
//        override fun areItemsTheSame(oldItem: PostVO, newItem: PostVO): Boolean {
//            return oldItem.id == newItem.id
//        }
//
//        override fun areContentsTheSame(oldItem: PostVO, newItem: PostVO): Boolean {
//            return oldItem == newItem
//        }
//    }
//
//    val differ = AsyncListDiffer(this, callback)
//
//    inner class PostViewHolder(private val binding: ListTileBinding) :
//        RecyclerView.ViewHolder(binding.root) {
//        fun onBind(post: PostVO) {
//            binding.title.text = post.title
//            binding.content.text = post.body
//        }
//    }
//
//    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): PostViewHolder {
//        val binding = ListTileBinding.inflate(LayoutInflater.from(parent.context), parent, false)
//        return PostViewHolder(binding)
//    }
//
//    override fun getItemCount() = differ.currentList.size
//
//    override fun onBindViewHolder(holder: PostViewHolder, position: Int) {
//        holder.onBind(differ.currentList[position])
//    }
//}