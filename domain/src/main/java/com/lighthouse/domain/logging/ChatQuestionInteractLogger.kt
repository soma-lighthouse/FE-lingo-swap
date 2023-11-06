package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.ExposureScheme
import kotlin.properties.Delegates

class ChatQuestionInteractLogger(
    stayTime: Double
) : ExposureScheme() {
    init {
        setLoggingScheme(
            eventLogName = "exposure_log",
            screenName = "chat_question",
            logVersion = 1,
            logData = mutableMapOf(
                "stayTime" to stayTime
            )
        )
    }

    class Builder {
        private var stayTime by Delegates.notNull<Double>()

        fun setStayTime(stayTime: Double): Builder {
            this.stayTime = stayTime
            return this
        }

        fun build(): ChatQuestionInteractLogger {
            return ChatQuestionInteractLogger(
                stayTime
            )
        }
    }
}