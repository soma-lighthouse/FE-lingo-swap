package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.ExposureScheme
import kotlin.properties.Delegates

class ChatQuestionInteractLogger(
    question: String,
    category: Int,
    stayTime: Double,
) : ExposureScheme() {
    init {
        setLoggingScheme(
            eventLogName = "chat_question_interact",
            screenName = "chat_question",
            logVersion = 1,
            logData = mutableMapOf(
                "question" to question,
                "category" to category,
                "firstQuestionInteract" to stayTime
            )
        )
    }

    class Builder {
        private var stayTime by Delegates.notNull<Double>()
        private lateinit var question: String
        private var category: Int = 0

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

        fun build(): ChatQuestionInteractLogger {
            return ChatQuestionInteractLogger(
                question,
                category,
                stayTime
            )
        }
    }
}