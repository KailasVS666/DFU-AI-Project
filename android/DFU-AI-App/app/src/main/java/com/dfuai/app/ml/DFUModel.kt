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

    fun analyze(bitmap: Bitmap): Prediction {
        val input = ImagePreprocessor.prepare(bitmap)

        val inputs = model.createInputBuffers()
        inputs[0].writeFloat(input)

        val outputs = model.run(inputs)
        val probability = outputs[0].readFloat()[0]

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