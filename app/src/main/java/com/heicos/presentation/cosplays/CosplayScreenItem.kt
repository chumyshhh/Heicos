package com.heicos.presentation.cosplays

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ExperimentalFoundationApi
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.material3.Card
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.res.vectorResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.navigation.NavController
import coil.compose.SubcomposeAsyncImage
import coil.request.ImageRequest
import com.heicos.R
import com.heicos.domain.model.CosplayPreview
import com.heicos.presentation.util.IS_DOWNLOADED
import com.heicos.presentation.util.IS_VIEWED
import com.heicos.presentation.util.USER_AGENT_MOZILLA

@OptIn(ExperimentalFoundationApi::class)
@Composable
fun CosplayScreenItem(
    modifier: Modifier = Modifier,
    cosplay: CosplayPreview,
    navController: NavController,
    onItemClickListener: () -> Unit,
    onItemLongClickListener: () -> Unit
) {
    var isExpanded by rememberSaveable { mutableStateOf(false) }

    var isViewed by rememberSaveable {
        mutableStateOf(cosplay.isViewed)
    }

    var isDownloaded by rememberSaveable {
        mutableStateOf(cosplay.isDownloaded)
    }

    isViewed =
        navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(cosplay.title + IS_VIEWED) == true

    isDownloaded =
        navController.currentBackStackEntry?.savedStateHandle?.get<Boolean>(cosplay.title + IS_DOWNLOADED) == true

    Card(
        modifier = modifier
            .padding(4.dp)
            .combinedClickable(
                onClick = {
                    onItemClickListener()
                },
                onLongClick = {
                    isExpanded = !isExpanded
                }
            )
            .fillMaxWidth(),
        shape = RoundedCornerShape(5.dp)
    ) {
        Box(
            modifier = Modifier
                .height(275.dp)
        ) {
            SubcomposeAsyncImage(
                modifier = Modifier
                    .fillMaxSize(),
                model = ImageRequest.Builder(LocalContext.current)
                    .data(cosplay.previewUrl)
                    .addHeader("User-Agent", USER_AGENT_MOZILLA)
                    .crossfade(true)
                    .build(),
                contentDescription = cosplay.title,
                contentScale = ContentScale.Crop,
                loading = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                },
                error = {
                    Box(
                        modifier = Modifier
                            .fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        Text(text = stringResource(id = R.string.error_message))
                    }
                }
            )

            AnimatedContent(
                modifier = Modifier
                    .padding(top = 16.dp)
                    .align(Alignment.TopCenter),
                targetState = isExpanded,
                label = "cosplaysListSize",
                transitionSpec = { fadeIn() togetherWith fadeOut() }
            ) { expanded ->
                if (expanded) {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black
                                    )
                                )
                            ),
                        contentAlignment = Alignment.Center
                    ) {
                        IconButton(
                            onClick = {
                                isViewed = false
                                isDownloaded = false
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = cosplay.title + IS_VIEWED,
                                    value = false
                                )
                                navController.currentBackStackEntry?.savedStateHandle?.set(
                                    key = cosplay.title + IS_DOWNLOADED,
                                    value = false
                                )
                                cosplay.isViewed = false
                                cosplay.isDownloaded = false
                                onItemLongClickListener()
                            }
                        ) {
                            Icon(
                                imageVector = Icons.Filled.Delete,
                                contentDescription = null,
                                tint = Color.Red.copy(alpha = 0.7f)
                            )
                        }
                    }
                } else {
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black
                                    ),
                                    startY = 450f
                                )
                            )
                    )
                }
            }

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .align(Alignment.BottomStart),
                text = cosplay.title,
                textAlign = TextAlign.Center,
                maxLines = 2,
                color = Color.White
            )

            /*androidx.compose.animation.AnimatedVisibility(
                visible = isViewed || cosplay.isViewed,
                enter = fadeIn(),
                exit = fadeOut(),
                content = {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.TopStart)
                            .padding(top = 4.dp, start = 4.dp),
                        imageVector = ImageVector.vectorResource(R.drawable.is_viewed_eye),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            )

            androidx.compose.animation.AnimatedVisibility(
                visible = isDownloaded || cosplay.isDownloaded,
                enter = fadeIn(),
                exit = fadeOut(),
                content = {
                    Icon(
                        modifier = Modifier
                            .align(Alignment.TopEnd)
                            .padding(top = 4.dp, end = 4.dp)
                            .background(
                                color = MaterialTheme.colorScheme.background.copy(alpha = 0.35F),
                                shape = CircleShape
                            ),
                        imageVector = ImageVector.vectorResource(R.drawable.download_icon),
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.secondary
                    )
                }
            )*/

            if (isDownloaded || cosplay.isDownloaded) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 4.dp, end = 4.dp)
                        .background(
                            color = MaterialTheme.colorScheme.background.copy(alpha = 0.35F),
                            shape = CircleShape
                        ),
                    imageVector = ImageVector.vectorResource(R.drawable.download_icon),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }

            if (isViewed || cosplay.isViewed) {
                Icon(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .padding(top = 4.dp, start = 4.dp),
                    imageVector = ImageVector.vectorResource(R.drawable.is_viewed_eye),
                    contentDescription = null,
                    tint = MaterialTheme.colorScheme.secondary
                )
            }
        }
    }
}