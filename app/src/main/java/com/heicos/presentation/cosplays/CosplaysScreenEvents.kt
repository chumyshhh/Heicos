package com.heicos.presentation.cosplays

import com.heicos.domain.model.CosplayPreview
import com.heicos.domain.model.SearchQuery
import com.heicos.domain.util.CosplayType

sealed class CosplaysScreenEvents {
    data class Search(val query: String) : CosplaysScreenEvents()
    data class ChangeCosplayType(val type: CosplayType) : CosplaysScreenEvents()
    data class DeleteSearchItem(val searchItem: SearchQuery) : CosplaysScreenEvents()
    data class ChangePage(val page: Int) : CosplaysScreenEvents()
    data class ChangeReversedState(val state: Boolean) : CosplaysScreenEvents()
    data class ChangeDownloadedState(val state: Boolean) : CosplaysScreenEvents()
    data class DeleteCosplayPreview(val cosplayPreview: CosplayPreview) : CosplaysScreenEvents()
    data object LoadSearchQueries : CosplaysScreenEvents()
    data object Reset : CosplaysScreenEvents()
    data object Refresh : CosplaysScreenEvents()
    data object LoadNextData : CosplaysScreenEvents()
    data object DeleteHistoryQueries : CosplaysScreenEvents()
}