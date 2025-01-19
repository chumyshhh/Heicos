package com.heicos.data.mapper

import com.heicos.data.database.entities.CosplayPreviewEntity
import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.util.CosplayMediaType
import com.heicos.utils.time.convertTime

fun CosplayPreview.toCosplayPreviewEntity(
    time: Long?,
    isDownloaded: Boolean
): CosplayPreviewEntity {
    return CosplayPreviewEntity(
        id = id,
        name = title,
        downloadedAt = if (isDownloaded) time else this.downloadedAt,
        url = pageUrl,
        previewUrl = previewUrl,
        storyPageUrl = storyPageUrl,
        viewedAt = time,
        type = if (type is CosplayMediaType.Images) CosplayMediaType.IMAGES else CosplayMediaType.VIDEO
    )
}

fun CosplayPreviewEntity.toCosplayPreview(date: String = ""): CosplayPreview {
    return CosplayPreview(
        id = id,
        pageUrl = url,
        storyPageUrl = storyPageUrl,
        previewUrl = previewUrl,
        title = name,
        date = date,
        isDownloaded = downloadedAt != null,
        downloadedAt = downloadedAt,
        downloadTime = downloadedAt?.let { convertTime(it) },
        isViewed = viewedAt != null,
        type = if (type == CosplayMediaType.IMAGES) CosplayMediaType.Images else CosplayMediaType.Video
    )
}