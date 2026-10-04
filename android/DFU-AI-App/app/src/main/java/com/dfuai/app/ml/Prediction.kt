package com.dfuai.app.ml

data class Prediction(
    val isUlcer: Boolean,
    val probability: Float,
    val confidence: Float
)