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
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.dfuai.app.ui.components.PrimaryButton
import com.dfuai.app.ui.components.SectionCard

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
        horizontalAlignment = Alignment.Start,
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "New Screening",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Select a clear image of the foot for AI-assisted screening.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(20.dp))

        if (bitmap == null) {
            EmptyImageCard(
                onChooseImage = {
                    picker.launch("image/*")
                }
            )
        } else {
            SelectedImageCard(bitmap)

            Spacer(Modifier.height(16.dp))

            PrimaryButton(
                text = "Analyze Image",
                onClick = {
                    onAnalyze(bitmap)
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

        Spacer(Modifier.height(20.dp))

        SectionCard {
            Text(
                text = "Image guidance",
                style = MaterialTheme.typography.titleMedium
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Use a clear, well-lit image with the foot visible " +
                        "and the area of concern unobstructed."
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
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {
            Text(
                text = "Select a foot image",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Choose an image from your device gallery."
            )

            Spacer(Modifier.height(20.dp))

            PrimaryButton(
                text = "Choose Image",
                onClick = onChooseImage
            )
        }
    }
}

@Composable
private fun SelectedImageCard(bitmap: Bitmap) {
    Card(
        modifier = Modifier.fillMaxWidth(),
        shape = RoundedCornerShape(24.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 2.dp
        )
    ) {
        Image(
            bitmap = bitmap.asImageBitmap(),
            contentDescription = "Selected foot image",
            modifier = Modifier
                .fillMaxWidth()
                .height(320.dp)
        )
    }
}