package com.lighthouse.domain.logging

import com.lighthouse.swm_logging.logging_scheme.ExposureScheme

class ProfileEditLogger(
    uuid: String,
    duration: Double,
    changes: List<String>
) : ExposureScheme() {
    init {
        setLoggingScheme(
            uuid = uuid,
            eventLogName = "profile_edit",
            screenName = "profile_screen",
            logVersion = 1,
            logData = mutableMapOf(
                "profile_edit" to mapOf(
                    "duration" to duration,
                    "changedProfile" to changes
                )
            )
        )
    }

    class Builder {
        private var duration: Double = 0.0
        private lateinit var changes: List<String>
        private lateinit var uuid: String

        fun setDuration(duration: Double): Builder {
            this.duration = duration
            return this
        }

        fun setChanges(changes: List<String>): Builder {
            this.changes = changes
            return this
        }

        fun setUuid(uuid: String): Builder {
            this.uuid = uuid
            return this
        }

        fun build(): ProfileEditLogger {
            return ProfileEditLogger(
                uuid,
                duration,
                changes
            )
        }
    }
}