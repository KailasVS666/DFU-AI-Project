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
        fillPaint.color = fillColor

        val targetSize = ImagePreprocessor.IMAGE_SIZE
        val resizedOriginal = Bitmap.createScaledBitmap(original, targetSize, targetSize, true)
        val originalProb = try {
            model.computeProbability(ImagePreprocessor.prepare(resizedOriginal))
        } catch (e: Exception) {
            android.util.Log.e("OcclusionDebug", "Failed to compute original prob", e)
            throw e
        }
        val originalIsUlcer = originalProb >= THRESHOLD
        val originalUlcerProb = originalProb
        val originalHealthyProb = 1f - originalProb

        val cellWidth = if (targetSize % gridSize == 0) {
            targetSize / gridSize
        } else {
            targetSize / gridSize
        }

        val cellHeight = if (targetSize % gridSize == 0) {
            targetSize / gridSize
        } else {
            targetSize / gridSize
        }

        val importanceGrid = Array(gridSize) { FloatArray(gridSize) { 0f } }

        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val left = col * cellWidth
                val top = row * cellHeight
                val right = if (col == gridSize - 1) targetSize else (col + 1) * cellWidth
                val bottom = if (row == gridSize - 1) targetSize else (row + 1) * cellHeight
                val occluded = resizedOriginal.copy(Bitmap.Config.ARGB_8888, true)
                val canvas = Canvas(occluded)
                canvas.drawRect(Rect(left, top, right, bottom), fillPaint)
                val occludedProb = try {
                    model.computeProbability(ImagePreprocessor.prepare(occluded))
                } catch (e: Exception) {
                    android.util.Log.e("OcclusionDebug", "Failed at cell $row,$col", e)
                    throw e
                }
                val importance = computeImportance(
                    originalIsUlcer = originalIsUlcer,
                    originalUlcerProb = originalUlcerProb,
                    originalHealthyProb = originalHealthyProb,
                    occludedProb = occludedProb,
                )
                importanceGrid[row][col] = importance
                occluded.recycle()
            }
        }

        val normalizedGrid = normalizeGrid(importanceGrid)
        val heatmapSmall = createHeatmapBitmap(normalizedGrid, targetSize, targetSize, gridSize)
        val heatmapOverlay = Bitmap.createScaledBitmap(heatmapSmall, original.width, original.height, true)

        OcclusionHeatmap(
            heatmapOverlay = heatmapOverlay,
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
        val occludedUlcerProb = occludedProb
        val occludedHealthyProb = 1f - occludedProb
        val importance = if (originalIsUlcer) {
            originalUlcerProb - occludedUlcerProb
        } else {
            originalHealthyProb - occludedHealthyProb
        }
        return if (importance < 0f) 0f else importance
    }

    private fun normalizeGrid(grid: Array<FloatArray>): Array<FloatArray> {
        var max = 0f
        for (row in grid) {
            for (value in row) {
                if (value > max) {
                    max = value
                }
            }
        }
        if (max <= 0f) {
            return Array(grid.size) { FloatArray(grid[0].size) { 0f } }
        }
        val normalized = Array(grid.size) { FloatArray(grid[0].size) }
        for (row in grid.indices) {
            for (col in grid[row].indices) {
                normalized[row][col] = grid[row][col] / max
            }
        }
        return normalized
    }

    private fun createHeatmapBitmap(
        normalizedGrid: Array<FloatArray>,
        width: Int,
        height: Int,
        gridSize: Int,
    ): Bitmap {
        val heatmap = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
        val canvas = Canvas(heatmap)
        val cellWidth = if (width % gridSize == 0) width / gridSize else width / gridSize
        val cellHeight = if (height % gridSize == 0) height / gridSize else height / gridSize
        for (row in 0 until gridSize) {
            for (col in 0 until gridSize) {
                val left = col * cellWidth
                val top = row * cellHeight
                val right = if (col == gridSize - 1) width else (col + 1) * cellWidth
                val bottom = if (row == gridSize - 1) height else (row + 1) * cellHeight
                val value = normalizedGrid[row][col]
                if (value > 0f) {
                    val alpha = (64 + value * 191).toInt().coerceIn(64, 255)
                    val color = heatmapColor(value)
                    val paint = Paint().apply {
                        this.color = (alpha shl 24) or (color and 0x00FFFFFF)
                        isAntiAlias = true
                        style = Paint.Style.FILL
                    }
                    canvas.drawRect(Rect(left, top, right, bottom), paint)
                }
            }
        }
        return heatmap
    }

    private fun heatmapColor(value: Float): Int {
        val clamped = value.coerceIn(0f, 1f)
        return when {
            clamped < 0.25f -> {
                val t = clamped / 0.25f
                interpolateColor(0xFF0000FF, 0xFF00FFFF, t)
            }
            clamped < 0.5f -> {
                val t = (clamped - 0.25f) / 0.25f
                interpolateColor(0xFF00FFFF, 0xFF00FF00, t)
            }
            clamped < 0.75f -> {
                val t = (clamped - 0.5f) / 0.25f
                interpolateColor(0xFF00FF00, 0xFFFFFF00, t)
            }
            else -> {
                val t = (clamped - 0.75f) / 0.25f
                interpolateColor(0xFFFFFF00, 0xFFFF0000, t)
            }
        }
    }

    private fun interpolateColor(start: Long, end: Long, t: Float): Int {
        val tClamped = t.coerceIn(0f, 1f)
        val startInt = start.toInt()
        val endInt = end.toInt()
        val r = ((startInt shr 16) and 0xFF) * (1f - tClamped) + ((endInt shr 16) and 0xFF) * tClamped
        val g = ((startInt shr 8) and 0xFF) * (1f - tClamped) + ((endInt shr 8) and 0xFF) * tClamped
        val b = (startInt and 0xFF) * (1f - tClamped) + (endInt and 0xFF) * tClamped
        return ((r.toInt()) shl 16) or ((g.toInt()) shl 8) or (b.toInt())
    }

    companion object {
        private const val DEFAULT_GRID_SIZE = 7
        private const val THRESHOLD = 0.5f
        private const val FILL_COLOR = 0xFF808080.toInt()
    }
}