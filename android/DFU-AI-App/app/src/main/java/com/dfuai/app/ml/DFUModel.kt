package com.dfuai.app.ml

import android.content.Context
import android.graphics.Bitmap
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel

class DFUModel(context: Context) : AutoCloseable {

    private val model = CompiledModel.create(
        context.assets,
        "efficientnet_b0_dynamic_range.tflite",
        CompiledModel.Options(Accelerator.CPU)
    )

    fun computeProbability(input: FloatArray): Float {
        val inputs = model.createInputBuffers()
        inputs[0].writeFloat(input)
        val outputs = model.run(inputs)
        return outputs[0].readFloat()[0]
    }

    fun analyzeProbability(bitmap: Bitmap): Float {
        val input = ImagePreprocessor.prepare(bitmap)
        return computeProbability(input)
    }

    fun analyze(bitmap: Bitmap): Prediction {
        val probability = analyzeProbability(bitmap)

        return Prediction(
            isUlcer = probability >= 0.5f,
            probability = probability,
            confidence = if (probability >= 0.5f) {
                probability
            } else {
                1f - probability
            }
        )
    }

    override fun close() {
        model.close()
    }
}