package com.dfuai.app.navigation

import androidx.compose.runtime.Composable
import com.dfuai.app.ml.DFUModel
import com.dfuai.app.ui.DFUApp

@Composable
fun AppNavigation(model: DFUModel) {
    DFUApp(model = model)
}