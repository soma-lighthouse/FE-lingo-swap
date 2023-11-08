package com.lighthouse.swm_logging.logging_scheme

import com.lighthouse.swm_logging.SWMLogging

abstract class SWMLoggingScheme {
    open lateinit var eventLogName: String
    open lateinit var screenName: String
    open var logVersion: Int = 0
    private val osVersionAndName: String = SWMLogging.getOsNameAndVersion()
    private lateinit var uuid: String
    private var logData: MutableMap<String, Any>? = mutableMapOf()
    fun setLoggingScheme(
        uuid: String,
        eventLogName: String,
        screenName: String,
        logVersion: Int,
        logData: MutableMap<String, Any>?
    ) {
        this.uuid = uuid
        this.eventLogName = eventLogName
        this.screenName = screenName
        this.logVersion = logVersion
        this.logData = logData
    }
}
