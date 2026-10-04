package com.dfuai.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dfuai.app.ui.components.SectionCard

@Composable
fun InfoScreen() {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        Text(
            text = "About DFU AI",
            style = MaterialTheme.typography.headlineMedium
        )

        Text(
            text = "AI-assisted diabetic foot ulcer screening.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(4.dp))

        SectionCard {
            Text(
                text = "How it works",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Select a foot image and the on-device " +
                        "EfficientNet-B0 model analyzes it for " +
                        "features associated with a diabetic foot ulcer."
            )
        }

        SectionCard {
            Text(
                text = "Privacy",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Image analysis is performed locally on the device. " +
                        "The application does not require an online AI service " +
                        "for inference."
            )
        }

        SectionCard {
            Text(
                text = "Important",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "DFU AI is a research prototype and screening aid. " +
                        "Its output should not be used as a standalone " +
                        "clinical diagnosis."
            )
        }
    }
}