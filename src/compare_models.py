import json
from pathlib import Path

RESULTS_DIR = Path("results/metrics")

FILES = {
    "EfficientNet-B0": "test_results.json",
    "ResNet50": "resnet50_results.json",
    "VGG16": "vgg16_results.json",
    "MobileNetV2": "mobilenetv2_results.json",
    "InceptionV3": "inceptionv3_results.json",
}


def get_metric(data, *keys):
    for key in keys:
        if key in data:
            return data[key]
    return None


print("\nModel Comparison")
print("=" * 100)

print(
    f"{'Model':<18}"
    f"{'Accuracy':>12}"
    f"{'Sensitivity':>14}"
    f"{'Specificity':>14}"
    f"{'Precision':>12}"
    f"{'F1':>10}"
    f"{'ROC-AUC':>12}"
)

print("-" * 100)

for model_name, filename in FILES.items():

    path = RESULTS_DIR / filename

    with open(path, "r") as f:
        data = json.load(f)

    accuracy = get_metric(data, "accuracy")
    sensitivity = get_metric(data, "sensitivity", "recall")
    specificity = get_metric(data, "specificity")
    precision = get_metric(data, "precision")
    f1 = get_metric(data, "f1_score", "f1")
    roc_auc = get_metric(data, "roc_auc", "roc_auc_score")

    def percent(value):
        return f"{value * 100:.2f}%" if value is not None else "N/A"

    def decimal(value):
        return f"{value:.4f}" if value is not None else "N/A"

    print(
        f"{model_name:<18}"
        f"{percent(accuracy):>12}"
        f"{percent(sensitivity):>14}"
        f"{percent(specificity):>14}"
        f"{percent(precision):>12}"
        f"{percent(f1):>10}"
        f"{decimal(roc_auc):>12}"
    )

print("=" * 100)