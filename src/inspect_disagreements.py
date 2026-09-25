import numpy as np
from pathlib import Path

PRED_DIR = Path("results/predictions")

FILES = {
    "EfficientNet-B0": "y_prob.npy",
    "ResNet50": "resnet50_y_prob.npy",
    "VGG16": "vgg16_y_prob.npy",
    "MobileNetV2": "mobilenetv2_y_prob.npy",
    "InceptionV3": "inceptionv3_y_prob.npy",
}

indices = [2, 16, 33]

print("\nProbability Analysis of Disagreement Cases")
print("=" * 100)

print(
    f"{'Index':<8}"
    + "".join(f"{name:>20}" for name in FILES)
)

print("-" * 100)

probabilities = {
    name: np.load(PRED_DIR / filename)
    for name, filename in FILES.items()
}

for idx in indices:
    print(
        f"{idx:<8}"
        + "".join(f"{probabilities[name][idx]:>20.6f}" for name in FILES)
    )

print("=" * 100)
print("\nTrue label: 1 (Abnormal/Ucler) for all three cases.")
print("Prediction threshold: 0.50")