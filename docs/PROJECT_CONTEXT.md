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

DO NOT randomly split the 1,055 patches.

Exact and near duplicates must be grouped before train/validation/test assignment to reduce data leakage.

All members of a duplicate/near-duplicate group should remain in the same split.

## Leakage-Aware Split

A grouped split was created from the 1,055 labeled patches.

Location:

data/DFU_split/

### Final Split

#### Train

- Total: 726
- Abnormal(Ulcer): 351
- Normal(Healthy skin): 375

#### Validation

- Total: 162
- Abnormal(Ulcer): 81
- Normal(Healthy skin): 81

#### Test

- Total: 167
- Abnormal(Ulcer): 80
- Normal(Healthy skin): 87

#### Overall

- Total: 1,055

## Split Leakage Verification

Verification script:

src/check_split_leakage.py

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

Therefore, no exact or pHash-based near-duplicate leakage was detected between the current train, validation, and test splits.

## Supporting Scripts and Files

### Dataset Audit

- results/dataset_audit.txt
- results/duplicate_report.csv

### Split Creation

- src/create_split.py

### Split Verification

- src/check_split_leakage.py

### Split Summary

- src/summarize_split.py
- results/split_summary.txt

### Project Documentation

- docs/PROJECT_CONTEXT.md

## Git / Version Control

The dataset is excluded from Git using .gitignore.

The following project documentation and scripts have been committed and pushed to GitHub:

- Project context
- Dataset audit
- Duplicate report
- Split creation script
- Split leakage verification script
- Split summary
- Supporting documentation

The local dataset and generated split remain outside Git.

## Current Status

1. Dataset downloaded locally.
2. GitHub repository created.
3. Dataset excluded from Git using .gitignore.
4. Dataset audit completed.
5. Duplicate/near-duplicate audit completed.
6. Leakage-aware split created.
7. Split counts documented.
8. Cross-split leakage checked.
9. No detected exact or pHash-based near-duplicate overlap across splits.
10. Model training has NOT started.
11. Actual model training will be performed on Kaggle GPU rather than the local PC.

## Important Limitations

Do not claim:

- Indian population-specific validation
- Fitzpatrick skin-tone analysis
- Wagner severity classification
- Clinician segmentation masks
- Patient-level splitting

unless appropriate supporting data is obtained.

Also do not assume that the 167 images in TestSet have usable binary labels without verifying their labels/source.

## Planned Methodology

The initial model to be evaluated is EfficientNet-B0.

The proposed research workflow includes:

1. Dataset preparation
2. Image preprocessing
3. Model training
4. Model evaluation
5. Explainability analysis
6. Mobile deployment/benchmarking

The exact training configuration will be reviewed before execution.

## Training Environment

- Local development: Windows + VS Code
- Local hardware: 16 GB RAM, NVIDIA RTX 3050 6 GB VRAM
- Actual model training: Kaggle GPU
- Local machine will primarily be used for dataset preparation, coding, debugging, and lightweight verification.

## Current Next Step

Prepare and review the EfficientNet-B0 training pipeline for execution on Kaggle GPU.

Do not begin model training until:

- preprocessing is defined,
- training/validation/test usage is defined,
- augmentation is defined,
- evaluation metrics are defined,
- and the training script has been reviewed.