# Experiment Log

Each experiment uses the fixed deduplicated split: `data/DFU_split/`

(train 494 / val 105 / test 108; abnormal=ulcer is the positive class).

Results are recorded only after an experiment has actually been run.

## ResNet50 Baseline

Status: COMPLETED

### Purpose

Evaluate ResNet50 as a CNN baseline against the primary EfficientNet-B0 model using the same final deduplicated dataset split and evaluation protocol.

### Dataset

Final deduplicated split:

- Train: 494 images
- Validation: 105 images
- Test: 108 images
- Test ulcer: 71
- Test healthy: 37

Class convention:

- 0 = Normal(Healthy skin)
- 1 = Abnormal(Ulcer)

### Model

- Model: ImageNet-pretrained ResNet50
- Input: 224x224 RGB
- Seed: 42
- Batch size: 16
- Preprocessing: tf.keras.applications.resnet50.preprocess_input
- Augmentation: horizontal flip, rotation 0.05, zoom 0.10
- Head: GlobalAveragePooling2D -> Dense(128, relu, L2 1e-4) -> Dropout(0.3) -> Dense(1, sigmoid)

### Training

Stage 1:

- Backbone frozen
- Adam learning rate: 1e-4
- Maximum epochs: 20
- Early stopping patience: 5
- Validation monitor: val_loss
- Restore best weights: yes

Stage 2:

- Final 20 backbone layers unfrozen
- Adam learning rate: 1e-5
- Maximum epochs: 10
- Early stopping patience: 3
- Validation monitor: val_loss
- Restore best weights: yes

Classification threshold: 0.5

### Test Results

- Accuracy: 99.07%
- Sensitivity: 100.00%
- Specificity: 97.30%
- Precision: 98.61%
- F1-score: 99.30%
- ROC-AUC: 1.0000

Confusion matrix:

TN = 36

FP = 1

FN = 0

TP = 71

### Output Files

Model:

`results/models/resnet50_model.keras`

Metrics:

`results/metrics/resnet50_results.json`

Predictions:

`results/predictions/resnet50_y_true.npy`

`results/predictions/resnet50_y_prob.npy`

`results/predictions/resnet50_y_pred.npy`

### Notes

ResNet50 produced the same test-set classification metrics as the completed EfficientNet-B0 experiment on the current 108-image test set.

These results should be interpreted in the context of the relatively small test set and the dataset limitations documented in DATASET_AUDIT.md.

## VGG16 Baseline

Status: COMPLETED

### Purpose

Evaluate VGG16 as a CNN baseline using the same final deduplicated dataset split and evaluation protocol.

### Dataset

Final deduplicated split:

- Train: 494 images
- Validation: 105 images
- Test: 108 images
- Test ulcer: 71
- Test healthy: 37

Class convention:

- 0 = Normal(Healthy skin)
- 1 = Abnormal(Ulcer)

### Model

- Model: ImageNet-pretrained VGG16
- Input: 224x224 RGB
- Seed: 42
- Batch size: 16
- Preprocessing: tf.keras.applications.vgg16.preprocess_input
- Augmentation: horizontal flip, rotation 0.05, zoom 0.10
- Head: GlobalAveragePooling2D -> Dense(128, relu, L2 1e-4) -> Dropout(0.3) -> Dense(1, sigmoid)

### Training

Stage 1:

- Backbone frozen
- Adam learning rate: 1e-4
- Maximum epochs: 20
- Early stopping patience: 5
- Validation monitor: val_loss
- Restore best weights: yes

Stage 2:

- Final 20 backbone layers unfrozen
- Adam learning rate: 1e-5
- Maximum epochs: 10
- Early stopping patience: 3
- Validation monitor: val_loss
- Restore best weights: yes

Classification threshold: 0.5

### Test Results

- Accuracy: 100.00%
- Sensitivity: 100.00%
- Specificity: 100.00%
- Precision: 100.00%
- F1-score: 100.00%
- ROC-AUC: 1.0000

Confusion matrix:

TN = 37

FP = 0

FN = 0

TP = 71

### Output Files

Model:

`results/models/vgg16_model.keras`

Metrics:

`results/metrics/vgg16_results.json`

Predictions:

`results/predictions/vgg16_y_true.npy`

`results/predictions/vgg16_y_prob.npy`

