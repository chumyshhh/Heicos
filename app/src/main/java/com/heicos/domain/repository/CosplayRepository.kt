package com.heicos.domain.repository

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.model.SearchQuery
import com.heicos.domain.util.CosplayType
import com.heicos.utils.Resource
import kotlinx.coroutines.flow.Flow

interface CosplayRepository {
    suspend fun getCosplays(
        page: Int,
        cosplayType: CosplayType,
        showDownloaded: Boolean
    ): Flow<Resource<List<CosplayPreview>>>

    suspend fun getFullCosplay(url: String): Flow<Resource<List<String>>>
    suspend fun getCosplayTags(url: String): Flow<Resource<List<String>>>
    suspend fun getCosplayLastPage(): Int
    suspend fun getSearchQueries(): Flow<List<SearchQuery>>
    suspend fun upsertSearchQuery(searchItem: SearchQuery)
    suspend fun deleteSearchQueryById(searchItem: SearchQuery)
    suspend fun deleteAllSearchQueries()

    suspend fun findCosplayPreview(url: String): CosplayPreview?
    suspend fun insertCosplayPreview(
        cosplay: CosplayPreview,
        time: Long?
    ): Long

    suspend fun updateCosplayPreview(
        cosplay: CosplayPreview,
        time: Long?,
        isDownloaded: Boolean
    )

    suspend fun getFullVideoCosplay(url: String): String
    suspend fun deleteCosplayPreview(cosplayPreview: CosplayPreview)
}