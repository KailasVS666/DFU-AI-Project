@file:OptIn(ExperimentalCoroutinesApi::class)

package com.dfuai.app.ui.screens

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.unit.dp
import com.dfuai.app.ml.DFUModel
import com.dfuai.app.ml.ExplainabilityEngine
import com.dfuai.app.ml.OcclusionHeatmap
import com.dfuai.app.ui.components.PrimaryButton
import com.dfuai.app.ui.components.SectionCard
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch

@Composable
fun ResultScreen(
    bitmap: Bitmap?,
    isUlcer: Boolean?,
    confidence: Float?,
    model: DFUModel,
    onScanAgain: () -> Unit,
) {
    val scrollState = rememberScrollState()
    val scope = rememberCoroutineScope()
    val engine = remember { ExplainabilityEngine() }
    var explanationState: ExplanationState by remember { mutableStateOf(ExplanationState.Idle) }
    var heatmap by remember { mutableStateOf<OcclusionHeatmap?>(null) }
    var explanationJob by remember { mutableStateOf<Job?>(null) }

    LaunchedEffect(Unit) {
        explanationState = ExplanationState.Idle
        heatmap = null
        explanationJob?.cancel()
    }

    Column(
        modifier = Modifier
            .fillMaxSize()
            .verticalScroll(scrollState)
            .padding(24.dp),
        verticalArrangement = Arrangement.Top,
    ) {
        Text(
            text = "Screening Result",
            style = MaterialTheme.typography.headlineMedium,
        )

        Spacer(Modifier.height(6.dp))

        Text(
            text = "AI-assisted analysis of the selected image.",
            style = MaterialTheme.typography.bodyLarge,
        )

        Spacer(Modifier.height(20.dp))

        bitmap?.let {
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(24.dp),
                elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            ) {
                Image(
                    bitmap = it.asImageBitmap(),
                    contentDescription = "Analyzed foot image",
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(260.dp),
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
                style = MaterialTheme.typography.headlineSmall,
            )

            confidence?.let {
                Spacer(Modifier.height(8.dp))

                Text(
                    text = "Confidence: ${(it * 100).format(1)}%",
                    style = MaterialTheme.typography.titleMedium,
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
                style = MaterialTheme.typography.bodyMedium,
            )
        }

        Spacer(Modifier.height(16.dp))

        Text(
            text = "This result is intended for research and screening support " +
                "only. It does not replace examination or diagnosis by a " +
                "qualified healthcare professional.",
            style = MaterialTheme.typography.bodySmall,
        )

        Spacer(Modifier.height(24.dp))

        bitmap?.let { currentBitmap ->
            PrimaryButton(
                text = when (explanationState) {
                    is ExplanationState.Generating -> "Generating Explanation..."
                    is ExplanationState.Ready -> "Regenerate AI Explanation"
                    is ExplanationState.Idle -> "Show AI Explanation"
                },
                enabled = explanationState !is ExplanationState.Generating,
                onClick = {
                    explanationJob?.cancel()
                    explanationJob = scope.launch {
                        try {
                            explanationState = ExplanationState.Generating
                            val result = engine.generateOcclusionHeatmap(
                                original = currentBitmap,
                                model = model,
                            )
                            heatmap = result
                            explanationState = ExplanationState.Ready(result)
                        } catch (e: Exception) {
                            if (explanationState is ExplanationState.Generating) {
                                explanationState = ExplanationState.Idle
                            }
                            android.util.Log.e("OcclusionDebug", "Occlusion failed", e)
                        }
                    }
                },
            )

            if (explanationState is ExplanationState.Generating) {
                Spacer(Modifier.height(12.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    CircularProgressIndicator(modifier = Modifier.size(16.dp))
                    Spacer(Modifier.width(8.dp))
                    Text(
                        text = "Generating explanation...",
                        style = MaterialTheme.typography.bodyMedium,
                    )
                }
            }

                            val stateNow = explanationState
                            val overlay = when (stateNow) {
                                is ExplanationState.Ready -> stateNow.heatmap.heatmapOverlay
                                else -> heatmap?.heatmapOverlay
                            }
                            if (overlay != null) {
                                Spacer(Modifier.height(20.dp))
                                OcclusionExplanationSection(
                                    original = currentBitmap,
                                    overlay = overlay,
                                )
                            }
        }

        Spacer(Modifier.height(16.dp))

        PrimaryButton(
            text = "Analyze Another Image",
            onClick = onScanAgain,
        )
    }
}

@Composable
private fun OcclusionExplanationSection(
    original: Bitmap,
    overlay: Bitmap,
) {
    val blended = remember(original, overlay) {
        val result = Bitmap.createBitmap(original.width, original.height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(result)
        canvas.drawBitmap(original, 0f, 0f, null)
        val paint = Paint().apply {
            alpha = 160
        }
        canvas.drawBitmap(overlay, 0f, 0f, paint)
        result
    }

    SectionCard {
        Text(
            text = "AI Explanation",
            style = MaterialTheme.typography.titleMedium,
        )

        Spacer(Modifier.height(12.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(20.dp),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
        ) {
            Image(
                bitmap = blended.asImageBitmap(),
                contentDescription = "Occlusion sensitivity model response map",
                modifier = Modifier
                    .fillMaxWidth()
                    .height(260.dp),
            )
        }

        Spacer(Modifier.height(12.dp))

        Text(
            text = "Highlighted areas indicate regions that most influenced the model's prediction.",
            style = MaterialTheme.typography.bodyMedium,
        )

        Spacer(Modifier.height(8.dp))

        Text(
            text = "This visualization is qualitative and is not a clinical lesion segmentation.",
            style = MaterialTheme.typography.bodySmall,
        )
    }
}

private sealed class ExplanationState {
    data object Idle : ExplanationState()
    data object Generating : ExplanationState()
    data class Ready(val heatmap: OcclusionHeatmap) : ExplanationState()
}

private fun Float.format(decimals: Int): String =
    "%.${decimals}f".format(this)