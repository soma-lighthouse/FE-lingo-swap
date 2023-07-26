package com.lighthouse.domain.constriant

import com.lighthouse.domain.response.ContentVO
import java.lang.reflect.Type

enum class ViewType(
    private val viewTypeClass: Type
) {
    HomeTitleViewType(ContentVO.HomeTitleContent::class.java),
    UserInfoViewType(ContentVO.UserInfoTile::class.java),
    UnknownViewType(ContentVO.UnknownContent::class.java);


    companion object {
        fun findClassByItsName(viewTypeString: String?): ViewType {
            values().find { it.name == viewTypeString }?.let {
                return it
            } ?: return UnknownViewType
        }

        fun findViewTypeClassByItsName(viewTypeString: String?): Type {
            return findClassByItsName(viewTypeString).viewTypeClass
        }

        fun getViewTypeByOrdinal(ordinalNum: Int): ViewType {
            return values()[ordinalNum]
        }
    }
}