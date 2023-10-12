package com.lighthouse.lighthousei18n

import java.util.Locale
import java.util.TimeZone

object I18n : I18nManager {
    private lateinit var delegate: I18nManager

    fun init(i18nManager: I18nManager) {
        this.delegate = i18nManager
    }

    override fun getAllSupportedRegions(): List<Region> {
        return delegate.getAllSupportedRegions()
    }

    override fun getSelectedRegion(): Region? {
        return delegate.getSelectedRegion()
    }

    override fun saveSelectedRegion(selectedRegion: Region, locale: Locale) {
        return delegate.saveSelectedRegion(selectedRegion, locale)
    }

    override fun getI18nCurrency(): I18nCurrency {
        return delegate.getI18nCurrency()
    }

    override fun getLocale(): Locale {
        return delegate.getLocale()
    }

    override fun resetSelectedI18n() {
        return delegate.resetSelectedI18n()
    }

    override fun getTimezone(): TimeZone {
        return delegate.getTimezone()
    }

    override fun getTimezoneId(): String {
        return delegate.getTimezoneId()
    }

    override fun getAllTimezone(): List<TimeZone> {
        return delegate.getAllTimezone()
    }

    override fun saveTimezoneId(timezoneId: String) {
        return delegate.saveTimezoneId(timezoneId)
    }

    override fun getFormattingTimeFromTimeStamp(timeStamp: Long): String {
        return delegate.getFormattingTimeFromTimeStamp(timeStamp)
    }
}