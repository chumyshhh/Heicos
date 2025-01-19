package com.heicos.presentation.cosplays

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.LinearOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.outlined.Clear
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.DismissibleDrawerSheet
import androidx.compose.material3.DismissibleNavigationDrawer
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.ListItem
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationDrawerItem
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.SearchBar
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.pulltorefresh.PullToRefreshContainer
import androidx.compose.material3.pulltorefresh.rememberPullToRefreshState
import androidx.compose.material3.rememberDrawerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.nestedscroll.nestedScroll
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.isTraversalGroup
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.semantics.traversalIndex
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.navigation.NavController
import com.heicos.R
import com.heicos.domain.util.CosplayMediaType
import com.heicos.domain.util.CosplayType
import com.heicos.presentation.cosplays.types.CosplayTypes
import com.heicos.presentation.destinations.FullCosplayScreenDestination
import com.heicos.presentation.destinations.FullVideoCosplayDestination
import com.heicos.presentation.destinations.SettingsScreenDestination
import com.heicos.presentation.util.CheckBox
import com.heicos.presentation.util.ErrorMessage
import com.heicos.presentation.util.LoadingScreen
import com.heicos.presentation.util.SwipeToDeleteContainer
import com.heicos.presentation.util.getActivity
import com.ramcosta.composedestinations.annotation.Destination
import com.ramcosta.composedestinations.annotation.RootNavGraph
import com.ramcosta.composedestinations.navigation.DestinationsNavigator
import com.ramcosta.composedestinations.result.NavResult
import com.ramcosta.composedestinations.result.ResultRecipient
import kotlinx.coroutines.launch


