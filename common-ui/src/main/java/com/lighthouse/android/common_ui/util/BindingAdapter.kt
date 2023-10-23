package com.lighthouse.android.common_ui.util

import android.graphics.drawable.Drawable
import android.widget.ImageView
import android.widget.RatingBar
import androidx.databinding.BindingAdapter

@BindingAdapter("imgRes")
fun imageLoad(img: ImageView, res: Drawable?) {
    img.setImageDrawable(
        res
    )
}

@BindingAdapter("convertRating")
fun convertRating(rate: RatingBar, score: Double?) {
    rate.rating = score?.toFloat() ?: 0f
}