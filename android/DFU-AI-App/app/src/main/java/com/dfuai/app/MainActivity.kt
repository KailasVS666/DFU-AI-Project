package com.dfuai.app

import android.graphics.BitmapFactory
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.compose.setContent
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import com.dfuai.app.ui.theme.DFUAITheme
import com.google.ai.edge.litert.Accelerator
import com.google.ai.edge.litert.CompiledModel

class MainActivity : ComponentActivity() {

    private var model: CompiledModel? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val modelStatus = try {
            model = CompiledModel.create(
                assets,
                "efficientnet_b0_dynamic_range.tflite",
                CompiledModel.Options(Accelerator.CPU)
            )
            "DFU AI model loaded successfully"
        } catch (e: Exception) {
            "Model loading failed: ${e.message}"
        }

        setContent {
            DFUAITheme {

                var selectedImage by remember { mutableStateOf<android.graphics.Bitmap?>(null) }

                val imagePicker = rememberLauncherForActivityResult(
                    contract = ActivityResultContracts.GetContent()
                ) { uri ->
                    uri?.let {
                        contentResolver.openInputStream(it)?.use { stream ->
                            selectedImage = BitmapFactory.decodeStream(stream)
                        }
                    }
                }

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(24.dp),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {

                    Text("DFU AI")

                    Spacer(modifier = Modifier.height(12.dp))

                    Text(modelStatus)

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            imagePicker.launch("image/*")
                        }
                    ) {
                        Text("Select Foot Image")
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    selectedImage?.let { bitmap ->
                        Image(
                            bitmap = bitmap.asImageBitmap(),
                            contentDescription = "Selected foot image",
                            modifier = Modifier.size(300.dp),
                            contentScale = ContentScale.Fit
                        )
                    }
                }
            }
        }
    }

    override fun onDestroy() {
        model?.close()
        model = null
        super.onDestroy()
    }
}