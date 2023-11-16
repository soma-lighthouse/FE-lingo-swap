package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.SWMLoggingScheme
import kotlin.properties.Delegates

class RegisterClickLogger private constructor(
    screenName: String,
    duration: Double,
    eventLogName: String,
    uuid: String,
) : SWMLoggingScheme() {
    init {
        setLoggingScheme(
            uuid = "",
            eventLogName = eventLogName,
            screenName = screenName,
            logVersion = 1,
            logData = mutableMapOf(
                eventLogName to mapOf(
                    "duration" to duration,
                    "uuid" to uuid
                )
            )
        )
    }

    class Builder {
        private var stayTime by Delegates.notNull<Double>()
        private lateinit var screenName: String
        private lateinit var eventLogName: String
        private lateinit var uuid: String

        fun setStayTime(stayTime: Double): Builder {
            this.stayTime = stayTime
            return this
        }

        fun setScreenName(screenName: String): Builder {
            this.screenName = screenName
            return this
        }

        fun setEventLogName(eventLogName: String): Builder {
            this.eventLogName = eventLogName
            return this
        }

        fun setUUID(uuid: String): Builder {
            this.uuid = uuid
            return this
        }

        fun build(): RegisterClickLogger {
            return RegisterClickLogger(
                screenName, stayTime, eventLogName, uuid
            )
        }
    }
}