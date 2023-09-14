package com.lighthouse.android.common_ui.dialog

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import com.lighthouse.android.common_ui.databinding.ImagePickerDialogBinding

class ImagePickerDialog : DialogFragment() {
    private lateinit var binding: ImagePickerDialogBinding
    private var listener: CameraDialogListener? = null

    companion object {
        fun newInstance() = ImagePickerDialog()
    }

    fun setListener(listener: CameraDialogListener) {
        this.listener = listener
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?,
    ): View {
        binding = ImagePickerDialogBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        initViews()
    }

    private fun initViews() {
        binding.btCamera.setOnClickListener {
            listener?.openCamera()
            dismiss()
        }

        binding.btGallery.setOnClickListener {
            listener?.openGallery()
            dismiss()
        }
    }

    interface CameraDialogListener {
        fun openCamera()
        fun openGallery()
    }

}