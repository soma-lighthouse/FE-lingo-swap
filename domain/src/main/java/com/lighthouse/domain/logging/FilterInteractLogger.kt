package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.ExposureScheme
import kotlin.properties.Delegates

class FilterInteractLogger(
    stayTime: Double,
    changedFilter: List<String>,
) : ExposureScheme() {
    init {
        setLoggingScheme(
            eventLogName = "filter_interact",
            screenName = "filter_screen",
            logVersion = 1,
            logData = mutableMapOf(
                "changedFilter" to changedFilter,
                "duration" to stayTime
            )
        )
    }

    class Builder {
        private var stayTime by Delegates.notNull<Double>()
        private var changedFilter by Delegates.notNull<List<String>>()

        fun setStayTime(stayTime: Double): Builder {
            this.stayTime = stayTime
            return this
        }

        fun setChangedFilter(changedFilter: List<String>): Builder {
            this.changedFilter = changedFilter
            return this
        }

        fun build(): FilterInteractLogger {
            return FilterInteractLogger(
                stayTime,
                changedFilter
            )
        }
    }
}