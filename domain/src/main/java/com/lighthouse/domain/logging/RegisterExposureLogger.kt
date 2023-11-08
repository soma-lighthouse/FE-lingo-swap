package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.ExposureScheme
import kotlin.properties.Delegates

class RegisterExposureLogger(
    uuid: String, stayTime: Double
) : ExposureScheme() {
    init {
        setLoggingScheme(
            uuid = uuid,
            eventLogName = "register_time",
            screenName = "register_screen",
            logVersion = 1,
            logData = mutableMapOf(
                "register_time" to mapOf(
                    "duration" to stayTime
                )
            )
        )
    }

    class Builder {
        private var stayTime by Delegates.notNull<Double>()
        private lateinit var uuid: String

        fun setStayTime(stayTime: Double): Builder {
            this.stayTime = stayTime
            return this
        }

        fun setUuid(uuid: String): Builder {
            this.uuid = uuid
            return this
        }

        fun build(): RegisterExposureLogger {
            return RegisterExposureLogger(
                uuid, stayTime
            )
        }
    }
}