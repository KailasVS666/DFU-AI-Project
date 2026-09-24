# DFU-AI Project Context

## Project

Working title: AI-Based Mobile System for Early Diagnosis of Diabetic Foot Ulcers (DFU)

Goal: develop and evaluate an AI system for binary diabetic-foot-ulcer image classification, with eventual consideration of explainability and mobile deployment.

## Current Role Split

- ChatGPT: research, methodology, statistics, experiment design, interpretation, paper support, coding guidance.
- VS Code: development, scripts, debugging, documentation.
- GitHub: version control.
- Kaggle GPU: actual model training and computationally heavier experiments.
- Dataset: local/Kaggle only; excluded from Git.

## Repository

DFU-AI-Project/
├── data/
│   └── DFU/
├── src/
├── results/
├── notebooks/
└── docs/

## Dataset

Current dataset:

Kaggle/Alzubaidi-associated DFU dataset

Important provenance limitation:

- Do not describe this as an Indian dataset.
- Source literature indicates the original clinical images are associated with Nasiriyah Hospital, Iraq.
- No patient metadata, Fitzpatrick labels, Wagner grades, or segmentation masks were found in the downloaded dataset.

### Original labeled patches

Abnormal(Ulcer):       512
Normal(Healthy skin):  543
Total:                1055

The binary labels come from the folder structure.

Other dataset folders include Original Images, TestSet, and Transfer-Learning images. Their labels should not be inferred without verification.

## Final Dataset Split

The 1,055 labeled patches were audited for exact duplicates and near-duplicates before splitting.

Duplicate grouping used:

- SHA-256 exact hashing
- perceptual hashing (pHash), threshold <= 6

Each duplicate/near-duplicate group contributed one representative image to the final split.

Final split:

Split       Total    Ulcer    Healthy
Train       494      327      167
Validation  105       70       35
Test        108       71       37
Total       707

No exact or pHash-based near-duplicate overlap was detected within or between the final splits.

Important: the 707 retained images should not be described as 707 independent patient images. The dataset has no patient identifiers, so patient-level splitting cannot be claimed.

Detailed dataset information is maintained in:

docs/DATASET_AUDIT.md

## Label Convention

All model experiments use:

0 = Normal(Healthy skin)
1 = Abnormal(Ulcer)

Therefore, model sigmoid probability represents the probability of the ulcer class.

This class ordering must remain consistent across all models.

## Main Model: EfficientNet-B0

EfficientNet-B0 was the initial/main model.

Training was performed on Kaggle GPU.

Configuration:

- Input: 224 x 224
- ImageNet pretrained weights
- Batch size: 16
- Stage 1 learning rate: 1e-4
- Stage 1 maximum epochs: 20
- Stage 2: final 20 backbone layers unfrozen
- Stage 2 learning rate: 1e-5
- Stage 2 maximum epochs: 10
- Early stopping
- L2 regularization: 1e-4
- Dropout: 0.3
- Horizontal flip
- Random rotation: 0.05
- Random zoom: 0.10

CLAHE was not used in this completed training run.

### EfficientNet-B0 Test Results

Test set: 108 images.

Metric        Result
Accuracy      99.07%
Sensitivity   100.00%
Specificity   97.30%
Precision     98.61%
F1-score      99.30%

Confusion matrix:

TN = 36
FP = 1
FN = 0
TP = 71

One false-positive case was identified:

Image: 40.jpg
True: Normal(Healthy skin)
Predicted: Abnormal(Ulcer)
Ulcer probability: 0.793048

The model was retrained using the same configuration and produced the same test results.

Detailed experiment information belongs in:

docs/EXPERIMENT_LOG.md

## Explainability

Grad-CAM was applied to EfficientNet-B0 using the final convolutional layer:

top_conv

Three qualitative examples were generated:

- True positive: 101.jpg
- True negative: 104.jpg
- False positive: 40.jpg

Outputs are stored under:

results/gradcam/

Important limitation:

The dataset does not contain clinician segmentation masks or validated lesion-region annotations. Therefore, Grad-CAM results are qualitative explainability examples only and cannot currently be evaluated using quantitative localization agreement.

