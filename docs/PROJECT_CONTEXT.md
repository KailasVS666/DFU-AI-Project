# DFU-AI Project Context

## Project

AI-Based Mobile System for Early Diagnosis of Diabetic Foot Ulcers (DFU).

Goal: develop and evaluate an AI system for binary diabetic-foot-ulcer image classification, with eventual consideration of explainability and mobile deployment.

## Current Role Split

- ChatGPT: research, methodology, statistics, experiment design, interpretation, paper support, coding guidance.
- VS Code: development and execution.
- GitHub: version control.
- Dataset remains local and is excluded from Git.
- Kaggle GPU: actual model training and computationally heavier experiments.

## Repository

DFU-AI-Project/

    data/
        DFU/

    src/

    results/

    notebooks/

    docs/

## Dataset Currently Used

Kaggle/Alzubaidi-associated DFU dataset.

### Important Provenance Limitations

- Do NOT describe this dataset as an Indian dataset.
- Source literature indicates the original clinical images are associated with Nasiriyah Hospital, Iraq.
- No patient metadata, Fitzpatrick labels, Wagner grades, or segmentation masks were found in the downloaded dataset.

### Labeled Patches

Location:

data/DFU/Patches/

- Abnormal(Ulcer): 512
- Normal(Healthy skin): 543
- Total: 1,055

These folder names are the available binary labels.

### Other Dataset Folders

- Original Images: 493
- TestSet: 167
- Transfer-Learning images:
  - internetSet: 137
  - samples: 36
  - Wound Images: 109
  - Wound Images2: 677

Do not infer labels for these other folders.

## Dataset Audit

Audit file:

results/dataset_audit.txt

Detailed duplicate report:

results/duplicate_report.csv

### Key Findings

- Total readable image files: 2,674
- Exact duplicate groups: 503
- Files belonging to exact duplicate groups: 1,182
- Extra exact duplicate files: 679
- Non-exact near-duplicate pHash groups: 90
- Files in near-duplicate groups: 248
- Extra near-duplicate group members: 158
- Patches/TestSet exact hash overlap: 0
- Patches/TestSet pHash overlap within the audit threshold: 0
- 31 filenames are reused between Patches and TestSet, but this is not evidence of content leakage.
- 104 patch filenames have stems matching Original Images filenames; this is only suggestive.

## Critical Methodological Decision

Do NOT randomly split the 1,055 patches.

Exact and near duplicates must be grouped before train/validation/test assignment to reduce data leakage.

All members of a duplicate/near-duplicate group should remain in the same split.

For the final split, each duplicate/near-duplicate group contributes only ONE representative image.

## Leakage-Aware Split

A duplicate/near-duplicate-aware split was created from the 1,055 labeled patches.

Exact duplicates and near-duplicates were grouped using SHA-256 hashing and perceptual hashing (pHash, threshold <= 6).

Each duplicate/near-duplicate group contributed only ONE representative image to the final split.

Location:

data/DFU_split/

### Final Deduplicated Split

#### Train

- Total: 494
- Abnormal(Ulcer): 327
- Normal(Healthy skin): 167

#### Validation

- Total: 105
- Abnormal(Ulcer): 70
- Normal(Healthy skin): 35

#### Test

- Total: 108
- Abnormal(Ulcer): 71
- Normal(Healthy skin): 37

#### Overall

- Total retained: 707

The 707 retained images represent one representative from each duplicate/near-duplicate group assigned to the split.

This should not be described as 707 independent original patient images.

## Split Leakage Verification

Verification included exact SHA-256 comparison and pHash comparison across splits.

Results:

- Train vs Validation:
  - Exact overlaps: 0
  - Near-duplicate overlaps: 0

- Train vs Test:
  - Exact overlaps: 0
  - Near-duplicate overlaps: 0

- Validation vs Test:
  - Exact overlaps: 0
  - Near-duplicate overlaps: 0

Internal duplicate verification also found zero duplicate groups within each final split.

Therefore, no exact or pHash-based near-duplicate leakage was detected within or between the final train, validation, and test splits.

## Supporting Scripts and Files

### Dataset Audit

- results/dataset_audit.txt
- results/duplicate_report.csv

### Split Creation

- src/create_split.py

### Split Verification

- src/check_split_leakage.py
- src/check_internal_duplicates.py

### Split Summary

- src/summarize_split.py
- results/split_summary.txt

### Project Documentation

- docs/PROJECT_CONTEXT.md

## EfficientNet-B0 Training

EfficientNet-B0 was selected as the initial model.

Actual training was performed on Kaggle GPU rather than the local PC.

### Training Configuration

- Architecture: EfficientNet-B0
- Image size: 224 x 224
- Initial backbone: ImageNet pretrained
- Batch size: 16
- Stage 1 learning rate: 1e-4
- Stage 1 maximum epochs: 20
- Stage 2: final 20 backbone layers unfrozen
- Stage 2 learning rate: 1e-5
- Stage 2 maximum epochs: 10
- Early stopping used
- Dense-layer L2 regularization: 1e-4
- Dropout: 0.3
- Augmentation:
  - horizontal flip
  - rotation: 0.05
  - zoom: 0.10

CLAHE preprocessing was not included in this training run and should not be claimed as part of the completed experiment.

### Saved Model

The trained model was saved locally as:

results/models/final_efficientnet_b0.keras

A backup copy is also stored locally:

results/models/final_efficientnet_b0_backup.keras

The model files are excluded from Git.

## EfficientNet-B0 Test Results

Evaluation was performed on the held-out test split.

### Test Set

