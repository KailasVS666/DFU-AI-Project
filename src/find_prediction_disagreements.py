import numpy as np
from pathlib import Path

PRED_DIR = Path("results/predictions")

FILES = {
    "EfficientNet-B0": "y_pred.npy",
    "ResNet50": "resnet50_y_pred.npy",
    "VGG16": "vgg16_y_pred.npy",
    "MobileNetV2": "mobilenetv2_y_pred.npy",
    "InceptionV3": "inceptionv3_y_pred.npy",
}

predictions = {
    name: np.load(PRED_DIR / filename)
    for name, filename in FILES.items()
}

names = list(predictions.keys())

print("\nImages with Prediction Disagreements")
print("=" * 80)

all_disagreement_indices = set()

for i in range(len(names)):
    for j in range(i + 1, len(names)):
        a = predictions[names[i]]
        b = predictions[names[j]]

        indices = np.where(a != b)[0]

        for idx in indices:
            all_disagreement_indices.add(int(idx))

print(f"Total images involved: {len(all_disagreement_indices)}")
print()

for idx in sorted(all_disagreement_indices):
    print(f"Test index {idx}:")
    for name in names:
        print(f"  {name:<18}: {predictions[name][idx]}")
    print()

print("=" * 80)