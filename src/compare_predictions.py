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

print("\nPrediction Agreement")
print("=" * 70)

names = list(predictions.keys())

for i in range(len(names)):
    for j in range(i + 1, len(names)):
        a = predictions[names[i]]
        b = predictions[names[j]]

        disagreements = np.sum(a != b)

        print(
            f"{names[i]:<18} vs {names[j]:<18} "
            f"disagreements: {disagreements}"
        )

print("=" * 70)