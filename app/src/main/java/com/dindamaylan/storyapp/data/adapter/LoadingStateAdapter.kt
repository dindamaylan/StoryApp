package com.dindamaylan.storyapp.data.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.core.view.isVisible
import androidx.paging.LoadState
import androidx.paging.LoadStateAdapter
import androidx.recyclerview.widget.RecyclerView
import com.dindamaylan.storyapp.R
import com.dindamaylan.storyapp.databinding.ItemLoadBinding
import retrofit2.HttpException
import java.io.IOException

class LoadingStateAdapter(private val retry: () -> Unit) :
    LoadStateAdapter<LoadingStateAdapter.LoadStateViewHolder>() {

    private lateinit var context: Context

    override fun onCreateViewHolder(parent: ViewGroup, loadState: LoadState): LoadStateViewHolder {
        context = parent.context
        val binding = ItemLoadBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return LoadStateViewHolder(binding)
    }

    override fun onBindViewHolder(holder: LoadStateViewHolder, loadState: LoadState) {
        holder.bind(loadState)
    }

    inner class LoadStateViewHolder(private val binding: ItemLoadBinding): RecyclerView.ViewHolder(binding.root) {

        init {
            binding.btnRetry.setOnClickListener { retry.invoke() }
        }

        fun bind(loadState: LoadState) {
            if (loadState is LoadState.Error) {
                val exception = loadState.error
                when(exception){
                    is HttpException -> binding.tvAlert.text = context.getString(R.string.alert_timeout)
                    is IOException -> binding.tvAlert.text = context.getString(R.string.alert_disconnect)
                }

            }
            binding.pbLoading.isVisible = loadState is LoadState.Loading
            binding.btnRetry.isVisible = loadState is LoadState.Error
            binding.tvAlert.isVisible = loadState is LoadState.Error
        }

    }
}
