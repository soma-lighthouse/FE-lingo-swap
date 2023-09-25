package com.lighthouse.domain.i18n.supportRegions

import com.lighthouse.lighthousei18n.I18nCurrency

enum class CurrencyInfo : I18nCurrency {
    KRW {
        override val currencyCode: String
            get() = "KRW"
        override val prefixUnit: String
            get() = "₩"
        override val postfixUnit: String
            get() = "원"
        override val decimalPoint: Int
            get() = 0
    },

    USD {
        override val currencyCode: String
            get() = "USD"
        override val prefixUnit: String
            get() = "$"
        override val postfixUnit: String
            get() = "Dollar"
        override val decimalPoint: Int
            get() = 2
    };
}