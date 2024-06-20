package com.bigdeal.podcast.ui.library

import android.Manifest
import android.app.Activity
import android.content.Intent
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.material3.Button
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SnackbarHost
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import com.bigdeal.podcast.ui.common.RequestPermissionScreen
import kotlinx.coroutines.launch


@Composable
fun Library(
    modifier: Modifier = Modifier,
    navigateToDiscover: () -> Unit,
    viewModel: LibraryViewModel = hiltViewModel()
) {
    val contentResolver = LocalContext.current.contentResolver
    var showPicker by remember {
        mutableStateOf(false)
    }
    val snackbarHostState = remember { SnackbarHostState() }
    val scope = rememberCoroutineScope()

    Scaffold(
        snackbarHost = { SnackbarHost(snackbarHostState) },
    ) { paddingValue ->
        Box(
            modifier = Modifier
                .padding(paddingValue)
                .fillMaxSize()
                .clickable {
                    showPicker = true
                },
            contentAlignment = Alignment.Center
        ) {
            if (showPicker) {
                FilePickerScreen(
                    onSelectFile = { uri ->
                        scope.launch {
                            snackbarHostState.showSnackbar("Load opml document success.")
                        }
                        navigateToDiscover()
                        viewModel.importOpml(contentResolver, uri)
                    }
                )
            } else {
                Text(text = "Click to pick opml file")
            }
        }
    }
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

    RequestPermissionScreen(permission = Manifest.permission.READ_EXTERNAL_STORAGE) {
        Button(onClick = {
            openFilePicker()
        }) {
            Text("Choose Document")
        }
    }

}