package com.dindamaylan.storyapp.ui.story.detail

import android.content.Context
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dindamaylan.storyapp.R
import com.dindamaylan.storyapp.data.local.Stories
import com.dindamaylan.storyapp.databinding.FragmentStoryDetailBinding
import com.dindamaylan.storyapp.utils.Helper.dateFormat
import com.dindamaylan.storyapp.utils.Helper.setImage
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class StoryDetailFragment : Fragment() {
    private var isBinding: FragmentStoryDetailBinding? = null
    private val binding get() = isBinding as FragmentStoryDetailBinding

    private val args: StoryDetailFragmentArgs by navArgs()
    private var position = -1

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isBinding = FragmentStoryDetailBinding.inflate(layoutInflater, container, false)
        binding.toolbar.apply {
            navigationIcon =
                ContextCompat.getDrawable(requireContext(), R.drawable.ic_baseline_arrow_back)
        }
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        binding.toolbar.setNavigationOnClickListener {
            goBack(findNavController())
        }

        val story = args.story
        position = args.position
        Log.d("TAG", "position: $position")
        detailStory(requireContext(), story)
    }

    private fun detailStory(context: Context, stories: Stories) {
        binding.apply {
            ivStory.setImage(context, stories.photoUrl)
            root.transitionName = "root_$position"
            tvName.text = stories.name
            tvDate.dateFormat(stories.createdAt)
            tvDesc.text = stories.description
        }
    }

    private fun goBack(findNavController: NavController) {
        findNavController.navigateUp()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isBinding = null
    }
}