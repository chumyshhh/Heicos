package com.heicos.data.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.heicos.data.database.entities.CosplayPreviewEntity

@Dao
interface CosplayPreviewDao {
    @Query("SELECT * FROM CosplayPreviewEntity where url IN (:urls) LIMIT 20")
    suspend fun getCosplayPreviews(urls: List<String>): List<CosplayPreviewEntity>

    @Query("SELECT * FROM CosplayPreviewEntity ORDER BY viewed_at DESC LIMIT 20 OFFSET :offset")
    suspend fun getRecentlyViewedCosplays(offset: Int): List<CosplayPreviewEntity>

    @Query("SELECT * FROM CosplayPreviewEntity where url = :url LIMIT 1")
    suspend fun findCosplay(url: String): CosplayPreviewEntity?

    @Insert
    suspend fun insertCosplayPreview(cosplayPreviewEntity: CosplayPreviewEntity): Long

    @Update
    suspend fun updateCosplayPreview(cosplayPreviewEntity: CosplayPreviewEntity)

    @Query("DELETE FROM CosplayPreviewEntity WHERE url = :url")
    suspend fun deleteCosplayPreview(url: String)
}