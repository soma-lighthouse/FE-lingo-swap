package com.lighthouse.android.common_ui.util

import android.graphics.drawable.Drawable
import android.util.Log
import android.widget.EditText
import android.widget.ImageView
import android.widget.RatingBar
import android.widget.TextView
import androidx.databinding.BindingAdapter
import androidx.lifecycle.MutableLiveData
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.DecodeFormat
import com.google.android.material.chip.ChipGroup
import com.google.android.material.tabs.TabLayout
import com.lighthouse.android.common_ui.R
import com.lighthouse.android.common_ui.base.adapter.ItemDiffCallBack
import com.lighthouse.android.common_ui.base.adapter.SimpleListAdapter
import com.lighthouse.android.common_ui.databinding.LanguageTabBinding
import com.lighthouse.domain.entity.request.UploadQuestionVO
import com.lighthouse.domain.entity.response.vo.LanguageVO

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

@BindingAdapter(value = ["setUpFlag", "size"])
fun setUpFlag(image: ImageView, code: String?, size: Int?) {
    code?.let {
        val context = image.context
        val flag = context.resources.getIdentifier(
            code, "drawable", context.packageName
        )

        image.setImageResource(flag)
        image.layoutParams.width = calSize(Constant.PROFILE_FLAG_SIZE)
        image.layoutParams.height = calSize(Constant.PROFILE_FLAG_SIZE)
        size?.let {
            image.layoutParams.width = calSize(size.toFloat())
            image.layoutParams.height = calSize(size.toFloat())
        }
        image.requestLayout()
    }
}

@BindingAdapter("setUpImage", requireAll = false)
fun setUpImage(image: ImageView, url: String?) {
    val context = image.context
    Glide.with(context).load(url)
        .placeholder(R.drawable.placeholder)
        .skipMemoryCache(false)
        .format(DecodeFormat.PREFER_RGB_565)
        .centerInside()
        .override(calSize(Constant.PROFILE_IMAGE_SIZE))
        .dontAnimate()
        .into(image)
}

@BindingAdapter("setUpLanguage")
fun setUpLanguage(recycler: RecyclerView, languages: List<LanguageVO>?) {
    val adapter =
        SimpleListAdapter<String, LanguageTabBinding>(diffCallBack = ItemDiffCallBack(
            onContentsTheSame = { old, new -> old == new },
            onItemsTheSame = { old, new -> old == new }),
            layoutId = R.layout.language_tab,
            onBindCallback = { v, s ->
                val binding = v.binding
                binding.tvLanguage.text = s
            })

    val languages = languages?.map {
        "${it.name}/Lv.${it.level}"
    }

    adapter.submitList(languages)
    recycler.adapter = adapter
}

@BindingAdapter("setUpDescription")
fun setUpDescription(text: TextView, description: String?) {
    description?.let {
        val context = text.context
        val description = description.ifEmpty {
            context.getString(R.string.profile_description)
        }
        text.setText(description)
        if (text is EditText) {
            text.onCloseKeyBoard(context)
        }
    }
}

@BindingAdapter("bindChipGroup")
fun bindChipGroup(chipGroup: ChipGroup, upload: UploadQuestionVO) {
    chipGroup.setOnCheckedStateChangeListener { group, _ ->
        upload.categoryId = group.checkedChipId + 1
    }
}

@BindingAdapter("observeChange")
fun observeChange(adapter: RecyclerView, position: Int) {
    Log.d("TESTING LIKE", position.toString())
    adapter.adapter?.notifyItemChanged(position)
}

@BindingAdapter("currentTabPosition")
fun setCurrentTabPosition(tabLayout: TabLayout, position: MutableLiveData<Int>) {
    if (tabLayout.getTag(R.id.tabLayoutListener) == null) {
        tabLayout.addOnTabSelectedListener(object : TabLayout.OnTabSelectedListener {
            override fun onTabSelected(tab: TabLayout.Tab?) {
                position.value = tab?.position ?: 0
                tabLayout.isClickable = false
            }

            override fun onTabUnselected(tab: TabLayout.Tab?) {
                // TODO("Not yet implemented")
            }

            override fun onTabReselected(tab: TabLayout.Tab?) {
                // TODO("Not yet implemented")
            }
        })
        tabLayout.setTag(R.id.tabLayoutListener, true)
    }
}