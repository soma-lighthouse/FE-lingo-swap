package com.lighthouse.domain.repository


fun interface IntroRepository {
    suspend fun getIntro() : String
}