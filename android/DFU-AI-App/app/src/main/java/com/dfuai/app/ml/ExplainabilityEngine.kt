
package com.dfuai.app.ml

import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Paint
import android.graphics.Rect
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

data class OcclusionImportance(
    val gridSize: Int,
    val importanceGrid: Array<FloatArray>,
    val originalProbability: Float,
    val originalIsUlcer: Boolean,
    val originalHealthyProbability: Float,
    val originalUlcerProbability: Float,
)

data class OcclusionHeatmap(
    val heatmapOverlay: Bitmap,
    val importance: OcclusionImportance,
)

class ExplainabilityEngine {

    private val fillPaint = Paint().apply {
        color = FILL_COLOR
        isAntiAlias = false
        style = Paint.Style.FILL
    }

    suspend fun generateOcclusionHeatmap(
        original: Bitmap,
        model: DFUModel,
        gridSize: Int = DEFAULT_GRID_SIZE,
        fillColor: Int = FILL_COLOR,
    ): OcclusionHeatmap = withContext(Dispatchers.Default) {
        require(gridSize >= 2) { "Grid size must be at least 2" }
        fillPaint.color = fillColor

        val targetSize = ImagePreprocessor.IMAGE_SIZE
        val resizedOriginal = Bitmap.createScaledBitmap(
            original, targetSize, targetSize, true
        )
        val originalProb = model.computeProbability(
            ImagePreprocessor.prepare(resizedOriginal)
        )
        val originalIsUlcer = originalProb >= THRESHOLD
        val originalUlcerProb = originalProb
        val originalHealthyProb = 1f - originalProb

        val cellWidth = targetSize / gridSize
        val cellHeight = targetSize / gridSize
        val importanceGrid = Array(gridSize) { FloatArray(gridSize) }

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val left = col * cellWidth
                val top = row * cellHeight
                val right = if (col == gridSize - 1) {
                    targetSize
                } else {
                    (col + 1) * cellWidth
                }
                val bottom = if (row == gridSize - 1) {
                    targetSize
                } else {
                    (row + 1) * cellHeight
                }

                val occluded = resizedOriginal.copy(
                    Bitmap.Config.ARGB_8888, true
                )
                try {
                    Canvas(occluded).drawRect(
                        Rect(left, top, right, bottom), fillPaint
                    )
                    val occludedProb = model.computeProbability(
                        ImagePreprocessor.prepare(occluded)
                    )
                    importanceGrid[row][col] = computeImportance(
                        originalIsUlcer,
                        originalUlcerProb,
                        originalHealthyProb,
                        occludedProb,
                    )
                } finally {
                    occluded.recycle()
                }
            }
        }

        val normalizedGrid = normalizeGrid(importanceGrid)
        val smoothHeatmap = createSmoothHeatmap(
            normalizedGrid, targetSize, targetSize
        )
        val scaledOverlay = Bitmap.createScaledBitmap(
            smoothHeatmap, original.width, original.height, true
        )
        if (smoothHeatmap !== scaledOverlay) smoothHeatmap.recycle()

        OcclusionHeatmap(
            heatmapOverlay = scaledOverlay,
            importance = OcclusionImportance(
                gridSize = gridSize,
                importanceGrid = normalizedGrid,
                originalProbability = originalProb,
                originalIsUlcer = originalIsUlcer,
                originalHealthyProbability = originalHealthyProb,
                originalUlcerProbability = originalUlcerProb,
            ),
        )
    }

    private fun computeImportance(
        originalIsUlcer: Boolean,
        originalUlcerProb: Float,
        originalHealthyProb: Float,
        occludedProb: Float,
    ): Float {
        val occludedHealthyProb = 1f - occludedProb
        val importance = if (originalIsUlcer) {
            originalUlcerProb - occludedProb
        } else {
            originalHealthyProb - occludedHealthyProb
        }
        return importance.coerceAtLeast(0f)
    }

    private fun normalizeGrid(
        grid: Array<FloatArray>,
    ): Array<FloatArray> {
        val max = grid.maxOf { row -> row.maxOrNull() ?: 0f }
        if (max <= 0f) {
            return Array(grid.size) { FloatArray(grid[0].size) }
        }
        return Array(grid.size) { row ->
            FloatArray(grid[row].size) { col ->
                grid[row][col] / max
            }
        }
    }

    private fun createSmoothHeatmap(
        grid: Array<FloatArray>,
        width: Int,
        height: Int,
    ): Bitmap {
        val small = Bitmap.createBitmap(
            grid.size, grid[0].size, Bitmap.Config.ARGB_8888
        )
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                val value = grid[row][col]
                val alpha = if (value <= 0f) 0 else
                    (40 + value * 125).toInt().coerceIn(40, 165)
                small.setPixel(
                    col, row,
                    (alpha shl 24) or (heatmapColor(value) and 0x00FFFFFF),
                )
            }
        }

        val smoothPaint = Paint(Paint.FILTER_BITMAP_FLAG or Paint.ANTI_ALIAS_FLAG)
        val output = Bitmap.createBitmap(
            width, height, Bitmap.Config.ARGB_8888
        )
        Canvas(output).drawBitmap(
            small,
            null,
            Rect(0, 0, width, height),
            smoothPaint,
        )
        small.recycle()
        return output
    }

    private fun heatmapColor(value: Float): Int {
        val v = value.coerceIn(0f, 1f)
        return when {
            v < 0.25f -> interpolateColor(0xFF0000FF, 0xFF00FFFF, v / 0.25f)
            v < 0.5f -> interpolateColor(
                0xFF00FFFF, 0xFF00FF00, (v - 0.25f) / 0.25f
            )
            v < 0.75f -> interpolateColor(
                0xFF00FF00, 0xFFFFFF00, (v - 0.5f) / 0.25f
            )
            else -> interpolateColor(
                0xFFFFFF00, 0xFFFF0000, (v - 0.75f) / 0.25f
            )
        }
    }

    private fun interpolateColor(
        start: Long,
        end: Long,
        fraction: Float,
    ): Int {
        val t = fraction.coerceIn(0f, 1f)
        val s = start.toInt()
        val e = end.toInt()
        val r = (((s shr 16) and 255) * (1f - t) +
                ((e shr 16) and 255) * t).toInt()
        val g = (((s shr 8) and 255) * (1f - t) +
                ((e shr 8) and 255) * t).toInt()
        val b = ((s and 255) * (1f - t) +
                (e and 255) * t).toInt()
        return (r shl 16) or (g shl 8) or b
    }

    companion object {
        private const val DEFAULT_GRID_SIZE = 7
        private const val THRESHOLD = 0.5f
        private const val FILL_COLOR = 0xFF808080.toInt()
    }
}
