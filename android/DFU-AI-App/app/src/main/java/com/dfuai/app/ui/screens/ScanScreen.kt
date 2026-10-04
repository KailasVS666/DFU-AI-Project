package com.dfuai.app.ui.screens

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dfuai.app.ui.components.PrimaryButton

@Composable
fun ScanScreen(
    bitmap: Bitmap?,
    onImageSelected: (Bitmap) -> Unit,
    onAnalyze: (Bitmap) -> Unit
) {
    val context = LocalContext.current

    val picker = rememberLauncherForActivityResult(
        contract = ActivityResultContracts.GetContent()
    ) { uri: Uri? ->
        if (uri != null) {
            context.contentResolver.openInputStream(uri)?.use { stream ->
                BitmapFactory.decodeStream(stream)?.let(onImageSelected)
            }
        }
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "New Scan",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "Select a clear image of the foot.",
            style = MaterialTheme.typography.bodyMedium
        )

        Spacer(Modifier.height(24.dp))

        if (bitmap == null) {
            EmptyImageCard(
                onChooseImage = {
                    picker.launch("image/*")
                }
            )
        } else {
            val selectedBitmap = bitmap

            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp)
            ) {
                Image(
                    bitmap = selectedBitmap.asImageBitmap(),
                    contentDescription = "Selected foot image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(320.dp)
                )
            }

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = "Analyze Image",
                onClick = {
                    onAnalyze(selectedBitmap)
                }
            )

            Spacer(Modifier.height(12.dp))

            PrimaryButton(
                text = "Choose Different Image",
                onClick = {
                    picker.launch("image/*")
                }
            )
        }
    }
}

@Composable
private fun EmptyImageCard(
    onChooseImage: () -> Unit
) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .height(300.dp),
        shape = RoundedCornerShape(24.dp)
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "No image selected",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Choose a foot image from your gallery."
            )

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = "Choose Image",
                onClick = onChooseImage
            )
        }
    }
}