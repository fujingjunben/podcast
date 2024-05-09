package com.bigdeal.podcast.ui.v2.favourite

import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Podcasts
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import com.bigdeal.podcast.R
import com.bigdeal.core.data.Podcast
import com.bigdeal.podcast.ui.v2.common.EpisodeList

@Composable
fun Favourite(
    modifier: Modifier,
    navigateToEpisode: (String, String) -> Unit,
    navigateToPodcast: (String) -> Unit,
    viewModel: FavouriteViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    Column(
        modifier = modifier.systemBarsPadding()
    ) {
        val appBarColor = MaterialTheme.colorScheme.surface.copy(alpha = 0.87f)
        FavouriteAppBar(
            onFollow = { url -> viewModel.addFeed(url) },
            backgroundColor = appBarColor,
            modifier = Modifier.fillMaxWidth(),
        )

        when (uiState) {
            is FavouriteUiState.Loading -> {
                Box(
                    modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center
                ) {
                    Icon(imageVector = Icons.Filled.Refresh, contentDescription = null)
                }
            }

            is FavouriteUiState.Success -> {
                val episodeOfPodcasts = (uiState as FavouriteUiState.Success).episodeOfPodcasts

                EpisodeList(
                    episodeOfPodcasts,
                    navigateToEpisode,
                ) {
                    FollowedPodcasts(
                        podcasts = episodeOfPodcasts.map { it.podcast }
                        .distinctBy { it.uri },
                        navigateToPodcast = navigateToPodcast,
                        onPodcastUnfollowed = viewModel::onPodcastUnfollowed
                    )
                }
            }
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
            Icon(
                painter = painterResource(R.drawable.ic_text_logo),
                contentDescription = stringResource(R.string.app_name),
                modifier = Modifier
                    .padding(start = 4.dp)
                    .heightIn(max = 24.dp)
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
            IconButton(onClick = { /* TODO: Open account? */ }) {
                Icon(
                    imageVector = Icons.Default.AccountCircle,
                    contentDescription = stringResource(R.string.cd_account)
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
) {
    Column(modifier = modifier) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 12.dp, vertical = 8.dp),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text(text = stringResource(id = R.string.follow))
            Text(text = stringResource(id = R.string.more))
        }
        LazyRow(modifier = modifier.height(100.dp)) {
            items(podcasts, key = { podcast -> podcast.uri }) { podcast ->
                FollowedPodcastCarouselItem(
                    podcastImageUrl = podcast.imageUrl,
                    podcastTitle = podcast.title,
                    onUnfollowedClick = { onPodcastUnfollowed(podcast.uri) },
                    navigateToPodcast = { navigateToPodcast(podcast.uri) },
                    modifier = Modifier
                        .padding(4.dp)
                        .fillMaxHeight()
                )
            }
        }
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

//            ToggleFollowPodcastIconButton(
//                onClick = onUnfollowedClick,
//                isFollowed = true, /* All podcasts are followed in this feed */
//                modifier = Modifier.align(Alignment.BottomEnd)
//            )
        }
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
        Column {
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