package com.dindamaylan.storyapp.ui.story.add

import android.graphics.BitmapFactory
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.dindamaylan.storyapp.databinding.FragmentStoryPreviewBinding
import com.dindamaylan.storyapp.utils.Helper.rotateBitmap
import java.io.File

class StoryPreviewFragment : Fragment() {
    private var isBinding: FragmentStoryPreviewBinding? = null
    private val binding get() = isBinding as FragmentStoryPreviewBinding

    private val args: StoryPreviewFragmentArgs by navArgs()


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isBinding = FragmentStoryPreviewBinding.inflate(layoutInflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        val photoFile = args.photo
        val rotate = args.rotate
        val isFromFolder = args.isFromFolder

        val photo = if (isFromFolder) {
            BitmapFactory.decodeFile(photoFile.path)
        } else rotateBitmap(
            BitmapFactory.decodeFile(photoFile.path),
            rotate
        )

        binding.apply {
            ivPhotoPreview.setImageBitmap(photo)
            btnCancel.setOnClickListener {
                goBack()
            }
            btnOk.setOnClickListener {
                goToAddStory(
                    photoFile,
                    rotate,
                    isFromFolder
                )
            }
        }
    }

    private fun goBack() {
        findNavController().navigateUp()
    }

    private fun goToAddStory(photo: File, rotate: Boolean, isFromFolder: Boolean) {
        val direction = StoryPreviewFragmentDirections.actionStoryPreviewFragmentToStoryAddFragment(
            photo,
            rotate,
            isFromFolder
        )
        findNavController().navigate(direction)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        isBinding = null
    }
}