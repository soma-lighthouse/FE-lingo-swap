package com.lighthouse.lingo_talk.i18n

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.domain.i18n.supportRegions.SupportRegions
import com.lighthouse.lighthousei18n.I18nCurrency
import com.lighthouse.lighthousei18n.I18nManager
import com.lighthouse.lighthousei18n.Region
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale
import java.util.TimeZone

class I18nManagerImpl(private val localPreferenceDataSource: LocalPreferenceDataSource) :
    I18nManager {
    private val defaultLocale = SupportRegions.US
    override fun getAllSupportedRegions(): List<Region> {
        return SupportRegions.values().toList()
    }

    override fun saveSelectedRegion(selectedRegion: Region, locale: Locale) {
        if (selectedRegion !is SupportRegions) {
            throw IllegalArgumentException("selectedRegion is not SupportRegions")
        }
        localPreferenceDataSource.saveCurrentRegion(KEY_SELECTED_REGION, selectedRegion.name)
        localPreferenceDataSource.saveCurrentRegion(
            KEY_SELECTED_LOCALE,
            locale.toLanguageTag()
        )
    }

    override fun getSelectedRegion(): Region? {
        localPreferenceDataSource.getCurrentRegion(KEY_SELECTED_REGION)?.let {
            return SupportRegions.valueOf(it)
        }

        return null
    }

    override fun getI18nCurrency(): I18nCurrency {
        getSelectedRegion()?.let {
            return it.i18nCurrency
        } ?: run {
            return defaultLocale.i18nCurrency
        }
    }

    override fun getLocale(): Locale {
        val selectedLanguage = localPreferenceDataSource.getCurrentRegion(KEY_SELECTED_LOCALE)
            ?: defaultLocale.supportLanguagesFromJava.first().toLanguageTag()
        return Locale.forLanguageTag(selectedLanguage)
    }

    override fun resetSelectedI18n() {
        localPreferenceDataSource.saveCurrentRegion(KEY_SELECTED_REGION, null)
        localPreferenceDataSource.saveCurrentRegion(KEY_SELECTED_LOCALE, null)
    }

    override fun getTimezone(): TimeZone {
        val curTimezone = localPreferenceDataSource.getCurrentRegion(KEY_SELECTED_TIMEZONE)
        return curTimezone?.let {
            TimeZone.getTimeZone(it)
        } ?: run {
            val defaultTimezone = defaultLocale.timezones.first()
            localPreferenceDataSource.saveCurrentRegion(
                KEY_SELECTED_TIMEZONE,
                defaultTimezone.id
            )
            defaultTimezone
        }
    }

    override fun getTimezoneId(): String {
        return getTimezone().id
    }

    override fun getAllTimezone(): List<TimeZone> {
        getSelectedRegion()?.let {
            return it.timezones
        } ?: run {
            return defaultLocale.timezones
        }
    }

    override fun saveTimezoneId(timezoneId: String) {
        localPreferenceDataSource.saveCurrentRegion(KEY_SELECTED_TIMEZONE, timezoneId)
    }

    override fun getFormattingTimeFromTimeStamp(timeStamp: Long): String {
        val dateFormat = SimpleDateFormat(getSelectedRegion()?.timeFormat, getLocale())
        dateFormat.timeZone = getTimezone()
        val date = Date(timeStamp)
        return dateFormat.format(date)
    }

    companion object {
        private const val KEY_SELECTED_REGION = "KEY_SELECTED_REGION"
        private const val KEY_SELECTED_LOCALE = "KEY_SELECTED_LOCALE"
        private const val KEY_SELECTED_TIMEZONE = "KEY_SELECTED_TIMEZONE"
    }
}