Implementation:

src/gradcam.py

## Baseline Models

The following baseline architectures are planned:

1. ResNet50
2. VGG16
3. MobileNetV2
4. InceptionV3

All baselines should use:

- The same final deduplicated split
- Same class ordering
- 224 x 224 input
- Consistent training/evaluation protocol
- Same core evaluation metrics

The test set must not change between model comparisons.

### ResNet50

ResNet50 baseline training has been completed.

Configuration follows the same general two-stage protocol:

- ImageNet pretrained
- Batch size: 16
- Stage 1 learning rate: 1e-4
- Stage 2 learning rate: 1e-5
- Final 20 backbone layers fine-tuned
- Same augmentation
- Classification threshold: 0.5

Test results:

Metric        Result
Accuracy      99.07%
Sensitivity   100.00%
Specificity   97.30%
Precision     98.61%
F1-score      99.30%
ROC-AUC       1.0000

Confusion matrix:

TN = 36
FP = 1
FN = 0
TP = 71

Saved result files are under:

results/models/
results/metrics/
results/predictions/

Detailed experiment records belong in:

docs/EXPERIMENT_LOG.md

## Methodology Status

The broader paper methodology originally proposed:

- Data collection
- Clinical annotation
- Fitzpatrick skin-tone information
- Wagner severity grading
- Segmentation/ROI information
- Image preprocessing
- CNN training
- Explainable AI
- Mobile deployment
- Statistical comparison

However, several of these components are not supported by the current dataset.

Therefore, the paper must not claim:

- Indian population-specific validation
- Fitzpatrick skin-tone analysis
- Wagner severity classification
- Clinician segmentation masks
- Patient-level splitting

unless appropriate supporting data is obtained.

The finalized methodology should be maintained separately in:

docs/METHODOLOGY.md

## Paper Status

The project currently has:

- Dataset audit completed
- Duplicate/near-duplicate analysis completed
- Leakage-aware split completed
- EfficientNet-B0 trained and evaluated
- ResNet50 baseline trained and evaluated
- Grad-CAM qualitative analysis completed
- VGG16 baseline pending
- MobileNetV2 baseline pending
- InceptionV3 baseline pending
- Statistical comparison pending
- Final paper tables/figures pending
- Mobile deployment/benchmarking pending

Paper-specific claims, tables, figures, discussion points, and limitations should be maintained in:

docs/PAPER_NOTES.md

## Current Workflow

Dataset audit
      ↓
Duplicate / near-duplicate analysis
      ↓
Leakage-aware split
      ↓
EfficientNet-B0
      ↓
Baseline models
      ↓
Statistical comparison
      ↓
Error analysis
      ↓
Grad-CAM / explainability
      ↓
Mobile deployment / benchmarking
      ↓
Paper preparation

## Important Rules

1. Do not change the final test set between model experiments.
2. Keep class ordering fixed:
   0 = Normal(Healthy skin)
   1 = Abnormal(Ulcer)
3. Do not claim unsupported clinical metadata.
4. Do not describe the current dataset as Indian.
5. Do not describe the 707 retained images as independent patient images.
6. Keep actual training on Kaggle GPU.
7. Keep scripts and documentation version-controlled in GitHub.
8. Record every completed experiment in docs/EXPERIMENT_LOG.md.
9. Do not fabricate missing results or metadata.
10. Interpret the current results in the context of the relatively small test set.

## Key Files

docs/
├── PROJECT_CONTEXT.md
├── DATASET_AUDIT.md
├── EXPERIMENT_LOG.md
├── METHODOLOGY.md
└── PAPER_NOTES.md

Main scripts:

src/
├── create_split.py
├── check_split_leakage.py
├── check_internal_duplicates.py
├── summarize_split.py
├── gradcam.py
├── train_resnet50.py
├── train_vgg16.py
├── train_mobilenetv2.py
└── train_inceptionv3.py

## Current Next Step

Continue baseline evaluation with:

VGG16 → MobileNetV2 → InceptionV3

using the same final deduplicated dataset split and evaluation protocol.