`results/predictions/vgg16_y_pred.npy`

### Notes

VGG16 achieved perfect classification on the current 108-image test set.

This result should be interpreted in the context of the relatively small test set and the dataset limitations documented in DATASET_AUDIT.md.

## MobileNetV2 Baseline

Status: COMPLETED

### Purpose

Evaluate MobileNetV2 as a lightweight CNN baseline using the same final deduplicated dataset split and evaluation protocol.

### Dataset

Final deduplicated split:

- Train: 494 images
- Validation: 105 images
- Test: 108 images
- Test ulcer: 71
- Test healthy: 37

Class convention:

- 0 = Normal(Healthy skin)
- 1 = Abnormal(Ulcer)

### Model

- Model: ImageNet-pretrained MobileNetV2
- Input: 224x224 RGB
- Seed: 42
- Batch size: 16
- Preprocessing: tf.keras.applications.mobilenet_v2.preprocess_input
- Preprocessing implemented as a Lambda layer after augmentation and before the backbone
- Augmentation: horizontal flip, rotation 0.05, zoom 0.10
- Head: GlobalAveragePooling2D -> Dense(128, relu, L2 1e-4) -> Dropout(0.3) -> Dense(1, sigmoid)

### Training

Stage 1:

- Backbone frozen
- Adam learning rate: 1e-4
- Maximum epochs: 20
- Early stopping patience: 5
- Validation monitor: val_loss
- Restore best weights: yes
- Completed all 20 epochs

Stage 2:

- Final 20 backbone layers unfrozen
- Adam learning rate: 1e-5
- Maximum epochs: 10
- Early stopping patience: 3
- Validation monitor: val_loss
- Restore best weights: yes
- Stopped after epoch 4

Classification threshold: 0.5

### Test Results

- Accuracy: 100.00%
- Sensitivity: 100.00%
- Specificity: 100.00%
- Precision: 100.00%
- F1-score: 100.00%
- ROC-AUC: 1.0000

Confusion matrix:

TN = 37

FP = 0

FN = 0

TP = 71

### Output Files

Model:

`results/models/mobilenetv2_model.keras`

Metrics:

`results/metrics/mobilenetv2_results.json`

Predictions:

`results/predictions/mobilenetv2_y_true.npy`

`results/predictions/mobilenetv2_y_prob.npy`

`results/predictions/mobilenetv2_y_pred.npy`

### Notes

MobileNetV2 achieved perfect classification on the current 108-image test set.

This result should be interpreted in the context of the relatively small test set and the dataset limitations documented in DATASET_AUDIT.md.

## InceptionV3 Baseline

Status: PLANNED - not yet run. Results must be produced on Kaggle GPU before any numbers are reported; nothing below is a measured result.

- Model: ImageNet-pretrained InceptionV3 (keras.applications.InceptionV3)
- Input: 224x224 RGB (uniform with other baselines; 224 is valid for InceptionV3, minimum 75, although its native design size is 299)
- Split: same fixed deduplicated split as EfficientNet-B0 (no new random split)
- Seed: 42
- Batch size: 16
- Preprocessing: `tf.keras.applications.inception_v3.preprocess_input` (Lambda) after augmentation, before the backbone (tf mode: [0, 255] -> [-1, 1])
- Assumption: keras.applications.InceptionV3 does not embed a Rescaling layer; verify first base-model layer names at runtime
- Augmentation: horizontal flip, rotation 0.05, zoom 0.10 (same as EfficientNet)
- Head: GlobalAveragePooling2D -> Dense(128, relu, L2 1e-4) -> Dropout(0.3) -> Dense(1, sigmoid)
- Stage 1: backbone frozen, Adam 1e-4, max 20 epochs, early stopping patience 5 (val_loss, restore best)
- Stage 2: last 20 backbone layers unfrozen, Adam 1e-5, max 10 epochs, early stopping patience 3
- Metrics (test set): accuracy, sensitivity, specificity, precision, F1, confusion matrix, ROC-AUC

Outputs:

- results/models/inceptionv3_model.keras
- results/inceptionv3_results.json
- results/inceptionv3_y_true.npy
- results/inceptionv3_y_prob.npy
- results/inceptionv3_y_pred.npy

Results: PENDING - to be filled after running on Kaggle.