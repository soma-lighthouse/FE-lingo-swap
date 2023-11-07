package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.ExposureScheme

class ProfileEditLogger(
    duration: Double,
    changes: List<String>
) : ExposureScheme() {
    init {
        setLoggingScheme(
            eventLogName = "profile_edit",
            screenName = "profile_screen",
            logVersion = 1,
            logData = mutableMapOf(
                "duration" to duration,
                "changes" to changes
            )
        )
    }

    class Builder {
        private var duration: Double = 0.0
        private lateinit var changes: List<String>

        fun setDuration(duration: Double): Builder {
            this.duration = duration
            return this
        }

        fun setChanges(changes: List<String>): Builder {
            this.changes = changes
            return this
        }

        fun build(): ProfileEditLogger {
            return ProfileEditLogger(
                duration,
                changes
            )
        }
    }
}