# IMPLEMENTATION DETAILS

## 1. Environment

- Training: Kaggle
- GPU: Tesla T4 × 2
- Kaggle TensorFlow: 2.20.0
- Local TensorFlow: 2.21.0
- Local Keras: 3.15.1
- Random seed: 42

Training is performed on Kaggle GPU. Local environment is used for scripting and analysis.

## 2. Dataset Configuration

Kaggle path:

`/kaggle/input/datasets/kailassharji/dfu-ai-split/DFU_split`

Dataset version:

`Clean Deduplicated Split v2`

Input size: `224 × 224 RGB`

Batch size: `16`

Class mapping:
- `0 = Normal(Healthy skin)`
- `1 = Abnormal(Ulcer)`

Classification threshold: `0.5`

## 3. Augmentation

Training augmentation:
- Horizontal flip
- Rotation factor: `0.05`
- Zoom factor: `0.10`

Validation and test data are evaluated without random training augmentation.

## 4. Transfer Learning

All five models use ImageNet-pretrained weights.

Stage 1:
- Frozen backbone
- Adam
- Learning rate: `1e-4`
- Maximum epochs: `20`
- Early stopping patience: `5`

Stage 2:
- Final 20 backbone layers unfrozen
- Adam
- Learning rate: `1e-5`
- Maximum epochs: `10`
- Early stopping patience: `3`

## 5. EfficientNet-B0

Architecture:

`EfficientNet-B0 → Global Average Pooling → Dropout → Dense`

- Dropout: `0.3`
- L2 regularization: `1e-4`

## 6. Preprocessing

- EfficientNet-B0: Keras EfficientNet preprocessing
- ResNet50: `resnet50.preprocess_input`
- MobileNetV2: Keras MobileNetV2 preprocessing
- InceptionV3: Keras InceptionV3 preprocessing
- VGG16: implemented VGG16 preprocessing pipeline

## 7. Evaluation

Metrics:
- Accuracy
- Sensitivity
- Specificity
- Precision
- F1-score
- ROC-AUC
- Confusion matrix

Exact binomial confidence intervals are used for sensitivity and specificity.

Pairwise model comparisons use exact McNemar tests with:

`α = 0.05`

## 8. Grad-CAM

Grad-CAM was implemented for EfficientNet-B0 using the `top_conv` layer.

Examples:
- True positive
- True negative
- False positive

Saved under:

`results/gradcam/`

Grad-CAM is qualitative because clinician ROI/segmentation annotations are unavailable.

## 9. Output Organization

- Models: `results/models/`
- Metrics: `results/metrics/`
- Predictions: `results/predictions/`
- Grad-CAM: `results/gradcam/`

Large model files are excluded from Git.

## 10. Main Scripts

Training:
- `src/train_efficientnet.py`
- `src/train_resnet50.py`
- `src/train_vgg16.py`
- `src/train_mobilenetv2.py`
- `src/train_inceptionv3.py`

Analysis:
- `src/compare_models.py`
- `src/compare_predictions.py`
- `src/exact_confidence_intervals.py`
- `src/mcnemar_comparison.py`
- `src/gradcam.py`

Dataset validation:
- `src/create_split.py`
- `src/check_internal_duplicates.py`
- `src/check_split_leakage.py`

## 11. Model Results

| Model | Accuracy | Sensitivity | Specificity | F1 |
|---|---:|---:|---:|---:|
| EfficientNet-B0 | 99.07% | 100% | 97.30% | 99.30% |
| ResNet50 | 99.07% | 100% | 97.30% | 99.30% |
| VGG16 | 100% | 100% | 100% | 100% |
| MobileNetV2 | 100% | 100% | 100% | 100% |
| InceptionV3 | 99.07% | 100% | 97.30% | 99.30% |

## 12. Important Notes

CLAHE is **not implemented** in the completed experiments.

Fitzpatrick analysis is **not possible** with the current dataset.

Wagner classification is **not implemented**.

Clinician ROI/segmentation comparison is **not implemented**.

## 13. Mobile Deployment

Current deployment candidate:

`EfficientNet-B0`

Pending:
- TensorFlow Lite conversion
- Quantization
- Keras vs TFLite prediction comparison
- Model-size measurement
- CPU latency measurement

Desktop/Kaggle latency must not be reported as smartphone latency.

## 14. Reproducibility

Each new experiment should record:
- Dataset version
- Model
- Input size
- Batch size
- Augmentation
- Learning rates
- Trainable layers
- Epoch limits
- Threshold
- Seed
- Hardware/software
- Results location

Completed experiments must be added to:

`docs/EXPERIMENT_LOG.md`