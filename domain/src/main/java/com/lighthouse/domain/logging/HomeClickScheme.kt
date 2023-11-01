package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.ClickScheme

class HomeClickScheme(
    name: String
) : ClickScheme() {
    init {
        setLoggingScheme(
            evenLogName = "normalClick",
            screenName = "home",
            logVersion = 1,
            logData = mutableMapOf(
                "name" to name
            )
        )
    }

    class Builder {
        private lateinit var name: String
        fun setName(name: String): Builder {
            this.name = name
            return this
        }

        fun build(): ClickScheme {
            return HomeClickScheme(
                name
            )
        }
    }
}