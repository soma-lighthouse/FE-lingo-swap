package com.lighthouse.lingo_talk.i18n

import com.lighthouse.android.data.local.LocalPreferenceDataSource
import com.lighthouse.android.data.util.LocalKey
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
        localPreferenceDataSource.save(LocalKey.SELECTED_REGION, selectedRegion.name)
        localPreferenceDataSource.save(
            LocalKey.SELECTED_LOCALE,
            locale.toLanguageTag()
        )
    }

    override fun getSelectedRegion(): Region? {
        localPreferenceDataSource.getString(LocalKey.SELECTED_REGION)?.let {
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
        val selectedLanguage = localPreferenceDataSource.getString(LocalKey.SELECTED_LOCALE)
            ?: defaultLocale.supportLanguagesFromJava.first().toLanguageTag()
        return Locale.forLanguageTag(selectedLanguage)
    }

    override fun resetSelectedI18n() {
        localPreferenceDataSource.save(LocalKey.SELECTED_REGION, "")
        localPreferenceDataSource.save(LocalKey.SELECTED_LOCALE, "")
    }

    override fun getTimezone(): TimeZone {
        val curTimezone = localPreferenceDataSource.getString(LocalKey.SELECTED_TIMEZONE)
        return curTimezone?.let {
            TimeZone.getTimeZone(it)
        } ?: run {
            val defaultTimezone = defaultLocale.timezones.first()
            localPreferenceDataSource.save(
                LocalKey.SELECTED_TIMEZONE,
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
        localPreferenceDataSource.save(LocalKey.SELECTED_TIMEZONE, timezoneId)
    }

    override fun getFormattingTimeFromTimeStamp(timeStamp: Long): String {
        val dateFormat = SimpleDateFormat(getSelectedRegion()?.timeFormat, getLocale())
        dateFormat.timeZone = getTimezone()
        val date = Date(timeStamp)
        return dateFormat.format(date)
    }

}