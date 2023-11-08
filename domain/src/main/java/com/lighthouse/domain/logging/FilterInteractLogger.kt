package com.lighthouse.domain.logging

import com.lighthouse.domain.entity.request.UploadFilterVO
import com.lighthouse.swm_logging.logging_scheme.ExposureScheme
import kotlin.properties.Delegates

class FilterInteractLogger(
    uuid: String,
    stayTime: Double,
    changedFilter: List<String>,
    filterVO: UploadFilterVO
) : ExposureScheme() {
    init {
        setLoggingScheme(
            uuid = uuid,
            eventLogName = "filter_interact",
            screenName = "filter_screen",
            logVersion = 1,
            logData = mutableMapOf(
                "filter_interact" to mapOf(
                    "changedFilter" to changedFilter,
                    "duration" to stayTime,
                    "filter" to filterVO
                )
            )
        )
    }

    class Builder {
        private var stayTime by Delegates.notNull<Double>()
        private var changedFilter by Delegates.notNull<List<String>>()
        private lateinit var filterVO: UploadFilterVO
        private lateinit var uuid: String

        fun setStayTime(stayTime: Double): Builder {
            this.stayTime = stayTime
            return this
        }

        fun setChangedFilter(changedFilter: List<String>): Builder {
            this.changedFilter = changedFilter
            return this
        }

        fun setUuid(uuid: String): Builder {
            this.uuid = uuid
            return this
        }

        fun setFilter(filterVO: UploadFilterVO): Builder {
            this.filterVO = filterVO
            return this
        }

        fun build(): FilterInteractLogger {
            return FilterInteractLogger(
                uuid,
                stayTime,
                changedFilter,
                filterVO
            )
        }
    }
}