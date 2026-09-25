# Paper Notes

## Working Title
**AI-Based Mobile System for Early Diagnosis of Diabetic Foot Ulcers**

## 1. Paper Objective
Develop and evaluate an AI-based image classification system for distinguishing diabetic foot ulcer images from healthy-skin images, with model explainability and future mobile deployment.

Focus:
- Binary DFU classification
- Transfer learning with multiple CNNs
- Duplicate-controlled splitting
- Statistical evaluation
- Grad-CAM explainability
- Mobile deployment preparation

## 2. Research Questions
1. Can transfer-learning CNNs accurately classify ulcer vs healthy-skin images?
2. How consistently do different CNN architectures classify the same test images?
3. Can Grad-CAM provide useful visual explanations?
4. Can the selected model be converted for mobile deployment?

## 3. Dataset
Source:
- Kaggle dataset associated with the Alzubaidi DFU work
- Clinical images associated with Nasiriyah Hospital, Iraq
- No patient IDs, Fitzpatrick labels, Wagner grades, or clinician segmentation masks

Final cleaned dataset:
- Train: 494
- Validation: 105
- Test: 108
- Total retained: 707
- Classes: Normal(Healthy skin), Abnormal(Ulcer)

Duplicate and near-duplicate groups were consolidated before splitting. No exact or near-duplicate leakage was detected between splits.

## 4. Models
- EfficientNet-B0
- ResNet50
- VGG16
- MobileNetV2
- InceptionV3

All used ImageNet-pretrained weights and the same cleaned dataset split.

## 5. Main Results

| Model | Accuracy | Sensitivity | Specificity | Precision | F1 | ROC-AUC |
|---|---:|---:|---:|---:|---:|---:|
| EfficientNet-B0 | 99.07% | 100% | 97.30% | 98.61% | 99.30% | 1.000 |
| ResNet50 | 99.07% | 100% | 97.30% | 98.61% | 99.30% | 1.000 |
| VGG16 | 100% | 100% | 100% | 100% | 100% | 1.000 |
| MobileNetV2 | 100% | 100% | 100% | 100% | 100% | 1.000 |
| InceptionV3 | 99.07% | 100% | 97.30% | 98.61% | 99.30% | 1.000 |

Test set: 108 images (71 ulcer, 37 healthy).

## 6. Confidence Intervals
Exact binomial 95% CIs:
- Sensitivity, all models: 100.00% (94.94–100.00%)
- Specificity, EfficientNet-B0/ResNet50/InceptionV3: 97.30% (85.84–99.93%)
- Specificity, VGG16/MobileNetV2: 100.00% (90.51–100.00%)

Exact binomial intervals are preferred for reporting sensitivity and specificity.

## 7. Statistical Comparison
Pairwise exact McNemar tests used predictions from the same 108 test images.

No pairwise comparison was statistically significant at alpha = 0.05.

The small number of discordant predictions means this should not be interpreted as proof of statistical equivalence.

## 8. Prediction Agreement
Only three test cases contributed to disagreements among the five models.

Pairwise disagreement counts ranged from 0 to 2 images.

VGG16 and MobileNetV2 produced identical predictions across the test set.

These findings should be presented descriptively.

## 9. Explainability
Grad-CAM was generated for EfficientNet-B0 using:
- False-positive case
- True-positive case
- True-negative case

Grad-CAM remains qualitative because clinician segmentation masks/ROI annotations are unavailable.

The figures should demonstrate model attention rather than claim clinically validated localization.

## 10. Results Discussion
- All five models achieved very high test performance.
- EfficientNet-B0, ResNet50, and InceptionV3 each produced one false-positive prediction.
- VGG16 and MobileNetV2 produced zero classification errors.
- McNemar testing detected no significant pairwise differences.
- Results should be interpreted in the context of the small test set.
- Independent external validation is still required.

## 11. Key Limitations
- Relatively small dataset
- Not an Indian population-specific dataset
- No patient-level identifiers
- No Fitzpatrick skin-tone labels
- No Wagner severity labels
- No clinician segmentation masks
- Binary classification only
- No external validation
- Mobile deployment not yet benchmarked
- Grad-CAM not quantitatively validated against clinician ROIs

## 12. Claims to Avoid
Do not claim:
- Indian-specific dataset/population
- Fitzpatrick subgroup analysis
- Wagner-grade classification
- Clinician-validated segmentation
- Clinically validated ulcer localization
- Completed mobile deployment
- Clinical diagnostic replacement
- External or prospective clinical validation

Do not reuse the original abstract's 96.2% accuracy, 94.8% sensitivity, or 95.5% specificity unless their experimental source is independently verified.

## 13. Mobile Deployment — Pending
Planned:
- TensorFlow Lite conversion
- Model loading/inference testing
- Model-size comparison
- Inference-latency measurement
- Quantization experiments
- Post-conversion accuracy comparison

No mobile performance claims should be made before these experiments.

## 14. Future Work
- External validation
- Larger and more diverse datasets
- Patient-level metadata
- Skin-tone representation
- Severity classification
- Clinician ROI/segmentation annotations
- Quantitative explainability evaluation
- Mobile benchmarking
- Prospective clinical evaluation

## 15. Current Status
Completed:
- Dataset audit and cleaning
- Duplicate-controlled split
- Five CNN experiments
- Test evaluation
- Model comparison
- Confidence intervals
- McNemar analysis
- EfficientNet Grad-CAM
- Core methodology documentation

Pending:
- TFLite/mobile experiments
- Final deployment-oriented model selection
- Final figures/tables
- Paper writing and references

## 16. Evidence Rule
Every numerical paper result must trace to:
- `EXPERIMENT_LOG.md`
- Files under `results/`
- The corresponding script under `src/`

Unsupported claims from the original proposal must not be presented as current results.