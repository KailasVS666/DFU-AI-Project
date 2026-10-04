package com.dfuai.app.ui

import android.graphics.Bitmap
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.dfuai.app.data.ScanRecord
import com.dfuai.app.data.ScanRepository
import com.dfuai.app.ml.DFUModel
import com.dfuai.app.ui.screens.HistoryScreen
import com.dfuai.app.ui.screens.HomeScreen
import com.dfuai.app.ui.screens.InfoScreen
import com.dfuai.app.ui.screens.ResultScreen
import com.dfuai.app.ui.screens.ScanScreen

private enum class AppScreen(val label: String) {
    HOME("Home"),
    SCAN("Scan"),
    HISTORY("History"),
    INFO("Info"),
    RESULT("Result")
}

private val bottomScreens = listOf(
    AppScreen.HOME,
    AppScreen.SCAN,
    AppScreen.HISTORY,
    AppScreen.INFO
)

@Composable
fun DFUApp(model: DFUModel) {
    val context = LocalContext.current
    val repository = remember(context) {
        ScanRepository(context)
    }

    var screen by remember { mutableStateOf(AppScreen.HOME) }
    var selectedBitmap by remember { mutableStateOf<Bitmap?>(null) }
    var isUlcer by remember { mutableStateOf<Boolean?>(null) }
    var confidence by remember { mutableStateOf<Float?>(null) }
    var history by remember { mutableStateOf(repository.getAll()) }

    Scaffold(
        bottomBar = {
            NavigationBar {
                bottomScreens.forEach { item ->
                    NavigationBarItem(
                        selected = screen == item,
                        onClick = {
                            if (item == AppScreen.HISTORY) {
                                history = repository.getAll()
                            }
                            screen = item
                        },
                        icon = {
                            Text(item.label.first().toString())
                        },
                        label = {
                            Text(item.label)
                        }
                    )
                }
            }
        }
    ) { padding ->

        Box(
            modifier = Modifier.padding(padding)
        ) {
            when (screen) {

                AppScreen.HOME -> {
                    HomeScreen(
                        modelReady = true,
                        onScan = {
                            screen = AppScreen.SCAN
                        }
                    )
                }

                AppScreen.SCAN -> {
                    ScanScreen(
                        bitmap = selectedBitmap,
                        onImageSelected = {
                            selectedBitmap = it
                            isUlcer = null
                            confidence = null
                        },
                        onAnalyze = { bitmap ->
                            val result = model.analyze(bitmap)

                            isUlcer = result.isUlcer
                            confidence = result.confidence
                            selectedBitmap = bitmap

                            repository.save(
                                ScanRecord(
                                    id = System.currentTimeMillis(),
                                    timestamp = System.currentTimeMillis(),
                                    isUlcer = result.isUlcer,
                                    confidence = result.confidence
                                )
                            )

                            history = repository.getAll()
                            screen = AppScreen.RESULT
                        }
                    )
                }

                AppScreen.RESULT -> {
                    ResultScreen(
                        bitmap = selectedBitmap,
                        isUlcer = isUlcer,
                        confidence = confidence,
                        onScanAgain = {
                            screen = AppScreen.SCAN
                        }
                    )
                }

                AppScreen.HISTORY -> {
                    HistoryScreen(
                        records = history,
                        onOpenLatest = {
                            screen = AppScreen.SCAN
                        }
                    )
                }

                AppScreen.INFO -> {
                    InfoScreen()
                }
            }
        }
    }
}