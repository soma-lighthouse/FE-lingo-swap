package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.ClickScheme

class MatchingTimeAndCountLogger(
    opUid: String,
    name: String,
    region: String,
    clickTime: Double,
    clickCount: Int
) : ClickScheme() {
    init {
        setLoggingScheme(
            eventLogName = "profileClick",
            screenName = "home",
            logVersion = 1,
            logData = mutableMapOf(
                "opUuid" to opUid,
                "name" to name,
                "region" to region,
                "duration" to clickTime,
                "count" to clickCount
            )
        )
    }

    class Builder {
        private lateinit var name: String
        private lateinit var region: String
        private lateinit var opUid: String
        private var clickTime: Double = 0.0
        private var clickCount: Int = 0

        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        fun setRegion(region: String): Builder {
            this.region = region
            return this
        }

        fun setClickTime(clickTime: Double): Builder {
            this.clickTime = clickTime
            return this
        }

        fun setClickCount(clickCount: Int): Builder {
            this.clickCount = clickCount
            return this
        }

        fun setOpUid(opUid: String): Builder {
            this.opUid = opUid
            return this
        }

        fun build(): ClickScheme {
            return MatchingTimeAndCountLogger(
                opUid,
                name,
                region,
                clickTime,
                clickCount
            )
        }
    }
}