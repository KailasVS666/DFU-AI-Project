package com.dfuai.app.ui.screens

import android.graphics.Bitmap
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
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.dfuai.app.ui.components.PrimaryButton
import com.dfuai.app.ui.components.SectionCard

@Composable
fun ResultScreen(
    bitmap: Bitmap?,
    isUlcer: Boolean?,
    confidence: Float?,
    onScanAgain: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Text(
            text = "Screening Result",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "AI-assisted analysis of the selected image.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(20.dp))

        bitmap?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(
                    defaultElevation = 2.dp
                )
            ) {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Analyzed foot image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp)
                )
            }
        }

        Spacer(Modifier.height(20.dp))

        SectionCard {
            Text(
                text = when (isUlcer) {
                    true -> "Ulcer detected"
                    false -> "No ulcer detected"
                    null -> "No result"
                },
                style = MaterialTheme.typography.headlineSmall
            )

            confidence?.let {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Confidence: ${(it * 100).format(1)}%",
                    style = MaterialTheme.typography.titleMedium
                )
            }

            Spacer(Modifier.height(16.dp))

            Text(
                text = when (isUlcer) {
                    true -> {
                        "The AI model detected image features associated " +
                                "with a diabetic foot ulcer."
                    }

                    false -> {
                        "The AI model did not detect image features " +
                                "associated with a diabetic foot ulcer."
                    }

                    null -> {
                        "No analysis has been completed."
                    }
                },
                style = MaterialTheme.typography.bodyMedium
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "This result is intended for research and screening support " +
                    "only. It does not replace examination or diagnosis by a " +
                    "qualified healthcare professional.",
            style = MaterialTheme.typography.bodySmall
        )

        Spacer(Modifier.height(24.dp))

        PrimaryButton(
            text = "Analyze Another Image",
            onClick = onScanAgain
        )
    }
}

private fun Float.format(decimals: Int): String =
    "%.${decimals}f".format(this)