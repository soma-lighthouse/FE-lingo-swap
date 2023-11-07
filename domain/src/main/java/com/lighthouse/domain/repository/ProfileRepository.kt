package com.lighthouse.domain.repository

import com.lighthouse.domain.entity.request.RegisterInfoVO
import com.lighthouse.domain.entity.response.vo.MyQuestionsVO
import com.lighthouse.domain.entity.response.vo.ProfileVO
import kotlinx.coroutines.flow.Flow

interface ProfileRepository {
    fun getProfileDetail(uuid: String): Flow<ProfileVO>

    fun getMyQuestions(): Flow<List<MyQuestionsVO>>

    fun updateProfile(newProfile: RegisterInfoVO): Flow<Boolean>

    fun updateFilter(newFilter: RegisterInfoVO): Flow<Boolean>

    fun getUUID(): String

    fun setPushEnabled(enabled: Boolean)
    fun getPushEnabled(): Boolean
}