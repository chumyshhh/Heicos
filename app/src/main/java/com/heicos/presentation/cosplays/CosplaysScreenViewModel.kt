package com.heicos.presentation.cosplays

import androidx.compose.foundation.gestures.stopScroll
import androidx.compose.foundation.lazy.grid.LazyGridState
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.heicos.domain.model.SearchQuery
import com.heicos.domain.use_case.DeleteAllSearchQueriesUseCase
import com.heicos.domain.use_case.DeleteCosplayPreviewUseCase
import com.heicos.domain.use_case.DeleteSearchQueryByIdUseCase
import com.heicos.domain.use_case.GetCosplaysLastPageUseCase
import com.heicos.domain.use_case.GetCosplaysUseCase
import com.heicos.domain.use_case.GetSearchQueriesUseCase
import com.heicos.domain.use_case.UpsertSearchQueryUseCase
import com.heicos.domain.util.CosplayType
import com.heicos.utils.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject


@HiltViewModel
class CosplaysScreenViewModel @Inject constructor(
    private val getCosplaysUseCase: GetCosplaysUseCase,
    private val getCosplaysLastPageUseCase: GetCosplaysLastPageUseCase,
    private val getSearchQueriesUseCase: GetSearchQueriesUseCase,
    private val upsertSearchQueryUseCase: UpsertSearchQueryUseCase,
    private val deleteSearchQueryByIdUseCase: DeleteSearchQueryByIdUseCase,
    private val deleteAllSearchQueriesUseCase: DeleteAllSearchQueriesUseCase,
    private val deleteCosplayPreviewUseCase: DeleteCosplayPreviewUseCase
) : ViewModel() {

    private var isSearching = false

    private val _state = MutableStateFlow(CosplaysScreenState(isLoading = true))
    val state = _state.asStateFlow()

    var searchQuery = ""
    var gridState = LazyGridState()

    init {
        loadNextCosplays()
    }

    fun onEvent(event: CosplaysScreenEvents) {
        when (event) {
            CosplaysScreenEvents.LoadNextData -> {
                loadNextCosplays(loadingNextData = true)
            }

            CosplaysScreenEvents.Reset -> {
                if (searchQuery.isEmpty())
                    return

                resetValues()
                loadNextCosplays()
            }

            CosplaysScreenEvents.Refresh -> {
                _state.value = _state.value.copy(
                    isRefreshing = true,
                    nextPage = _state.value.currentPage
                )
                resetValues(isRefreshing = true)
                loadNextCosplays()
            }

            is CosplaysScreenEvents.Search -> {
                resetValues()
                searchQuery = event.query
                isSearching = true
                val queryContains = _state.value.history.find { searchQuery ->
                    searchQuery.query == event.query
                }
                if (queryContains == null) {
                    val searchQuery = SearchQuery(query = event.query)
                    viewModelScope.launch {
                        upsertSearchQueryUseCase(searchQuery)
                    }
                }
                loadNextCosplays()
            }

            is CosplaysScreenEvents.DeleteHistoryQueries -> {
                viewModelScope.launch {
                    deleteAllSearchQueriesUseCase()
                    clearHistoryQueries()
                }
            }

            is CosplaysScreenEvents.DeleteSearchItem -> {
                viewModelScope.launch {
                    deleteSearchQueryByIdUseCase(event.searchItem)
                    clearHistoryQueries()
                }
            }

            is CosplaysScreenEvents.ChangeCosplayType -> {
                resetValues()
                _state.value = _state.value.copy(
                    currentPage = DEFAULT_PAGE,
                    nextPage = DEFAULT_PAGE + 1,
                    currentCosplayType = event.type,
                    reversedMode = false
                )
                loadNextCosplays()
            }

            is CosplaysScreenEvents.ChangePage -> {
                resetValues()
                _state.value = _state.value.copy(
                    isLoading = true,
                    currentPage = event.page,
                    nextPage = event.page
                )
                loadNextCosplays()
            }

            is CosplaysScreenEvents.ChangeReversedState -> {
                _state.value = _state.value.copy(
                    reversedMode = event.state
                )
            }

            CosplaysScreenEvents.LoadSearchQueries -> {
                loadSearchQueries()
            }

            is CosplaysScreenEvents.ChangeDownloadedState -> {
                resetValues(true)

                _state.value = _state.value.copy(
                    showDownloaded = event.state
                )

                loadNextCosplays()
            }

            is CosplaysScreenEvents.DeleteCosplayPreview -> {
                viewModelScope.launch {
                    deleteCosplayPreviewUseCase(event.cosplayPreview)
                }
            }
        }
    }

    private fun loadNextCosplays(loadingNextData: Boolean = false) {
        viewModelScope.launch(Dispatchers.IO) {
            val remoteData = getCosplaysUseCase(
                page = if (loadingNextData) _state.value.nextPage else _state.value.currentPage,
                cosplayType = if (isSearching) CosplayType.Search(searchQuery) else _state.value.currentCosplayType,
                showDownloaded = _state.value.showDownloaded
            )

            remoteData.collect { result ->
                when (result) {
                    is Resource.Error -> {
                        if (_state.value.cosplays.isNotEmpty()) {
                            _state.value = _state.value.copy(nextDataMessage = result.message)
                        } else {
                            _state.value = _state.value.copy(message = result.message)
                        }
                    }

                    is Resource.Loading -> {
                        if (loadingNextData && result.isLoading) {
                            _state.value = _state.value.copy(
                                isLoading = false,
                                nextDataIsLoading = true,
                                message = null
                            )
                        }
                    }

                    is Resource.Success -> {
                        result.data?.let { data ->
                            _state.value = if (data.isNotEmpty()) {
                                _state.value.copy(
                                    nextPage = if (_state.value.reversedMode)
                                        _state.value.nextPage - 1 else _state.value.nextPage + 1,
                                    isLoading = false,
                                    isRefreshing = false,
                                    nextDataIsLoading = false,
                                    cosplays = _state.value.cosplays + data,
                                    message = null
                                )
                            } else {
                                _state.value.copy(
                                    isLoading = false,
                                    isRefreshing = false,
                                    isEmpty = _state.value.cosplays.isEmpty(),
                                    nextDataIsLoading = false,
                                    nextDataIsEmpty = true,
                                    message = null
                                )
                            }
                        }
                        if (_state.value.lastPage == null || _state.value.lastPage == 0) {
                            loadLastPage()
                        }
                    }
                }
            }
        }
    }

    private fun resetValues(isRefreshing: Boolean = false) {
        if (gridState.isScrollInProgress) {
            viewModelScope.launch { gridState.stopScroll() }
        }

        _state.value = _state.value.copy(
            currentPage = DEFAULT_PAGE,
            nextPage = DEFAULT_PAGE + 1,
            isLoading = true,
            isEmpty = false,
            nextDataIsEmpty = false,
            message = null,
            nextDataMessage = null,
            cosplays = emptyList()
        )

        if (!isRefreshing) {
            isSearching = false
            searchQuery = ""
        }

        gridState = LazyGridState()
    }

    private fun loadSearchQueries() {
        viewModelScope.launch {
            val queries = getSearchQueriesUseCase()
            queries.collect { searchQuery ->
                if (searchQuery.isEmpty()) {
                    _state.value = _state.value.copy(
                        isHistoryLoading = false,
                        isHistoryIsEmpty = searchQuery.isEmpty()
                    )
                } else {
                    _state.value = _state.value.copy(
                        isHistoryLoading = false,
                        isHistoryIsEmpty = false,
                        history = searchQuery
                    )
                }
            }
        }
    }

    private fun loadLastPage() {
        viewModelScope.launch {
            val page = getCosplaysLastPageUseCase()
            _state.value = _state.value.copy(
                lastPage = page
            )
        }
    }

    private fun clearHistoryQueries() {
        _state.value = _state.value.copy(
            isHistoryLoading = false,
            isHistoryIsEmpty = true,
            history = emptyList()
        )
    }

    companion object {
        const val DEFAULT_PAGE = 1
    }
}