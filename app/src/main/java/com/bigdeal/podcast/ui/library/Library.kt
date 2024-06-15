package com.bigdeal.podcast.ui.library

import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.IconButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel


@Composable
fun Library(
    modifier: Modifier = Modifier,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val contentResolver = LocalContext.current.contentResolver
    FilePickerScreen(
        onSelectFile = { uri ->
            viewModel.importOpml(contentResolver, uri)
        }
    )
}

@Composable
fun FilePickerScreen(
    onSelectFile: (uri: Uri) -> Unit,
) {
    // Create a launcher for the activity result
    val filePickerLauncher = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.StartActivityForResult()
    ) { result ->
        if (result.resultCode == Activity.RESULT_OK) {
            result.data?.data?.let { uri ->
                onSelectFile(uri)
            }
        }
    }

    // Function to launch the file picker
    fun openFilePicker() {
        val intent = Intent(Intent.ACTION_OPEN_DOCUMENT).apply {
            addCategory(Intent.CATEGORY_OPENABLE)
            type = "*/*"
        }
        filePickerLauncher.launch(intent)
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .clickable {
                openFilePicker()
            },
        contentAlignment = Alignment.Center
    ) {
        Text(text = "Click to pick opml file")
    }
}