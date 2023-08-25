package com.lighthouse.android.data.model.response

import com.google.gson.annotations.SerializedName

sealed class FormResponse

data class CountryForm(
    @SerializedName("countryForm")
    val country: List<CountryDTO>,
) : FormResponse()

data class InterestForm(
    @SerializedName("interestsForm")
    val interest: List<InterestDTO>,
) : FormResponse()

data class LanguageForm(
    @SerializedName("languageForm")
    val language: List<LanguageDTO>,
) : FormResponse()