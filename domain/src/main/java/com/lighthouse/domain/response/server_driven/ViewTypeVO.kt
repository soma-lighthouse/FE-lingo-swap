package com.lighthouse.domain.response.server_driven

import com.lighthouse.domain.constriant.ViewType

data class ViewTypeVO(
    val id: Int,
    val viewType: ViewType,
    val content: ContentVO,
)