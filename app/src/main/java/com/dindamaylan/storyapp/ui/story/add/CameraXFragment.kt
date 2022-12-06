package com.dindamaylan.storyapp.ui.story.add

import android.net.Uri
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageCapture
import androidx.camera.core.ImageCaptureException
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.navigation.fragment.findNavController
import com.dindamaylan.storyapp.MainActivity
import com.dindamaylan.storyapp.R
import com.dindamaylan.storyapp.databinding.FragmentCameraXBinding
import com.dindamaylan.storyapp.utils.Helper.createFile
import com.dindamaylan.storyapp.utils.Helper.uriToFile
import java.io.File

class CameraXFragment : Fragment() {
    private var isBinding: FragmentCameraXBinding? = null
    private val binding get() = isBinding!!

    private var cameraSelector: CameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
    private var imageCapture: ImageCapture? = null

    private lateinit var ac: MainActivity

    private lateinit var resultLauncher: ActivityResultLauncher<String>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        resultLauncher =
            registerForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
                if (uri != null) {
                    val photoFile = uriToFile(uri, requireContext())
                    navigateToPreviewPhoto(photo = photoFile, rotate = true, isFromFolder = true)
                }
            }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        isBinding = FragmentCameraXBinding.inflate(layoutInflater, container, false)
        // Inflate the layout for this fragment
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        ac = activity as MainActivity

        if (ac.askPermissionCamerax()){
            startCamera()
            binding.apply {
                btnSwitchCamera.setOnClickListener {
                    setupSwitchCamera()
                }
                btnCaptureImage.setOnClickListener {
                    takePhoto()
                }
            }
        }
    }

    private fun setupSwitchCamera() {
        cameraSelector =
            if (cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA) CameraSelector.DEFAULT_FRONT_CAMERA
            else CameraSelector.DEFAULT_BACK_CAMERA
        startCamera()
    }

    private fun startCamera() {
        val processCameraProvider = ProcessCameraProvider.getInstance(requireActivity())

        processCameraProvider.addListener({
            val cameraProvider: ProcessCameraProvider = processCameraProvider.get()
            val preview = Preview.Builder()
                .build()
                .also {
                    it.setSurfaceProvider(binding.viewFinder.surfaceProvider)
                }

            imageCapture = ImageCapture.Builder().build()

            try {
                cameraProvider.unbindAll()
                cameraProvider.bindToLifecycle(
                    this,
                    cameraSelector,
                    preview,
                    imageCapture
                )
            } catch (exc: Exception) {
                Toast.makeText(
                    requireContext(),
                    getString(R.string.exception_open_camera),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }, ContextCompat.getMainExecutor(requireActivity()))
    }

    private fun takePhoto() {
        val imageCapture = imageCapture ?: return

        val photoFile = createFile(ac.application)

        val outputOptions = ImageCapture
            .OutputFileOptions
            .Builder(photoFile)
            .build()

        imageCapture.takePicture(
            outputOptions,
            ContextCompat.getMainExecutor(requireActivity()),
            onImageSaveCallBack(photoFile)
        )
    }

    private fun onImageSaveCallBack(photoFile: File): ImageCapture.OnImageSavedCallback {
        return object : ImageCapture.OnImageSavedCallback{
            override fun onImageSaved(outputFileResults: ImageCapture.OutputFileResults) {
                navigateToPreviewPhoto(photoFile, cameraSelector == CameraSelector.DEFAULT_BACK_CAMERA)
            }

            override fun onError(exception: ImageCaptureException) {
                Toast.makeText(
                    context,
                    "Gagal memunculkan camera",
                    Toast.LENGTH_SHORT
                ).show()
            }
        }
    }

    private fun navigateToPreviewPhoto(
        photo: File,
        rotate: Boolean,
        isFromFolder: Boolean = false
    ) {

        val direction = CameraXFragmentDirections.actionCameraXFragmentToStoryPreviewFragment(
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