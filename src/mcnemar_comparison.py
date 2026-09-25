import numpy as np
from pathlib import Path
from itertools import combinations
from statsmodels.stats.contingency_tables import mcnemar

PRED_DIR = Path("results/predictions")

FILES = {
    "EfficientNet-B0": "y_pred.npy",
    "ResNet50": "resnet50_y_pred.npy",
    "VGG16": "vgg16_y_pred.npy",
    "MobileNetV2": "mobilenetv2_y_pred.npy",
    "InceptionV3": "inceptionv3_y_pred.npy",
}

y_true = np.load(PRED_DIR / "y_true.npy")

predictions = {
    name: np.load(PRED_DIR / filename)
    for name, filename in FILES.items()
}

print("\nPairwise McNemar Tests")
print("=" * 90)

for (name_a, pred_a), (name_b, pred_b) in combinations(
    predictions.items(), 2
):
    a_correct = pred_a == y_true
    b_correct = pred_b == y_true

    both_correct = np.sum(a_correct & b_correct)
    a_correct_b_wrong = np.sum(a_correct & ~b_correct)
    a_wrong_b_correct = np.sum(~a_correct & b_correct)
    both_wrong = np.sum(~a_correct & ~b_correct)

    table = [
        [both_correct, a_correct_b_wrong],
        [a_wrong_b_correct, both_wrong],
    ]

    result = mcnemar(table, exact=True)

    print(
        f"{name_a:<18} vs {name_b:<18} "
        f"discordant={a_correct_b_wrong + a_wrong_b_correct:<3} "
        f"p={result.pvalue:.4f}"
    )

print("=" * 90)
print("Significance level: alpha = 0.05")