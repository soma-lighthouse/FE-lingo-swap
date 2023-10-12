package com.lighthouse.lighthousei18n

interface I18nCurrency {
    val currencyCode: String
    val prefixUnit: String
    val postfixUnit: String
    val decimalPoint: Int
}