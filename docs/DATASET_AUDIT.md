# DATASET AUDIT

## 1. Dataset Overview

**Working dataset:** DFU dataset associated with Alzubaidi et al.  
**Project source:** Kaggle / Google Drive copy  
**Associated location:** Iraq

**Important:** The current dataset is not an Indian DFU dataset.

## 2. Original Dataset Structure

| Folder | Images |
|---|---:|
| Original Images | 493 |
| Patches/Abnormal(Ulcer) | 512 |
| Patches/Normal(Healthy skin) | 543 |
| TestSet | 167 |

Additional folders:
- `internetSet`
- `samples`
- `Wound Images`
- `Wound Images2`

These additional folders are not part of the final experimental split.

## 3. Available Labels

Usable classification labels come from the folder structure:
- `Normal(Healthy skin)`
- `Abnormal(Ulcer)`

Project convention:
- `0 = Normal(Healthy skin)`
- `1 = Abnormal(Ulcer)`

No separate annotation file containing additional clinical labels was found.

## 4. Metadata Audit

No usable files were found providing:
- Patient IDs
- Fitzpatrick skin-tone labels
- Wagner grades
- Segmentation masks
- Clinician ROI annotations
- Patient-level demographic information

Therefore, the current project cannot perform verified:
- Patient-level analysis
- Fitzpatrick subgroup analysis
- Wagner severity analysis
- Quantitative segmentation evaluation
- Quantitative Grad-CAM localization evaluation

## 5. Duplicate Analysis

Duplicate checking used:
1. SHA-256 exact hashing
2. Perceptual hashing (pHash)
3. Connected-component grouping

The initial split contained substantial internal duplicate/near-duplicate groups.

The original splitting approach was replaced with a representative-based approach.

## 6. Cleaning Strategy

Images belonging to the same exact or near-duplicate group were grouped together.

One representative was retained from each connected duplicate/near-duplicate group.

Final dataset:

**707 retained image representatives/groups**

This does not imply 707 independent patients.

## 7. Final Dataset Split

| Split | Healthy | Ulcer | Total |
|---|---:|---:|---:|
| Train | 167 | 327 | 494 |
| Validation | 35 | 70 | 105 |
| Test | 37 | 71 | 108 |
| **Total** | **239** | **468** | **707** |

Class proportions were retained as closely as practical during splitting.

## 8. Leakage Check

Exact and near-duplicate comparisons were performed between every split pair.

| Comparison | Exact | Near-Duplicate |
|---|---:|---:|
| Train vs Validation | 0 | 0 |
| Train vs Test | 0 | 0 |
| Validation vs Test | 0 | 0 |

No detected exact or near-duplicate cross-split leakage was found according to the implemented checks.

## 9. Kaggle Dataset

The cleaned split was uploaded as a private Kaggle dataset.

**Dataset:** `DFU AI Split`

**Training path:**

`/kaggle/input/datasets/kailassharji/dfu-ai-split/DFU_split`

**Version:** `Clean Deduplicated Split v2`

The dataset contains the cleaned train, validation, and test sets.

## 10. Kaggle Verification

Verified before model training:
- Train: 494
- Validation: 105
- Test: 108
- Test files: 108
- Test unique files: 108
- Detected test duplicate groups: 0

Class ordering was explicitly verified during training.

## 11. Dataset Interpretation

The dataset contains image-level classification examples.

There is no current evidence that each retained image corresponds to a unique patient.

Therefore:
- Images must not be described as patients.
- Patient-level independence must not be assumed.
- Patient-level clinical statistics must not be reported.
- Population prevalence claims must not be derived from this dataset.

## 12. Dataset Limitations

1. Not India-specific.
2. Patient identifiers unavailable.
3. Fitzpatrick labels unavailable.
4. Wagner severity labels unavailable.
5. Clinician segmentation masks unavailable.
6. Clinician ROI annotations unavailable.
7. External validation data unavailable.
8. Test set contains only 108 images.
9. Population, acquisition, and source-specific biases may exist.

These limitations must be reflected in the final paper.

## 13. Reproducibility

Dataset scripts are stored under:

`src/`

Important scripts:
- `create_split.py`
- `check_internal_duplicates.py`
- `check_split_leakage.py`
- `summarize_split.py`

The cleaned dataset is excluded from Git because of size.

## 14. Current Dataset Status

**Status: COMPLETE**

Completed:
- Dataset audit
- Cleaning
- Deduplication
- Dataset splitting
- Cross-split leakage checking
- Kaggle upload
- Kaggle verification

No further dataset changes should be made without recording the reason and creating a new documented dataset version.