package com.lighthouse.android.common_ui.dialog

import android.app.AlertDialog
import android.content.Context
import android.view.LayoutInflater
import androidx.fragment.app.DialogFragment
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.databinding.ImagePickerDialogBinding

class ImagePickerDialog : DialogFragment() {
    companion object {
        fun newInstance() = ImagePickerDialog()
    }

    fun showDialog(
        context: Context,
        listener: CameraDialogListener,
    ) {
        val layoutInflater = LayoutInflater.from(context)
        val binding = ImagePickerDialogBinding.inflate(layoutInflater, null, false)

        val dialog = AlertDialog.Builder(context)
            .setTitle(R.string.choose_image)
            .setView(binding.root)
            .setNegativeButton(R.string.cancel) { dialog, _ ->
                dialog.dismiss()
            }
            .show()

        dialog.setOnShowListener {
            binding.btnCamera.requestFocus()
        }

        binding.btnCamera.setOnClickListener {
            listener.openCamera()
            dialog.dismiss()
        }

        binding.btnGallery.setOnClickListener {
            listener.openGallery()
            dialog.dismiss()
        }

    }

    interface CameraDialogListener {
        fun openCamera()
        fun openGallery()
    }

}