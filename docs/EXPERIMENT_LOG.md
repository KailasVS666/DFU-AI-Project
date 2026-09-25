# EXPERIMENT LOG

## 1. Dataset Preparation

The working dataset is the DFU dataset associated with Alzubaidi et al., obtained through Kaggle/Google Drive.

The dataset is associated with clinical data from Iraq.

A duplicate/near-duplicate controlled split was created using exact hashing, pHash, and connected-component grouping.

### Final split

| Split | Healthy | Ulcer | Total |
|---|---:|---:|---:|
| Train | 167 | 327 | 494 |
| Validation | 35 | 70 | 105 |
| Test | 37 | 71 | 108 |
| Total | 239 | 468 | 707 |

Cross-split exact/near-duplicate overlap: **0**.

---

## 2. Model Training

Five ImageNet-pretrained CNNs were trained using 224×224 images, batch size 16, augmentation, transfer learning, fine-tuning, Adam optimization, and early stopping.

Models:

- EfficientNet-B0
- ResNet50
- VGG16
- MobileNetV2
- InceptionV3

Training environment:

- TensorFlow 2.20.0
- 2 × Tesla T4 GPUs
- Kaggle

---

## 3. Test Results

| Model | Accuracy | Sensitivity | Specificity | Precision | F1 | ROC-AUC |
|---|---:|---:|---:|---:|---:|---:|
| EfficientNet-B0 | 99.07% | 100.00% | 97.30% | 98.61% | 99.30% | 1.000 |
| ResNet50 | 99.07% | 100.00% | 97.30% | 98.61% | 99.30% | 1.000 |
| VGG16 | 100.00% | 100.00% | 100.00% | 100.00% | 100.00% | 1.000 |
| MobileNetV2 | 100.00% | 100.00% | 100.00% | 100.00% | 100.00% | 1.000 |
| InceptionV3 | 99.07% | 100.00% | 97.30% | 98.61% | 99.30% | 1.000 |

These are results on the current 108-image held-out test set.

---

## 4. Statistical Validation

Exact binomial confidence intervals:

**Sensitivity:**  
100.00% (95% CI: 94.94–100.00%) for all models.

**Specificity:**

- EfficientNet-B0: 97.30% (85.84–99.93%)
- ResNet50: 97.30% (85.84–99.93%)
- VGG16: 100.00% (90.51–100.00%)
- MobileNetV2: 100.00% (90.51–100.00%)
- InceptionV3: 97.30% (85.84–99.93%)

Exact pairwise McNemar testing produced **p = 1.0000** for every model pair.

No statistically significant pairwise difference was detected at α = 0.05.

---

## 5. Error and Agreement Analysis

EfficientNet-B0 produced one false positive:

`Normal(Healthy skin)/40.jpg`

Confusion matrix:

`[[36, 1], [0, 71]]`

Only three test images were involved in disagreements between the five models:

- Index 2
- Index 16
- Index 33

All three were true ulcer images.

Detailed prediction probabilities are stored in the prediction-analysis outputs.

---

## 6. Grad-CAM

Grad-CAM was completed for EfficientNet-B0.

Examples are stored in:

`results/gradcam/`

Generated examples include:

- False positive
- True positive
- True negative

Because clinician segmentation masks/ROIs are unavailable, Grad-CAM is reported qualitatively only.

---

## 7. Important Technical Fixes

### Class-label ordering

All training scripts were corrected to explicitly use:

`0 = Normal(Healthy skin)`

`1 = Abnormal(Ulcer)`

This prevents alphabetical directory ordering from reversing the intended labels.

### Dataset splitting

The original split procedure was corrected to retain only one representative from each duplicate/near-duplicate group.

### Grad-CAM

The initial Grad-CAM implementation failed because of the nested Keras Functional architecture. The working implementation accesses the EfficientNet backbone and `top_conv` layer directly.

---

## 8. Current Status

### Completed

- Dataset cleaning
- Leakage checking
- Five CNN experiments
- Model comparison
- Error analysis
- Confidence intervals
- McNemar testing
- Grad-CAM

### Pending

- TensorFlow Lite conversion
- TFLite prediction verification
- Model-size measurement
- CPU latency benchmarking
- Mobile deployment
- Final paper methodology
- Final paper figures/tables

---

## 9. Next Experiment

**TensorFlow Lite/mobile benchmarking**

1. Convert EfficientNet-B0 to TFLite.
2. Verify TFLite predictions against Keras.
3. Measure model size.
4. Measure CPU inference latency.
5. Document the conversion and quantization method.

Detailed dataset information belongs in `DATASET_AUDIT.md`.

Detailed methodology belongs in `METHODOLOGY.md`.

Detailed paper material belongs in `PAPER_NOTES.md`.

A separate `MODEL_ANALYSIS.md` can be created later for detailed model comparisons, prediction probabilities, confidence intervals, and statistical analysis if needed.