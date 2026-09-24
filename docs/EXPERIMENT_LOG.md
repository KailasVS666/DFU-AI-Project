# Experiment Log

Each experiment uses the fixed deduplicated split: `data/DFU_split/`
(train 494 / val 105 / test 108; abnormal=ulcer is the positive class).
Results are recorded only after an experiment has actually been run.

## ResNet50 Baseline

Status: PLANNED - not yet run. Results must be produced on Kaggle GPU before any
numbers are reported; nothing below is a measured result.

- Model: ImageNet-pretrained ResNet50 (keras.applications.ResNet50)
- Input: 224x224 RGB
- Split: same fixed deduplicated split as EfficientNet-B0 (no new random split)
- Seed: 42
- Batch size: 16
- Preprocessing: `tf.keras.applications.resnet50.preprocess_input` (Lambda) after
  augmentation, before the backbone (required for ImageNet weights)
- Augmentation: horizontal flip, rotation 0.05, zoom 0.10 (same as EfficientNet)
- Head: GlobalAveragePooling2D -> Dense(128, relu, L2 1e-4) -> Dropout(0.3) -> Dense(1, sigmoid)
- Stage 1: backbone frozen, Adam 1e-4, max 20 epochs, early stopping patience 5 (val_loss, restore best)
- Stage 2: last 20 backbone layers unfrozen, Adam 1e-5, max 10 epochs, early stopping patience 3
- Metrics (test set): accuracy, sensitivity, specificity, precision, F1, confusion matrix, ROC-AUC

Outputs:
- results/models/resnet50_model.keras
- results/resnet50_results.json
- results/resnet50_y_true.npy
- results/resnet50_y_prob.npy
- results/resnet50_y_pred.npy

Results: PENDING - to be filled after running on Kaggle.

## VGG16 Baseline

Status: PLANNED - not yet run. Results must be produced on Kaggle GPU before any
numbers are reported; nothing below is a measured result.

- Model: ImageNet-pretrained VGG16 (keras.applications.VGG16)
- Input: 224x224 RGB
- Split: same fixed deduplicated split as EfficientNet-B0 (no new random split)
- Seed: 42
- Batch size: 16
- Preprocessing: `tf.keras.applications.vgg16.preprocess_input` (Lambda) after
  augmentation, before the backbone (caffe mode: RGB->BGR + ImageNet channel mean subtraction)
- Augmentation: horizontal flip, rotation 0.05, zoom 0.10 (same as EfficientNet)
- Head: GlobalAveragePooling2D -> Dense(128, relu, L2 1e-4) -> Dropout(0.3) -> Dense(1, sigmoid)
- Stage 1: backbone frozen, Adam 1e-4, max 20 epochs, early stopping patience 5 (val_loss, restore best)
- Stage 2: last 20 backbone layers unfrozen, Adam 1e-5, max 10 epochs, early stopping patience 3
- Metrics (test set): accuracy, sensitivity, specificity, precision, F1, confusion matrix, ROC-AUC
- Note: heaviest baseline (~138M params); requires Kaggle GPU

Outputs:
- results/models/vgg16_model.keras
- results/vgg16_results.json
- results/vgg16_y_true.npy
- results/vgg16_y_prob.npy
- results/vgg16_y_pred.npy

Results: PENDING - to be filled after running on Kaggle.

## MobileNetV2 Baseline

Status: PLANNED - not yet run. Results must be produced on Kaggle GPU before any
numbers are reported; nothing below is a measured result.

- Model: ImageNet-pretrained MobileNetV2 (keras.applications.MobileNetV2)
- Input: 224x224 RGB
- Split: same fixed deduplicated split as EfficientNet-B0 (no new random split)
- Seed: 42
- Batch size: 16
- Preprocessing: `tf.keras.applications.mobilenet_v2.preprocess_input` (Lambda) after
  augmentation, before the backbone (tf mode: [0, 255] -> [-1, 1])
- Assumption: keras.applications.MobileNetV2 does not embed a Rescaling layer
  (unlike EfficientNet/MobileNetV3); verify first base-model layer names at runtime
- Augmentation: horizontal flip, rotation 0.05, zoom 0.10 (same as EfficientNet)
- Head: GlobalAveragePooling2D -> Dense(128, relu, L2 1e-4) -> Dropout(0.3) -> Dense(1, sigmoid)
- Stage 1: backbone frozen, Adam 1e-4, max 20 epochs, early stopping patience 5 (val_loss, restore best)
- Stage 2: last 20 backbone layers unfrozen, Adam 1e-5, max 10 epochs, early stopping patience 3
- Metrics (test set): accuracy, sensitivity, specificity, precision, F1, confusion matrix, ROC-AUC

Outputs:
- results/models/mobilenetv2_model.keras
- results/mobilenetv2_results.json
- results/mobilenetv2_y_true.npy
- results/mobilenetv2_y_prob.npy
- results/mobilenetv2_y_pred.npy

Results: PENDING - to be filled after running on Kaggle.

## InceptionV3 Baseline

Status: PLANNED - not yet run. Results must be produced on Kaggle GPU before any
numbers are reported; nothing below is a measured result.

- Model: ImageNet-pretrained InceptionV3 (keras.applications.InceptionV3)
- Input: 224x224 RGB (uniform with other baselines; 224 is valid for InceptionV3,
  minimum 75, although its native design size is 299)
- Split: same fixed deduplicated split as EfficientNet-B0 (no new random split)
- Seed: 42
- Batch size: 16
- Preprocessing: `tf.keras.applications.inception_v3.preprocess_input` (Lambda) after
  augmentation, before the backbone (tf mode: [0, 255] -> [-1, 1])
- Assumption: keras.applications.InceptionV3 does not embed a Rescaling layer;
  verify first base-model layer names at runtime
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