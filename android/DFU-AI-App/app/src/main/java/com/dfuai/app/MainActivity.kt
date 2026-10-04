package com.dfuai.app

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.dfuai.app.ml.DFUModel
import com.dfuai.app.navigation.AppNavigation
import com.dfuai.app.ui.theme.DFUAITheme

class MainActivity : ComponentActivity() {

    private var dfuModel: DFUModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        try {
            dfuModel = DFUModel(this)

            setContent {
                DFUAITheme {
                    AppNavigation(model = dfuModel!!)
                }
            }
        } catch (_: Exception) {
            setContent {
                DFUAITheme {
                    androidx.compose.material3.Text(
                        text = "Unable to load AI model"
                    )
                }
            }
        }
    }

    override fun onDestroy() {
        dfuModel?.close()
        dfuModel = null
        super.onDestroy()
    }
}