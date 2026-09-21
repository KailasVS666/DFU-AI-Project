# DFU-AI Project Context

## Project
AI-Based Mobile System for Early Diagnosis of Diabetic Foot Ulcers (DFU).

Goal: develop and evaluate an AI system for binary diabetic-foot-ulcer image classification, with eventual consideration of explainability and mobile deployment.

## Current Role Split
- ChatGPT: research, methodology, statistics, experiment design, interpretation, paper support, coding guidance.
- VS Code: development and execution.
- GitHub: version control.
- Dataset remains local and is excluded from Git.

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

Important provenance limitation:
- Do NOT describe this dataset as an Indian dataset.
- Source literature indicates the original clinical images are associated with Nasiriyah Hospital, Iraq.
- No patient metadata, Fitzpatrick labels, Wagner grades, or segmentation masks were found in the downloaded dataset.

### Labeled Patches
data/DFU/Patches/

- Abnormal(Ulcer): 512
- Normal(Healthy skin): 543
- Total: 1,055

These folder names are the available binary labels.

Other folders:
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

Key findings:
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

Exact and near duplicates must be grouped before train/validation/test splitting to reduce data leakage.

All members of a duplicate/near-duplicate group should remain in the same split.

## Current Status
1. Dataset downloaded locally.
2. GitHub repository created.
3. Dataset excluded from Git using .gitignore.
4. Dataset audit completed.
5. Duplicate/near-duplicate audit completed.
6. Leakage-safe splitting is the next technical step.
7. Model training has NOT started.

## Important Limitations
Do not claim:
- Indian population-specific validation
- Fitzpatrick skin-tone analysis
- Wagner severity classification
- clinician segmentation masks
- patient-level splitting

unless appropriate supporting data is obtained.

## Next Step
Create and verify a leakage-aware train/validation/test split from the labeled Patches dataset.

Do not train the model until the split has been checked.