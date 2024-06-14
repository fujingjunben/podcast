package com.bigdeal.podcast.ui.favourite

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.core.animateDpAsState
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyItemScope
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Check
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.onClick
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.paging.LoadState
import androidx.paging.compose.LazyPagingItems
import androidx.paging.compose.collectAsLazyPagingItems
import coil.compose.AsyncImage
import com.bigdeal.core.data.EpisodeEntity
import com.bigdeal.core.data.EpisodeToPodcast
import com.bigdeal.core.data.FollowedEpisodesToPodcast
import com.bigdeal.podcast.R
import com.bigdeal.core.data.Podcast
import com.bigdeal.core.data.model.EpisodeWithPodcast
import com.bigdeal.podcast.core.model.EpisodeOfPodcast
import com.bigdeal.podcast.core.player.EpisodePlayerState
import com.bigdeal.podcast.core.player.model.PlayerEpisode
import com.bigdeal.podcast.core.player.model.toPlayerEpisode
import com.bigdeal.podcast.ui.common.EpisodeActions
import com.bigdeal.podcast.ui.common.EpisodeListItem

@Composable
fun Favourite(
    modifier: Modifier,
    navigateToEpisode: (String, String) -> Unit,
    navigateToPodcast: (String) -> Unit,
    onPlay: (String) -> Unit,
    viewModel: FavouriteViewModel = hiltViewModel()
) {
    val followedEpisodes = viewModel.followedEpisodes.collectAsLazyPagingItems()
    val playState = viewModel.episodePlayerState.collectAsState()
    val followedPodcasts = viewModel.followedPodcasts.collectAsState()

    Column(
        modifier = modifier
            .systemBarsPadding()
    ) {
        EpisodeList(
            episodePlayerState = playState.value,
            episodeWithPodcastsPagingItems = followedEpisodes,
            navigateToEpisode,
            showSummary = false,
            episodeActions = EpisodeActions(
                onPlay = { playerEpisode ->
                    run {
                        onPlay(playerEpisode.id)
                        viewModel.play(playerEpisode)
                    }
                },
                onPause = viewModel::pause,
                onAddToQueue = { playerEpisode ->
                    run {
                        viewModel.addToQueue(playerEpisode)
                    }
                },
                onDownload = viewModel::download,
                onCancelDownload = viewModel::cancelDownload,
                onDeleteDownload = viewModel::deleteDownload,
            )
        ) {
            FollowedPodcasts(
                podcasts = followedPodcasts.value,
                navigateToPodcast = navigateToPodcast,
                onPodcastUnfollowed = viewModel::onPodcastUnfollowed,
                onFollow = { url -> viewModel.addFeed(url) },
            )
        }
    }
}


@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavouriteAppBar(
    onFollow: (String) -> Unit, backgroundColor: Color, modifier: Modifier = Modifier
) {

    val feedState = remember { mutableStateOf("") }
    val showDialog = remember {
        mutableStateOf(false)
    }
    TopAppBar(title = {
        Row {
            Image(
                painter = painterResource(R.drawable.ic_logo), contentDescription = null
            )
        }
    }, actions = {
        CompositionLocalProvider(LocalContentColor provides MaterialTheme.colorScheme.onSurfaceVariant) {
            IconButton(onClick = { showDialog.value = true }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_add)
                )
            }
        }
    }, modifier = modifier
    )
    if (showDialog.value) {
        AlertDialogExample(
            onDismissRequest = { showDialog.value = false },
            onConfirmation = {
                if (feedState.value.isNotEmpty()) {
                    onFollow(feedState.value)
                    showDialog.value = false
                }
            },
            value = feedState.value,
            onValueChange = { value -> feedState.value = value },
            dialogTitle = "订阅播客",
            dialogText = "请输入播客地址",
            icon = Icons.Filled.Podcasts

        )
    }
}

@Composable
fun FollowedPodcasts(
    modifier: Modifier = Modifier,
    podcasts: List<Podcast>,
    navigateToPodcast: (String) -> Unit,
    onPodcastUnfollowed: (String) -> Unit,
    onFollow: (String) -> Unit,
) {
    val feedState = remember { mutableStateOf("") }
    val showDialog = remember {
        mutableStateOf(false)
    }
    if (showDialog.value) {
        AlertDialogExample(
            onDismissRequest = { showDialog.value = false },
            onConfirmation = {
                if (feedState.value.isNotEmpty()) {
                    onFollow(feedState.value)
                    showDialog.value = false
                }
            },
            value = feedState.value,
            onValueChange = { value -> feedState.value = value },
            dialogTitle = stringResource(id = R.string.cd_add_feed_dialog_title),
            dialogText = stringResource(id = R.string.cd_add_feed_dialog_content),
            icon = Icons.Filled.Podcasts

        )
    }
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Text(text = stringResource(id = R.string.cd_follow))
            IconButton(onClick = { showDialog.value = true }) {
                Icon(
                    imageVector = Icons.Filled.Add,
                    contentDescription = stringResource(R.string.cd_add)
                )
            }
        }
        HorizontalDivider(modifier = modifier)

        LazyRow(modifier = modifier.height(120.dp)) {
            items(podcasts, key = { podcast -> podcast.id }) { podcast ->
                FollowedPodcastCarouselItem(
                    podcastImageUrl = podcast.imageUrl,
                    podcastTitle = podcast.title,
                    onUnfollowedClick = { onPodcastUnfollowed(podcast.id) },
                    navigateToPodcast = { navigateToPodcast(podcast.id) },
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxHeight()
                )
            }
        }
        HorizontalDivider(modifier = modifier)
    }
}

