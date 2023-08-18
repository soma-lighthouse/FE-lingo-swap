package com.lighthouse.android.common_ui.util

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.view.View
import android.webkit.MimeTypeMap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy

class ImageUtils {
    companion object {
        fun newInstance() = ImageUtils()
    }

    fun downloadImage(pUrl: String, pView: View) {
        when (pView) {
            is ImageView -> {
                setImageToView(
                    pUrl = pUrl,
                    pView = pView
                )
            }
        }
    }

    private fun setImageToView(pUrl: String, pView: ImageView) {
        Glide.with(pView.context)
            .load(pUrl)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .into(pView);
    }

    fun openGallery(pContext: Activity): Intent {
        val intent = Intent()
        // Setting intent type as image to select image from phone storage.
        intent.type = "image/*"
        intent.action = Intent.ACTION_GET_CONTENT

        return Intent.createChooser(intent, "Please Select Image")
    }

    fun getFileExtension(pContext: Context, uri: Uri?): String? {
        val contentResolver = pContext.contentResolver
        val mimeTypeMap = MimeTypeMap.getSingleton()
        return mimeTypeMap.getExtensionFromMimeType(contentResolver.getType(uri!!))
    }
}