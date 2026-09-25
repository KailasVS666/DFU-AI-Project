# PROJECT CONTEXT

## Project

**Title:** AI-Based Mobile System for Early Diagnosis of Diabetic Foot Ulcers

**Goal:** Develop and evaluate a binary DFU image-classification system with explainability, statistical validation, and mobile deployment feasibility.

**Current status:** CNN experiments completed. TFLite/mobile benchmarking is next.

**Repository:** `DFU-AI-Project`

**Local path:** `C:\Users\sharj\Desktop\DFU-AI-Project`

---

## Dataset

Working dataset: DFU dataset associated with Alzubaidi et al., obtained through Kaggle/Google Drive.

**Dataset origin:** Iraq-associated, not India.

The dataset does not provide verified patient IDs, Fitzpatrick labels, Wagner grades, or clinician segmentation/ROI annotations.

**Final cleaned split:**

| Train | Validation | Test | Total |
|---:|---:|---:|---:|
| 494 | 105 | 108 | 707 |

Class mapping:

`0 = Normal(Healthy skin)`  
`1 = Abnormal(Ulcer)`

Exact and near-duplicate leakage between splits: **0**.

---

## Models

Completed:

- EfficientNet-B0
- ResNet50
- VGG16
- MobileNetV2
- InceptionV3

All models use 224×224 input, ImageNet weights, augmentation, transfer learning, fine-tuning, and a 0.5 classification threshold.

---

## Test Results

| Model | Accuracy | Sensitivity | Specificity | F1 |
|---|---:|---:|---:|---:|
| EfficientNet-B0 | 99.07% | 100% | 97.30% | 99.30% |
| ResNet50 | 99.07% | 100% | 97.30% | 99.30% |
| VGG16 | 100% | 100% | 100% | 100% |
| MobileNetV2 | 100% | 100% | 100% | 100% |
| InceptionV3 | 99.07% | 100% | 97.30% | 99.30% |

All models achieved ROC-AUC = 1.000 on the current test set.

These are dataset-specific results, not clinical validation results.

---

## Validation

Exact binomial confidence intervals and exact McNemar pairwise tests have been completed.

All pairwise McNemar tests:

**p = 1.0000**

No statistically significant pairwise difference was detected at α = 0.05.

---

## Explainability

Grad-CAM has been completed for EfficientNet-B0.

Available examples are stored in:

`results/gradcam/`

Grad-CAM is qualitative because clinician segmentation masks/ROIs are unavailable.

---

## Current Limitations

Do not claim:

- Indian population analysis
- Fitzpatrick analysis
- Wagner grading
- Patient-level analysis
- Segmentation performance
- Clinician ROI agreement
- Clinical validation
- Smartphone performance unless actually measured

---

## Completed

Dataset cleaning → leakage checking → five CNN models → model comparison → error analysis → confidence intervals → McNemar testing → Grad-CAM.

---

## Next Step

**TensorFlow Lite/mobile benchmarking**

1. Convert EfficientNet-B0 to TFLite.
2. Compare TFLite predictions with Keras.
3. Measure model size.
4. Measure CPU inference latency.
5. Document results.

Detailed experiment history belongs in `EXPERIMENT_LOG.md`.