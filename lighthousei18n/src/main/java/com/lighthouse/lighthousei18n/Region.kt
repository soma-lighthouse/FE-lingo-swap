package com.lighthouse.lighthousei18n

import java.util.Currency
import java.util.Locale
import java.util.TimeZone

interface Region {
    val supportLanguagesFromJava: List<Locale>
    val supportLanguagesManually: List<Locale>
    val i18nCurrency: I18nCurrency
    val timezones: List<TimeZone>
    val timeFormat: String
    val javaCurrency: Currency
}