package com.dfuai.app.ui.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.dfuai.app.ui.components.PrimaryButton

@Composable
fun HistoryScreen(
    onOpenLatest: () -> Unit
) {
    Column(
        modifier = Modifier
            .fillMaxSize()
            .padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = "Scan History",
            style = MaterialTheme.typography.headlineMedium
        )

        Spacer(Modifier.padding(8.dp))

        Text(
            text = "Your previous AI screening results will appear here.",
            style = MaterialTheme.typography.bodyLarge
        )

        Spacer(Modifier.padding(16.dp))

        PrimaryButton(
            text = "Start a New Scan",
            onClick = onOpenLatest
        )
    }
}