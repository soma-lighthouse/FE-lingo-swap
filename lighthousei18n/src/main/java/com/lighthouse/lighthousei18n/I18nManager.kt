package com.lighthouse.lighthousei18n

import java.util.Locale
import java.util.TimeZone

interface I18nManager {
    fun getAllSupportedRegions(): List<Region>
    fun getSelectedRegion(): Region?
    fun saveSelectedRegion(selectedRegion: Region, locale: Locale)
    fun getI18nCurrency(): I18nCurrency
    fun getLocale(): Locale
    fun resetSelectedI18n()
    fun getTimezone(): TimeZone
    fun getTimezoneId(): String
    fun getAllTimezone(): List<TimeZone>
    fun saveTimezoneId(timezoneId: String)
    fun getFormattingTimeFromTimeStamp(timeStamp: Long): String
}