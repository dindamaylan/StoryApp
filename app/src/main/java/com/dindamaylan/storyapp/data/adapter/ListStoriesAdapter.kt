package com.dindamaylan.storyapp.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.paging.PagingDataAdapter
import androidx.recyclerview.widget.DiffUtil
import androidx.recyclerview.widget.RecyclerView
import com.dindamaylan.storyapp.data.local.Stories
import com.dindamaylan.storyapp.databinding.ItemStoryBinding
import com.dindamaylan.storyapp.utils.Helper.dateFormat
import com.dindamaylan.storyapp.utils.Helper.setImage

class ListStoriesAdapter(private val onClick: (stories: Stories, position: Int) -> Unit) :
    PagingDataAdapter<Stories, ListStoriesAdapter.ListStoriesViewHolder>(callback) {
    inner class ListStoriesViewHolder(private val binding: ItemStoryBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bind(context: Context, stories: Stories) {
            binding.apply {
                ivStory.setImage(context, stories.photoUrl)
                root.transitionName = "root_$absoluteAdapterPosition"
                tvUser.text = stories.name
                tvDate.dateFormat(stories.createdAt)
                tvDesc.text = stories.description
                root.setOnClickListener {
                    onClick(stories, absoluteAdapterPosition)
                }
            }
        }

    }

    companion object {
        val callback = object : DiffUtil.ItemCallback<Stories>() {
            override fun areItemsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem.id == newItem.id
            }

            override fun areContentsTheSame(oldItem: Stories, newItem: Stories): Boolean {
                return oldItem == newItem
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ListStoriesViewHolder {
        val view: ItemStoryBinding =
            ItemStoryBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ListStoriesViewHolder(view)
    }

    override fun onBindViewHolder(holder: ListStoriesViewHolder, position: Int) {
        val story = getItem(position)
        if (story != null) {
            holder.bind(holder.itemView.context, story)
        }
    }


}