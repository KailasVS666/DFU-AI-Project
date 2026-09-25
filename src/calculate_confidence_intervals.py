import numpy as np
from pathlib import Path
from sklearn.metrics import accuracy_score, precision_score, recall_score, f1_score
from scipy.stats import bootstrap

PRED_DIR = Path("results/predictions")

FILES = {
    "EfficientNet-B0": "y_pred.npy",
    "ResNet50": "resnet50_y_pred.npy",
    "VGG16": "vgg16_y_pred.npy",
    "MobileNetV2": "mobilenetv2_y_pred.npy",
    "InceptionV3": "inceptionv3_y_pred.npy",
}

y_true = np.load(PRED_DIR / "y_true.npy")


def bootstrap_ci(metric_function, y_true, y_pred, n_bootstrap=10000):
    rng = np.random.default_rng(42)
    n = len(y_true)
    values = []

    for _ in range(n_bootstrap):
        indices = rng.integers(0, n, n)
        values.append(
            metric_function(y_true[indices], y_pred[indices])
        )

    return np.percentile(values, [2.5, 97.5])


metrics = {
    "Accuracy": accuracy_score,
    "Sensitivity": lambda y, p: recall_score(y, p, pos_label=1, zero_division=0),
    "Specificity": lambda y, p: recall_score(y, p, pos_label=0, zero_division=0),
    "Precision": lambda y, p: precision_score(y, p, pos_label=1, zero_division=0),
    "F1": f1_score,
}

print("\n95% Bootstrap Confidence Intervals")
print("=" * 110)

for model, filename in FILES.items():

    y_pred = np.load(PRED_DIR / filename)

    print(f"\n{model}")
    print("-" * 70)

    for metric_name, metric_function in metrics.items():

        point = metric_function(y_true, y_pred)

        lower, upper = bootstrap_ci(
            metric_function,
            y_true,
            y_pred
        )

        print(
            f"{metric_name:<15} "
            f"{point * 100:6.2f}% "
            f"(95% CI: {lower * 100:6.2f}% - {upper * 100:6.2f}%)"
        )

print("=" * 110)