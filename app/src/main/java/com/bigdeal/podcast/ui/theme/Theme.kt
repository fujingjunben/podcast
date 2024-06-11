

package com.bigdeal.podcast.ui.theme

import androidx.compose.runtime.Composable
import com.google.samples.apps.nowinandroid.core.designsystem.theme.NiaTheme

@Composable
fun PodcastTheme(
    content: @Composable () -> Unit
) {

    NiaTheme(content = content)
}
