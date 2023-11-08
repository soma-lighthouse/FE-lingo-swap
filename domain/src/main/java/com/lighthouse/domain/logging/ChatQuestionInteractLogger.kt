package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.ExposureScheme
import kotlin.properties.Delegates

class ChatQuestionInteractLogger(
    uuid: String,
    question: String,
    category: Int,
    stayTime: Double,
) : ExposureScheme() {
    init {
        setLoggingScheme(
            uuid = uuid,
            eventLogName = "chat_question_interact",
            screenName = "chat_question",
            logVersion = 1,
            logData = mutableMapOf(
                "chat_question_interact" to mapOf(
                    "question" to question,
                    "category" to category,
                    "firstQuestionInteract" to stayTime
                )
            )
        )
    }

    class Builder {
        private var stayTime by Delegates.notNull<Double>()
        private lateinit var question: String
        private var category: Int = 0
        private lateinit var uuid: String

        fun setStayTime(stayTime: Double): Builder {
            this.stayTime = stayTime
            return this
        }

        fun setQuestion(question: String): Builder {
            this.question = question
            return this
        }

        fun setCategory(category: Int): Builder {
            this.category = category
            return this
        }

        fun setUuid(uuid: String): Builder {
            this.uuid = uuid
            return this
        }

        fun build(): ChatQuestionInteractLogger {
            return ChatQuestionInteractLogger(
                uuid,
                question,
                category,
                stayTime
            )
        }
    }
}