@Composable
private fun FollowedPodcastCarouselItem(
    modifier: Modifier = Modifier,
    podcastImageUrl: String? = null,
    podcastTitle: String? = null,
    navigateToPodcast: () -> Unit,
    onUnfollowedClick: () -> Unit,
) {
    Column(
        modifier.padding(horizontal = 12.dp, vertical = 8.dp)
    ) {
        Box(
            Modifier
                .weight(1f)
                .align(Alignment.CenterHorizontally)
                .aspectRatio(1f)
        ) {
            if (podcastImageUrl != null) {
                AsyncImage(model = podcastImageUrl,
                    contentDescription = podcastTitle,
                    contentScale = ContentScale.Crop,
                    modifier = Modifier
                        .fillMaxSize()
                        .clip(MaterialTheme.shapes.medium)
                        .clickable { navigateToPodcast() })
            }

            ToggleFollowPodcastIconButton(
                onClick = onUnfollowedClick,
                isFollowed = true, /* All podcasts are followed in this feed */
                modifier = Modifier.align(Alignment.BottomEnd)
            )
        }
    }
}

@Composable
private fun ToggleFollowPodcastIconButton(
    onClick: () -> Unit,
    isFollowed: Boolean = true,
    modifier: Modifier = Modifier
) {
    val clickLabel = stringResource(if (isFollowed) R.string.cd_unfollow else R.string.cd_follow)
    IconButton(
        onClick = onClick,
        modifier = modifier.semantics {
            onClick(label = clickLabel, action = null)
        }
    ) {
        Icon(
            // TODO: think about animating these icons
            imageVector = when {
                isFollowed -> Icons.Default.Check
                else -> Icons.Default.Add
            },
            contentDescription = when {
                isFollowed -> stringResource(R.string.cd_following)
                else -> stringResource(R.string.cd_not_following)
            },
            tint = animateColorAsState(
                when {
                    isFollowed -> MaterialTheme.colorScheme.onPrimary
                    else -> MaterialTheme.colorScheme.primary
                }
            ).value,
            modifier = Modifier
                .shadow(
                    elevation = animateDpAsState(if (isFollowed) 0.dp else 1.dp).value,
                    shape = MaterialTheme.shapes.small
                )
                .background(
                    color = animateColorAsState(
                        when {
                            isFollowed -> MaterialTheme.colorScheme.primary
                            else -> MaterialTheme.colorScheme.surfaceContainerHighest
                        }
                    ).value,
                    shape = CircleShape
                )
                .padding(4.dp)
        )
    }
}



@Composable
fun AlertDialogExample(
    onDismissRequest: () -> Unit,
    onConfirmation: () -> Unit,
    value: String,
    onValueChange: (String) -> Unit,
    dialogTitle: String,
    dialogText: String,
    icon: ImageVector,
) {
    AlertDialog(icon = {
        Icon(icon, contentDescription = "Example Icon")
    }, title = {
        Text(text = dialogTitle)
    }, text = {
        Column(horizontalAlignment = Alignment.CenterHorizontally){
            Text(text = dialogText)
            TextField(
                value = value, onValueChange = onValueChange
            )
        }
    }, onDismissRequest = {
        onDismissRequest()
    }, confirmButton = {
        TextButton(onClick = {
            onConfirmation()
        }) {
            Text("Confirm")
        }
    }, dismissButton = {
        TextButton(onClick = {
            onDismissRequest()
        }) {
            Text("Dismiss")
        }
    })
}

@Composable
fun EpisodeList(
    episodePlayerState: EpisodePlayerState,
    episodeWithPodcastsPagingItems: LazyPagingItems<EpisodeWithPodcast>,
    navigateToEpisode: (String, String) -> Unit,
    episodeActions: EpisodeActions,
    modifier: Modifier = Modifier,
    showPodcastImage: Boolean = true,
    showSummary: Boolean = true,
    header: @Composable LazyItemScope.() -> Unit = {},
) {
    LazyColumn(
        modifier = modifier,
        contentPadding = PaddingValues(0.dp),
        verticalArrangement = Arrangement.Center
    ) {
        item {
            header(this)
        }
        items(episodeWithPodcastsPagingItems.itemCount) { index ->
            val episodeWithPodcast = episodeWithPodcastsPagingItems[index]
            episodeWithPodcast?.let {item ->
                EpisodeListItem(
                    episodePlayerState = episodePlayerState,
                    playerEpisode = item.toPlayerEpisode(),
                    onClick = navigateToEpisode,
                    onPlay = { episodeActions.onPlay(item.toPlayerEpisode()) },
                    onPause = episodeActions.onPause,
                    onAddToQueue = { episodeActions.onAddToQueue(item.toPlayerEpisode()) },
                    onDownload = { episodeActions.onDownload(item.toPlayerEpisode()) },
                    onCancelDownload = { episodeActions.onCancelDownload(item.toPlayerEpisode()) },
                    showPodcastImage = showPodcastImage,
                    showSummary = showSummary,
                    modifier = Modifier.fillParentMaxWidth()
                )
            }
        }

        episodeWithPodcastsPagingItems.apply {
            when {
                loadState.refresh is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(modifier = Modifier.fillParentMaxSize())
                    }
                }
                loadState.append is LoadState.Loading -> {
                    item {
                        CircularProgressIndicator(modifier = Modifier.padding(16.dp))
                    }
                }
                loadState.append is LoadState.Error -> {
                    item {
                        Text(text = "Error loading data")
                    }
                }
            }
        }

    }
}