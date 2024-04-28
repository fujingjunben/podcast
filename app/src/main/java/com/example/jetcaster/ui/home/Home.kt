/*
 * Copyright 2020 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.example.jetcaster.ui.home

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.material.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountCircle
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.jetcaster.R
import com.example.jetcaster.ui.home.discover.Discover
import com.google.accompanist.pager.ExperimentalPagerApi

@Composable
fun Home(
    modifier: Modifier,
    navigateToEpisode: (String, String) -> Unit,
    navigateToPodcast: (String) -> Unit,
    viewModel: HomeViewModel = viewModel()
) {
    HomeContent(
        navigateToEpisode = navigateToEpisode,
        navigateToPodcast = navigateToPodcast,
        modifier = modifier
            .systemBarsPadding()
            .navigationBarsPadding(),
        refresh = viewModel::forceRefresh
    )
}


@Composable
fun HomeContent(
    modifier: Modifier = Modifier,
    navigateToEpisode: (String, String) -> Unit,
    navigateToPodcast: (String) -> Unit,
    refresh: () -> Unit,
) {
    Column(
        modifier = modifier
    ) {
        val appBarColor = MaterialTheme.colors.surface.copy(alpha = 0.87f)
        HomeAppBar(
            backgroundColor = appBarColor,
            modifier = Modifier.fillMaxWidth(),
            refresh
        )

        Discover(
            navigateToEpisode = navigateToEpisode,
            navigateToPodcast = navigateToPodcast,
            Modifier
                .fillMaxWidth()
                .weight(1f)
        )
    }
}

@Composable
fun HomeAppBar(
    backgroundColor: Color,
    modifier: Modifier = Modifier,
    refresh: () -> Unit
) {
    TopAppBar(
        title = {
            Row {
                Image(
                    painter = painterResource(R.drawable.ic_logo),
                    contentDescription = null
                )
                Icon(
                    painter = painterResource(R.drawable.ic_text_logo),
                    contentDescription = stringResource(R.string.app_name),
                    modifier = Modifier
                        .padding(start = 4.dp)
                        .heightIn(max = 24.dp)
                )
            }
        },
        backgroundColor = backgroundColor,
        actions = {
            CompositionLocalProvider(LocalContentAlpha provides ContentAlpha.medium) {
                IconButton(
                    onClick = { refresh() }
                ) {
                    Icon(
                        imageVector = Icons.Filled.Refresh,
                        contentDescription = stringResource(R.string.cd_search)
                    )
                }
                IconButton(
                    onClick = { /* TODO: Open account? */ }
                ) {
                    Icon(
                        imageVector = Icons.Default.AccountCircle,
                        contentDescription = stringResource(R.string.cd_account)
                    )
                }
            }
        },
        modifier = modifier
    )
}
