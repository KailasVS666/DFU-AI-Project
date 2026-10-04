package com.dfuai.app.ml

import android.graphics.Bitmap

object ImagePreprocessor {

    const val IMAGE_SIZE = 224

    fun prepare(bitmap: Bitmap): FloatArray {
        val resized = Bitmap.createScaledBitmap(
            bitmap,
            IMAGE_SIZE,
            IMAGE_SIZE,
            true
        )

        val pixels = IntArray(IMAGE_SIZE * IMAGE_SIZE)

        resized.getPixels(
            pixels,
            0,
            IMAGE_SIZE,
            0,
            0,
            IMAGE_SIZE,
            IMAGE_SIZE
        )

        val input = FloatArray(IMAGE_SIZE * IMAGE_SIZE * 3)
        var index = 0

        for (pixel in pixels) {
            input[index++] = ((pixel shr 16) and 0xFF).toFloat()
            input[index++] = ((pixel shr 8) and 0xFF).toFloat()
            input[index++] = (pixel and 0xFF).toFloat()
        }

        return input
    }
}