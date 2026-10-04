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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dfuai.app.ui.components.PrimaryButton
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
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.Start
    ) {
        Text(
            text = "DFU AI",
            style = MaterialTheme.typography.displaySmall
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "Diabetic Foot Screening",
            style = MaterialTheme.typography.titleLarge
        )

        Spacer(Modifier.height(10.dp))

        Text(
            text = "AI-assisted screening from a foot image.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.height(28.dp))

        StatusCard(
            title = "AI model",
            message = if (modelReady) {
                "Ready for image analysis"
            } else {
                "Model unavailable"
            }
        )

        Spacer(Modifier.height(20.dp))

        PrimaryButton(
            text = "Start a New Scan",
            onClick = onScan
        )

        Spacer(Modifier.height(16.dp))

        Text(
            text = "For research and screening support only. " +
                    "This application does not replace clinical diagnosis.",
            style = MaterialTheme.typography.bodySmall
        )
    }
}