@OptIn(ExperimentalMaterial3Api::class)
@RootNavGraph(start = true)
@Destination
@Composable
fun CosplaysScreen(
    viewModel: CosplaysScreenViewModel = hiltViewModel(),
    navigator: DestinationsNavigator,
    resultRecipientTag: ResultRecipient<FullCosplayScreenDestination, String>,
    navController: NavController
) {
    var query by remember {
        mutableStateOf(viewModel.searchQuery)
    }

    resultRecipientTag.onNavResult { result ->
        when (result) {
            is NavResult.Canceled -> Unit

            is NavResult.Value -> {
                query = result.value
                viewModel.onEvent(CosplaysScreenEvents.Search(query))
            }
        }
    }

    val scope = rememberCoroutineScope()
    val gridCells = 2
    val state by viewModel.state.collectAsState()

    var page by remember {
        mutableIntStateOf(state.currentPage)
    }

    var searchBarStatus by remember {
        mutableStateOf(false)
    }

    val pullRefreshState = rememberPullToRefreshState()

    if (pullRefreshState.isRefreshing) {
        LaunchedEffect(true) {
            viewModel.onEvent(CosplaysScreenEvents.Refresh)
        }
    }

    LaunchedEffect(state.isRefreshing) {
        if (!state.isRefreshing) {
            pullRefreshState.endRefresh()
        }
    }

    val pattern = remember { Regex("^\\d+\$") }

    val context = LocalContext.current
    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val focusManager = LocalFocusManager.current

    val imageCosplays = listOf(
        CosplayTypes.New,
        CosplayTypes.Ranking,
        CosplayTypes.Recently
    )

    val videoCosplays = listOf(
        CosplayTypes.NewVideo,
        CosplayTypes.RankingVideo
    )

    val imageAsianCosplays = listOf(
        CosplayTypes.NewAsian,
        CosplayTypes.RankingAsian,
        CosplayTypes.RecentlyAsian
    )

    DismissibleNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DismissibleDrawerSheet(
                modifier = Modifier
                    .fillMaxHeight()
                    .verticalScroll(rememberScrollState())
            ) {
                Text(
                    modifier = Modifier
                        .padding(all = 12.dp),
                    text = stringResource(R.string.image_packs)
                )

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                imageCosplays.forEach { cosplay ->
                    NavigationItem(cosplay, state) {
                        if (state.currentCosplayType != cosplay.cosplayType) {
                            page = 1
                            scope.launch { drawerState.close() }
                            query = ""
                            viewModel.onEvent(CosplaysScreenEvents.ChangeCosplayType(cosplay.cosplayType))
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                Text(
                    modifier = Modifier
                        .padding(all = 12.dp),
                    text = stringResource(R.string.video_packs)
                )

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                videoCosplays.forEach { cosplay ->
                    NavigationItem(cosplay, state) {
                        if (state.currentCosplayType != cosplay.cosplayType) {
                            page = 1
                            scope.launch { drawerState.close() }
                            query = ""
                            viewModel.onEvent(CosplaysScreenEvents.ChangeCosplayType(cosplay.cosplayType))
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                Text(
                    modifier = Modifier
                        .padding(all = 12.dp),
                    text = stringResource(R.string.asian_packs)
                )

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                imageAsianCosplays.forEach { cosplay ->
                    NavigationItem(cosplay, state) {
                        if (state.currentCosplayType != cosplay.cosplayType) {
                            page = 1
                            scope.launch { drawerState.close() }
                            query = ""
                            viewModel.onEvent(CosplaysScreenEvents.ChangeCosplayType(cosplay.cosplayType))
                        }
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                Text(
                    modifier = Modifier
                        .padding(all = 12.dp),
                    text = stringResource(R.string.history)
                )

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                NavigationItem(CosplayTypes.RecentlyViewed, state) {
                    if (state.currentCosplayType != CosplayTypes.RecentlyViewed.cosplayType) {
                        page = 1
                        scope.launch { drawerState.close() }
                        query = ""
                        viewModel.onEvent(CosplaysScreenEvents.ChangeCosplayType(CosplayTypes.RecentlyViewed.cosplayType))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                Text(
                    modifier = Modifier
                        .padding(start = 12.dp),
                    text = stringResource(R.string.filter_pages)
                )
                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    IconButton(onClick = {
                        if (page > 1) {
                            page -= 1
                        }
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = null
                        )
                    }
                    OutlinedTextField(
                        modifier = Modifier
                            .weight(1f),
                        value = page.toString(),
                        keyboardActions = KeyboardActions(onDone = { focusManager.clearFocus() }),
                        keyboardOptions = KeyboardOptions(
                            keyboardType = KeyboardType.Number,
                            imeAction = ImeAction.Done
                        ),
                        onValueChange = {
                            if (it.matches(pattern)) {
                                page = it.toInt()
                            }
                        }
                    )
                    IconButton(onClick = {
                        page += 1
                    }) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowForward,
                            contentDescription = null
                        )
                    }
                }
                Spacer(Modifier.height(12.dp))
                Box(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(
                        modifier = Modifier
                            .clickable {
                                page = state.lastPage ?: 0
                            },
                        text = stringResource(id = R.string.last_page, state.lastPage.toString())
                    )
                }

                Spacer(Modifier.height(12.dp))
                Row(
                    modifier = Modifier
                        .padding(horizontal = 12.dp)
                        .fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    TextButton(
                        onClick = {
                            focusManager.clearFocus()
                            page = 1
                            scope.launch { drawerState.close() }
                            viewModel.onEvent(CosplaysScreenEvents.ChangePage(1))
                        }
                    ) {
                        Text(text = stringResource(id = R.string.reset))
                    }
                    TextButton(
                        onClick = {
                            if (state.currentPage != page) {
                                focusManager.clearFocus()
                                scope.launch { drawerState.close() }
                                viewModel.onEvent(CosplaysScreenEvents.ChangePage(page))
                            }
                        }
                    ) {
                        Text(text = stringResource(id = R.string.apply))
                    }
                }

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                Text(
                    modifier = Modifier
                        .padding(start = 12.dp),
                    text = stringResource(R.string.filters)
                )

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                var reverseCheckedState by remember { mutableStateOf(state.reversedMode) }

                CheckBox(
                    modifier = Modifier
                        .padding(horizontal = 12.dp),
                    checkedState = reverseCheckedState,
                    text = stringResource(R.string.check_box_reversed),
                    onClickListener = {
                        reverseCheckedState = !reverseCheckedState
                        viewModel.onEvent(
                            CosplaysScreenEvents.ChangeReversedState(
                                reverseCheckedState
                            )
                        )
                    }
                )
                Spacer(Modifier.height(12.dp))
                var showDownloadedCheckedState by remember { mutableStateOf(state.showDownloaded) }
                CheckBox(
                    modifier = Modifier
                        .padding(horizontal = 12.dp),
                    checkedState = showDownloadedCheckedState,
                    text = stringResource(R.string.check_box_show_downloaded),
                    onClickListener = {
                        showDownloadedCheckedState = !showDownloadedCheckedState
                        viewModel.onEvent(
                            CosplaysScreenEvents.ChangeDownloadedState(
                                showDownloadedCheckedState
                            )
                        )
                    }
                )

                HorizontalDivider(modifier = Modifier.padding(all = 12.dp))

                Box(
                    modifier = Modifier
                ) {
                    TextButton(
                        modifier = Modifier
                            .align(Alignment.BottomCenter),
                        onClick = {
                            navigator.navigate(SettingsScreenDestination)
                        }
                    ) {
                        Row(
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(imageVector = Icons.Outlined.Settings, contentDescription = null)
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(text = stringResource(id = R.string.settings))
                        }
                    }
                }

            }
        }
    ) {
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .semantics { isTraversalGroup = true }
                .nestedScroll(pullRefreshState.nestedScrollConnection),
        ) {
            SearchBar(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .semantics { traversalIndex = -1f },
                query = query,
                onQueryChange = {
                    query = it
                },
                onSearch = {
                    viewModel.onEvent(CosplaysScreenEvents.Search(query))
                    searchBarStatus = false

                },
                active = searchBarStatus,
                onActiveChange = {
                    searchBarStatus = it
                },
                placeholder = {
                    Text(text = stringResource(id = R.string.search))
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Outlined.Search,
                        contentDescription = null
                    )
                },
                trailingIcon = {
                    AnimatedVisibility(
                        visible = query.isNotEmpty(),
                        enter = fadeIn(tween(300)),
                        exit = fadeOut(tween(300))
                    ) {
                        IconButton(
                            onClick = {
                                query = ""
                                viewModel.onEvent(CosplaysScreenEvents.Reset)
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Outlined.Clear,
                                contentDescription = null
                            )
                        }
                    }
                }
            ) {
                LaunchedEffect(Unit) {
                    viewModel.onEvent(CosplaysScreenEvents.LoadSearchQueries)
                }
                when {
                    state.isHistoryLoading -> LoadingScreen()
                    state.isHistoryIsEmpty -> {
                        Box(
                            modifier = Modifier
                                .fillMaxSize()
                        ) {
                            Text(
                                modifier = Modifier
                                    .padding(PaddingValues(top = 144.dp))
                                    .align(Alignment.TopCenter)
                                    .fillMaxWidth(),
                                text = stringResource(id = R.string.empty_queries),
                                textAlign = TextAlign.Center
                            )
                        }
                    }

                    else -> {
                        LazyColumn {
                            items(
                                items = state.history.reversed(),
                                key = { it.id }
                            ) { historyItem ->
                                SwipeToDeleteContainer(
                                    item = historyItem,
                                    onDelete = {
                                        viewModel.onEvent(
                                            CosplaysScreenEvents.DeleteSearchItem(historyItem)
                                        )
                                    }
                                ) { item ->
                                    ListItem(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { query = item.query },
                                        headlineContent = { Text(text = historyItem.query) },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Filled.Refresh,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                            if (state.history.isNotEmpty()) {
                                item {
                                    ListItem(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .clickable { viewModel.onEvent(CosplaysScreenEvents.DeleteHistoryQueries) },
                                        headlineContent = { Text(text = stringResource(id = R.string.clean)) },
                                        leadingContent = {
                                            Icon(
                                                imageVector = Icons.Filled.Clear,
                                                contentDescription = null
                                            )
                                        }
                                    )
                                }
                            }
                        }
                    }
                }
            }

            if (!state.message.isNullOrEmpty()) {
                ErrorMessage(
                    message = state.message!!,
                    onButtonClickListener = {
                        viewModel.onEvent(CosplaysScreenEvents.Refresh)
                    }
                )
            } else {
                if (state.isLoading) {
                    LoadingScreen()
                } else {
                    if (state.isEmpty) {
                        Box(
                            modifier = Modifier
                                .fillMaxSize(),
                            contentAlignment = Alignment.Center
                        ) {
                            Text(text = stringResource(id = R.string.nothing_found))
                        }
                    }

                    LazyVerticalGrid(
                        contentPadding = PaddingValues(top = 72.dp),
                        columns = GridCells.Fixed(gridCells),
                        state = viewModel.gridState
                    ) {
                        items(state.cosplays) { cosplay ->
                            CosplayScreenItem(
                                cosplay = cosplay,
                                navController = navController,
                                onItemClickListener = {
                                    if (cosplay.type is CosplayMediaType.Video) {
                                        navigator.navigate(
                                            FullVideoCosplayDestination(
                                                cosplayPreview = cosplay
                                            )
                                        )
                                    } else {
                                        navigator.navigate(
                                            FullCosplayScreenDestination(
                                                cosplayPreview = cosplay
                                            )
                                        )
                                    }
                                },
                                onItemLongClickListener = {
                                    viewModel.onEvent(CosplaysScreenEvents.DeleteCosplayPreview(cosplay))
                                }
                            )
                        }
                        item(
                            span = { GridItemSpan(gridCells) }
                        ) {
                            when {
                                !state.nextDataMessage.isNullOrEmpty() -> {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 36.dp, bottom = 36.dp)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        Text(text = state.nextDataMessage ?: "")
                                    }
                                }

                                state.nextDataIsLoading -> {
                                    Box(
                                        modifier = Modifier
                                            .padding(top = 36.dp, bottom = 36.dp)
                                            .fillMaxWidth(),
                                        contentAlignment = Alignment.Center
                                    ) {
                                        CircularProgressIndicator()
                                    }
                                }

                                else -> {
                                    if (!state.nextDataIsEmpty) {
                                        SideEffect {
                                            viewModel.onEvent(CosplaysScreenEvents.LoadNextData)
                                        }
                                    }
                                }
                            }
                        }
                    }
                }

            }
            val scaleFraction = if (pullRefreshState.isRefreshing) 1f else
                LinearOutSlowInEasing.transform(pullRefreshState.progress).coerceIn(0f, 1f)
            PullToRefreshContainer(
                modifier = Modifier
                    .align(Alignment.TopCenter)
                    .padding(top = 72.dp)
                    .graphicsLayer(scaleX = scaleFraction, scaleY = scaleFraction),
                state = pullRefreshState,
                containerColor = MaterialTheme.colorScheme.background,
                contentColor = MaterialTheme.colorScheme.primary
            )
        }
    }

    BackHandler {
        if (drawerState.isOpen) {
            scope.launch {
                drawerState.close()
            }
            return@BackHandler
        }
        if (searchBarStatus) {
            searchBarStatus = false
            return@BackHandler
        }
        if (query.isNotEmpty()) {
            query = ""
            viewModel.onEvent(CosplaysScreenEvents.Reset)
            return@BackHandler
        }
        if (state.currentCosplayType !== CosplayType.New) {
            viewModel.onEvent(CosplaysScreenEvents.ChangeCosplayType(CosplayType.New))
            return@BackHandler
        }

        context.getActivity()?.finish()
    }
}

@Composable
private fun NavigationItem(
    cosplay: CosplayTypes,
    state: CosplaysScreenState,
    onClickListener: () -> Unit
) {
    val selected = cosplay.cosplayType == state.currentCosplayType
    NavigationDrawerItem(
        modifier = Modifier.padding(horizontal = 12.dp),
        icon = {
            Icon(
                imageVector = if (selected) {
                    cosplay.selectedIcon
                } else {
                    cosplay.icon
                },
                contentDescription = null
            )
        },
        label = { Text(text = stringResource(id = cosplay.title)) },
        selected = selected,
        onClick = {
            onClickListener()
        },
    )
}