- Test images: 108
- Abnormal(Ulcer): 71
- Normal(Healthy skin): 37

### Classification Results

- Accuracy: 99.07%
- Sensitivity: 100.00%
- Specificity: 97.30%
- Precision: 98.61%
- F1-score: 99.30%

### Confusion Matrix

- True negatives: 36
- False positives: 1
- False negatives: 0
- True positives: 71

### False Positive

The single false positive was:

- Image: 40.jpg
- True class: Normal(Healthy skin)
- Predicted class: Abnormal(Ulcer)
- Ulcer probability: 0.793048

Prediction sanity checking confirmed:

- Total test images: 108
- Incorrect predictions: 1

The model was retrained using the same configuration and produced the same test results.

## EfficientNet-B0 Result Files

The following files are stored locally under results/:

results/
    test_results.json
    y_true.npy
    y_prob.npy
    y_pred.npy

These files contain the saved test predictions and evaluation results.

## Grad-CAM Explainability

Grad-CAM was applied to the trained EfficientNet-B0 model.

The final convolutional layer used for Grad-CAM was:

top_conv

Three qualitative examples were generated from the held-out test set.

### True Positive

- Image: 101.jpg
- True class: Abnormal(Ulcer)
- Ulcer probability: 0.999725
- Output:

results/gradcam/gradcam_true_positive.jpg

### True Negative

- Image: 104.jpg
- True class: Normal(Healthy skin)
- Ulcer probability: 0.080493
- Output:

results/gradcam/gradcam_true_negative.jpg

### False Positive

- Image: 40.jpg
- True class: Normal(Healthy skin)
- Ulcer probability: 0.666203
- Output:

results/gradcam/gradcam_false_positive_40.jpg

### Grad-CAM Interpretation Limitation

These Grad-CAM examples are qualitative explainability examples only.

The current dataset does not contain clinician segmentation masks or validated lesion-region annotations.

Therefore, quantitative localization accuracy or agreement between Grad-CAM regions and clinician-defined wound regions cannot currently be assessed.

The Grad-CAM implementation is stored in:

src/gradcam.py

## Git / Version Control

The dataset is excluded from Git using .gitignore.

The following project components have been committed and pushed to GitHub:

- Project context
- Dataset audit
- Duplicate report
- Split creation script
- Split leakage verification script
- Split summary
- EfficientNet-B0 training pipeline
- EfficientNet-B0 test results
- Grad-CAM script
- Grad-CAM qualitative visualizations
- Supporting documentation

The local dataset and generated split remain outside Git.

### Recent Commits

- aa616ab — Update project context
- 904979a — Add EfficientNet training pipeline
- 9ecffa4 — Fix duplicate-free dataset split
- ad1c789 — Ignore dataset archives
- 6fc41fa — Add EfficientNet test results
- df1b954 — Add Grad-CAM explainability results

## Current Status

1. Dataset downloaded locally.
2. GitHub repository created.
3. Dataset excluded from Git using .gitignore.
4. Dataset audit completed.
5. Duplicate/near-duplicate audit completed.
6. Leakage-aware deduplicated split created.
7. Final split counts documented.
8. Cross-split leakage checked.
9. Internal duplicate verification completed.
10. No detected exact or pHash-based near-duplicate overlap within or between the final splits.
11. EfficientNet-B0 training completed on Kaggle GPU.
12. EfficientNet-B0 test evaluation completed.
13. Test predictions and evaluation results saved locally.
14. False-positive case identified.
15. Grad-CAM explainability analysis completed for qualitative examples.
16. Grad-CAM script and visualizations committed and pushed to GitHub.

## Important Limitations

Do not claim:

- Indian population-specific validation
- Fitzpatrick skin-tone analysis
- Wagner severity classification
- Clinician segmentation masks
- Patient-level splitting

unless appropriate supporting data is obtained.

The current dataset is not an Indian population-specific dataset.

The current dataset also lacks patient identifiers, so the final split cannot be described as patient-level splitting.

The current binary classification labels come from the Patches/Abnormal(Ulcer) and Patches/Normal(Healthy skin) folder structure.

Do not assume that the 167 images in the original TestSet folder have usable binary labels without verifying their labels/source.

The current test set of 108 images refers specifically to the final deduplicated data/DFU_split/test/ split.

## Proposed Research Workflow

The current research workflow is:

1. Dataset preparation
2. Duplicate and near-duplicate analysis
3. Leakage-aware splitting
4. Image preprocessing
5. Model training
6. Model evaluation
7. Explainability analysis
8. Baseline-model comparison
9. Statistical comparison
10. Mobile deployment/benchmarking
11. Paper preparation

## Training Environment

- Local development: Windows + VS Code
- Local hardware: 16 GB RAM, NVIDIA RTX 3050 6 GB VRAM
- Actual model training: Kaggle GPU
- Kaggle training hardware used: Tesla T4 GPUs
- Local machine is primarily used for dataset preparation, coding, debugging, documentation, and lightweight verification.

## Current Next Step

Proceed with baseline-model evaluation using the same final deduplicated train/validation/test split.

Candidate baseline architectures:

- ResNet50
- VGG16
- MobileNetV2
- InceptionV3

All baseline models should use the same final data split and a clearly documented evaluation protocol so their results can be compared consistently with EfficientNet-B0.

Do not change the test set between model comparisons.

Any comparison should report the same core metrics:

- Accuracy
- Sensitivity
- Specificity
- Precision
- F1-score

Additional metrics such as ROC-AUC and confidence intervals can be added after the basic baseline results are established.