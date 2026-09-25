# Mobile Deployment

## 1. Objective

Evaluate EfficientNet-B0 for mobile deployment using TensorFlow Lite (TFLite), including float32, dynamic-range quantized, and full INT8 variants.

All variants were evaluated on the same 108-image held-out test set.

## 2. Base Model

- Model: EfficientNet-B0
- Input: 224 × 224 × 3
- Base model: ImageNet pretrained
- Final trained Keras model used for conversion.
- Test set: 108 images
  - Healthy: 37
  - Ulcer: 71
- Classification threshold: 0.5

## 3. Float32 TFLite

Conversion from the final Keras model was successful.

- Model size: 15.30 MB
- Input dtype: float32
- Output dtype: float32
- Accuracy: 99.07%
- Confusion matrix: `[[36, 1], [0, 71]]`
- Prediction disagreements vs Keras: 0/108
- Mean probability difference: 0.0000001326
- Maximum probability difference: 0.0000018179
- Mean inference latency in Kaggle: 18.833 ms/image

The TFLite model reproduced the Keras classification decisions for all 108 test images.

## 4. Dynamic-Range Quantization

Dynamic-range quantization was applied using TensorFlow Lite optimization.

- Model size: 4.34 MB
- Input dtype: float32
- Output dtype: float32
- Accuracy: 99.07%
- Confusion matrix: `[[36, 1], [0, 71]]`
- Prediction disagreements vs Keras: 0/108
- Mean probability difference: 0.0045974560
- Maximum probability difference: 0.1026080251
- Mean inference latency in Kaggle: 32.678 ms/image

The model size decreased from 15.30 MB to 4.34 MB while producing the same classification decisions on the test set.

## 5. Full INT8 Quantization

Full INT8 quantization used 200 training images as the representative calibration dataset.

- Model size: 4.68 MB
- Internal INT8 tensors: 346
- Internal float32 tensors: 2
- Input dtype: float32
- Output dtype: float32
- Accuracy: 98.15%
- Sensitivity: 98.59%
- Specificity: 97.30%
- Precision: 98.59%
- F1: 98.59%
- Confusion matrix: `[[36, 1], [1, 70]]`
- Prediction disagreements vs Keras: 1/108
- Mean probability difference: 0.0321040954
- Maximum probability difference: 0.5871266723
- Mean inference latency in Kaggle: 18.653 ms/image

Full INT8 quantization introduced one additional false negative on the held-out test set.

## 6. Comparison

| Variant | Size | Accuracy | Disagreements | Mean latency |
|---|---:|---:|---:|---:|
| Float32 | 15.30 MB | 99.07% | 0/108 | 18.833 ms |
| Dynamic-range | 4.34 MB | 99.07% | 0/108 | 32.678 ms |
| Full INT8 | 4.68 MB | 98.15% | 1/108 | 18.653 ms |

## 7. Current Deployment Candidate

Dynamic-range quantization currently provides the most favorable observed size/accuracy combination in this experiment: 4.34 MB while preserving all 108 test-set classification decisions.

This does not establish smartphone performance. Latency measurements were obtained in the Kaggle runtime and require validation on an actual Android device.

## 8. Artifacts

The three TFLite models are stored locally under:

`results/mobile/`

- `efficientnet_b0_float32.tflite`
- `efficientnet_b0_dynamic_range.tflite`
- `efficientnet_b0_int8.tflite`

TFLite model files are excluded from Git because of their binary size.

## 9. Next Mobile Stage

The next stage is Android integration using the selected TFLite model, followed by device-level inference and latency testing.

Mobile deployment should preserve the same 224 × 224 image preprocessing and 0.5 classification threshold used during validation.