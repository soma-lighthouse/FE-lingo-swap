package com.lighthouse.domain.i18n.supportRegions

import com.lighthouse.lighthousei18n.I18nCurrency
import com.lighthouse.lighthousei18n.Region
import java.util.Currency
import java.util.Locale
import java.util.TimeZone

enum class SupportRegions : Region {
    KOREA {
        override val supportLanguagesFromJava: List<Locale>
            get() = Locale.getAvailableLocales().filter { it.country == "KR" }
        override val supportLanguagesManually: List<Locale>
            get() = listOf(Locale.forLanguageTag("ko-KR"))
        override val i18nCurrency: I18nCurrency
            get() = CurrencyInfo.KRW
        override val timezones: List<TimeZone>
            get() = TimeZone.getAvailableIDs().filter { it.contains("Seoul") }
                .map { TimeZone.getTimeZone(it) }
        override val timeFormat: String
            get() = "yyyy-MM-dd hh:mm:ss"
        override val javaCurrency: Currency
            get() = Currency.getInstance("KRW")
    },

    US {
        override val supportLanguagesFromJava: List<Locale>
            get() = Locale.getAvailableLocales().filter { it.country == "US" }
        override val supportLanguagesManually: List<Locale>
            get() = listOf(Locale.forLanguageTag("en-US"))
        override val i18nCurrency: I18nCurrency
            get() = CurrencyInfo.USD
        override val timezones: List<TimeZone>
            get() = TimeZone.getAvailableIDs().filter { it.contains("America") }
                .map { TimeZone.getTimeZone(it) }
        override val timeFormat: String
            get() = "MM-dd-yyyy hh:mm:ss"
        override val javaCurrency: Currency
            get() = Currency.getInstance("USD")
    };
}