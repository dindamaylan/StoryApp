package com.dindamaylan.storyapp.ui.dashboard

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.navigation.fragment.findNavController
import androidx.paging.ExperimentalPagingApi
import androidx.recyclerview.widget.LinearLayoutManager
import com.dindamaylan.storyapp.MainActivity
import com.dindamaylan.storyapp.R
import com.dindamaylan.storyapp.data.adapter.ListStoriesAdapter
import com.dindamaylan.storyapp.data.adapter.LoadingStateAdapter
import com.dindamaylan.storyapp.databinding.FragmentDashboardBinding
import dagger.hilt.android.AndroidEntryPoint

@ExperimentalPagingApi
@AndroidEntryPoint
class DashboardFragment : Fragment() {
    private var isBinding: FragmentDashboardBinding? = null
    private val binding get() = isBinding!!

    private val dashboardViewModel: DashboardViewModel by viewModels()
    private lateinit var listStoriesAdapter: ListStoriesAdapter
    private lateinit var ac: MainActivity

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isBinding = FragmentDashboardBinding.inflate(LayoutInflater.from(requireActivity()))
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        ac = activity as MainActivity
        listStoriesAdapter = ListStoriesAdapter { stories, position ->

            val direction =
                DashboardFragmentDirections.actionDashboardFragmentToStoryDetailFragment(stories, position)
            findNavController().navigate(direction)
        }

        setRecycleList()
        doPullSwipeRefresh()
        getStoriesData()

        binding.fabStoryAdd.setOnClickListener {
            ac.askPermissionCamerax()
            findNavController().navigate(R.id.action_dashboardFragment_to_storyAddFragment)
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isBinding = null
    }

    private fun doPullSwipeRefresh() {
        binding.swipeRefresh.setOnRefreshListener {
            listStoriesAdapter.refresh()
            binding.rvStories.scrollToPosition(0)
            binding.swipeRefresh.isRefreshing = false
        }
    }

    private fun getStoriesData() {
        dashboardViewModel.getStories().observe(viewLifecycleOwner) {
            listStoriesAdapter.submitData(lifecycle, it)
        }
    }

    private fun setRecycleList() {
        binding.rvStories.apply {
            layoutManager = LinearLayoutManager(requireContext())
            adapter = listStoriesAdapter.withLoadStateFooter(
                footer = LoadingStateAdapter{
                    listStoriesAdapter.retry()
                }
            )
        }
    }
}