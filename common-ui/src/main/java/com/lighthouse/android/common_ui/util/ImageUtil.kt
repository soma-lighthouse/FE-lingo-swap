package com.lighthouse.android.common_ui.util

import android.content.Context
import android.content.Intent
import android.net.Uri
import android.webkit.MimeTypeMap
import android.widget.ImageView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.lighthouse.android.common_ui.R

class ImageUtils {
    companion object {
        fun newInstance() = ImageUtils()
    }

    fun openGallery(): Intent {
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

    fun setFlagImage(image: ImageView, code: String, context: Context) {
        val flag = context.resources.getIdentifier(
            code, "drawable", context.packageName
        )

        image.setImageResource(flag)
        image.layoutParams.width = calSize(Constant.PROFILE_FLAG_SIZE)
        image.layoutParams.height = calSize(Constant.PROFILE_FLAG_SIZE)
        image.requestLayout()
    }

    fun setImage(image: ImageView, url: String, context: Context) {
        Glide.with(context).load(url)
            .placeholder(R.drawable.placeholder)
            .skipMemoryCache(false)
            .format(DecodeFormat.PREFER_RGB_565)
            .centerInside()
            .override(calSize(Constant.PROFILE_IMAGE_SIZE))
            .dontAnimate()
            .into(image)
    }
}