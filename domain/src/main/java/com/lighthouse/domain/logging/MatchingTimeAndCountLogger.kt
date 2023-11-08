package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.ClickScheme

class MatchingTimeAndCountLogger(
    uuid: String,
    opUid: String,
    name: String,
    region: String,
    clickTime: Double,
    clickCount: Int
) : ClickScheme() {
    init {
        setLoggingScheme(
            uuid = uuid,
            eventLogName = "profile_click",
            screenName = "home",
            logVersion = 1,
            logData = mutableMapOf(
                "profileClick" to mapOf(
                    "opUuid" to opUid,
                    "name" to name,
                    "region" to region,
                    "duration" to clickTime,
                    "count" to clickCount
                )
            )
        )
    }

    class Builder {
        private lateinit var name: String
        private lateinit var region: String
        private lateinit var opUid: String
        private lateinit var uuid: String
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

        fun setUuid(uuid: String): Builder {
            this.uuid = uuid
            return this
        }

        fun build(): ClickScheme {
            return MatchingTimeAndCountLogger(
                uuid,
                opUid,
                name,
                region,
                clickTime,
                clickCount
            )
        }
    }
}