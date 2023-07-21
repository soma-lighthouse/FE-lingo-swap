package com.lighthouse.android.common_ui.server_driven.viewholders

import android.view.ViewGroup
import com.lighthouse.android.common_ui.server_driven.viewholders.default_holder.DefaultViewHolder
import com.lighthouse.domain.constriant.ViewType

fun getViewHolder(parent: ViewGroup, viewType: ViewType): DefaultViewHolder {
    val viewTypeList = listOf(
        TitleViewHolder(parent),
        ChatRoomInfoViewHolder(parent)
    )

    for(type in viewTypeList) {
        if(type.getViewType() == viewType) {
            return type
        }
    }

    return UnknownViewHolder(parent)
}