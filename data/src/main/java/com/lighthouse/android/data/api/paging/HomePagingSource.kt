package com.lighthouse.android.data.api.paging

import androidx.paging.PagingSource
import androidx.paging.PagingState
import com.lighthouse.android.data.api.HomeApiService
import com.lighthouse.android.data.util.HttpResponseStatus
import com.lighthouse.domain.response.dto.ProfileVO
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class HomePagingSource @Inject constructor(
    private val service: HomeApiService,
) : PagingSource<Int, ProfileVO>() {
    override fun getRefreshKey(state: PagingState<Int, ProfileVO>): Int? {
        return state.anchorPosition?.let { anchorPosition ->
            state.closestPageToPosition(anchorPosition)?.prevKey?.plus(1)
                ?: state.closestPageToPosition(anchorPosition)?.nextKey?.minus(1)
        }
    }

    override suspend fun load(params: LoadParams<Int>): LoadResult<Int, ProfileVO> {
        val page = params.key ?: 1

        return try {
            val response = service.getMatchedUser(page)
            val body = response.body()
            val data = body!!.data.takeIf {
                HttpResponseStatus.create(body.code) == HttpResponseStatus.OK
            }?.toVO()

            val nextKey = if (data!!.profile.isEmpty()) {
                null
            } else {
                page + (params.loadSize / 10)
            }

            LoadResult.Page(
                data.profile,
                nextKey = nextKey,
                prevKey = if (page == 1) null else page - 1
            )
        } catch (e: IOException) {
            LoadResult.Error(e)
        } catch (e: HttpException) {
            LoadResult.Error(e)
        }

    }
}