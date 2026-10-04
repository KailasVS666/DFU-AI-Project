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
import com.dfuai.app.ui.components.PrimaryButton
import com.dfuai.app.ui.components.SectionCard
import com.dfuai.app.ui.components.StatusCard

@Composable
fun HomeScreen(
    modelReady: Boolean,
    onScan: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Top
    ) {
        Spacer(Modifier.height(24.dp))

        Text(
            text = "DFU AI",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Diabetic Foot Screening",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = "AI-assisted screening from a foot image, " +
                    "performed directly on your device.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(28.dp))

        StatusCard(
            title = "AI model",
            message = if (modelReady) {
                "Ready for screening"
            } else {
                "Model unavailable"
            }
        )

        Spacer(Modifier.height(16.dp))

        SectionCard {
            Text(
                text = "Start a screening",
                style = MaterialTheme.typography.titleLarge
            )

            Spacer(Modifier.height(8.dp))

            Text(
                text = "Select a clear foot image and let the on-device " +
                        "AI model screen it for features associated with " +
                        "a diabetic foot ulcer."
            )

            Spacer(Modifier.height(18.dp))

            PrimaryButton(
                text = "Start New Screening",
                onClick = onScan,
                enabled = modelReady
            )
        }

        Spacer(Modifier.height(20.dp))

        Text(
            text = "For research and screening support only. " +
                    "The result does not replace professional clinical " +
                    "assessment or diagnosis.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}