# METHODOLOGY

## 1. Study Overview

This study develops and evaluates an AI-based image-classification system for binary diabetic foot ulcer (DFU) detection.

Workflow:

Dataset → Cleaning → Split → Preprocessing → CNN Training → Evaluation → Statistical Validation → Grad-CAM → TensorFlow Lite

The study is a model-development study and does not constitute clinical validation.

## 2. Dataset

The working dataset is the DFU dataset associated with Alzubaidi et al., obtained through Kaggle/Google Drive.

The available dataset provides binary image-level labels:
- Normal(Healthy skin)
- Abnormal(Ulcer)

The dataset does not provide verified patient IDs, Fitzpatrick skin-tone labels, Wagner grades, or clinician segmentation/ROI annotations.

## 3. Dataset Cleaning and Splitting

Duplicate and near-duplicate images were identified using SHA-256 hashing, perceptual hashing (pHash), and connected-component grouping.

One representative was retained from each duplicate/near-duplicate group.

| Split | Healthy | Ulcer | Total |
|---|---:|---:|---:|
| Train | 167 | 327 | 494 |
| Validation | 35 | 70 | 105 |
| Test | 37 | 71 | 108 |
| Total | 239 | 468 | 707 |

Cross-split checks detected no exact or near-duplicate leakage.

## 4. Classification

The implemented class mapping is:

- `0 = Normal(Healthy skin)`
- `1 = Abnormal(Ulcer)`

Class ordering is explicitly defined in the training scripts.

## 5. Image Processing

Images are resized to 224 × 224 pixels.

Training augmentation includes horizontal flipping, small rotation, and zoom augmentation.

The held-out test set is evaluated without training augmentation.

## 6. CNN Models

Five ImageNet-pretrained CNN architectures are evaluated:

1. EfficientNet-B0
2. ResNet50
3. VGG16
4. MobileNetV2
5. InceptionV3

All models perform binary classification.

## 7. Transfer Learning

Training uses two stages.

### Stage 1
The pretrained backbone is frozen while the classification head is trained.

### Stage 2
The final 20 backbone layers are unfrozen for fine-tuning.

Both stages use Adam optimization with early stopping.

The detailed hyperparameters are documented separately in `IMPLEMENTATION_DETAILS.md`.

## 8. EfficientNet-B0 Classification Head

The EfficientNet-B0 pipeline uses:

EfficientNet-B0 → Global Average Pooling → Dropout → Dense classifier

Dropout and L2 regularization are applied to the classification head.

## 9. Evaluation

Models are evaluated on the held-out test set of 108 images.

Reported metrics include:

- Accuracy
- Sensitivity
- Specificity
- Precision
- F1-score
- ROC-AUC
- Confusion matrix

## 10. Statistical Analysis

Exact binomial 95% confidence intervals are calculated for sensitivity and specificity.

Pairwise model comparisons use exact McNemar tests because all models produce predictions on the same test images.

The significance level is α = 0.05.

McNemar analysis evaluates paired prediction differences and does not establish clinical equivalence.

## 11. Explainable AI

Grad-CAM is used to visualize regions contributing to EfficientNet-B0 predictions.

The EfficientNet `top_conv` layer is used for Grad-CAM.

Representative examples include:
- True positive
- True negative
- False positive

Because clinician segmentation masks or ROIs are unavailable, Grad-CAM is treated as qualitative evidence only.

No quantitative localization or IoU analysis is performed.

## 12. Mobile Deployment

EfficientNet-B0 is the current primary model for TensorFlow Lite deployment experiments.

Planned evaluation includes:
- Keras-to-TFLite conversion
- Quantization
- Prediction comparison
- Model-size measurement
- CPU inference-latency measurement

The benchmark environment will be explicitly recorded.

Desktop/server latency must not be described as smartphone latency. Physical smartphone performance will only be reported after hardware testing.

## 13. Methodological Limitations

The current dataset does not support:
- Indian population-specific analysis
- Fitzpatrick skin-tone analysis
- Wagner severity analysis
- Patient-level analysis
- Clinician segmentation comparison
- Quantitative Grad-CAM localization

The test set is relatively small and comes from a single dataset source.

External validation on an independent dataset is not currently included.

## 14. Reproducibility

Training and analysis scripts are stored under `src/`.

Experimental outputs are stored under `results/`.

Supporting documentation:
- `docs/DATASET_AUDIT.md`
- `docs/EXPERIMENT_LOG.md`
- `docs/PAPER_NOTES.md`
- `docs/IMPLEMENTATION_DETAILS.md`

## 15. Current Status

Completed:
- Dataset cleaning
- Duplicate/near-duplicate control
- Leakage checking
- CNN training
- Model evaluation
- Statistical validation
- Grad-CAM

Pending:
- TensorFlow Lite conversion
- TFLite validation
- Model-size benchmarking
- CPU latency benchmarking
- Final mobile deployment evaluation