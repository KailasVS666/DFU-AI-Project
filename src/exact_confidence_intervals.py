import numpy as np
from pathlib import Path
from scipy.stats import beta

PRED_DIR = Path("results/predictions")

FILES = {
    "EfficientNet-B0": "y_pred.npy",
    "ResNet50": "resnet50_y_pred.npy",
    "VGG16": "vgg16_y_pred.npy",
    "MobileNetV2": "mobilenetv2_y_pred.npy",
    "InceptionV3": "inceptionv3_y_pred.npy",
}

y_true = np.load(PRED_DIR / "y_true.npy")


def exact_binomial_ci(successes, total, confidence=0.95):
    alpha = 1 - confidence

    if successes == 0:
        lower = 0.0
    else:
        lower = beta.ppf(alpha / 2, successes, total - successes + 1)

    if successes == total:
        upper = 1.0
    else:
        upper = beta.ppf(1 - alpha / 2, successes + 1, total - successes)

    return lower, upper


print("\nExact 95% Binomial Confidence Intervals")
print("=" * 90)

for model, filename in FILES.items():

    y_pred = np.load(PRED_DIR / filename)

    true_positive = np.sum((y_true == 1) & (y_pred == 1))
    false_negative = np.sum((y_true == 1) & (y_pred == 0))

    true_negative = np.sum((y_true == 0) & (y_pred == 0))
    false_positive = np.sum((y_true == 0) & (y_pred == 1))

    positives = true_positive + false_negative
    negatives = true_negative + false_positive

    sensitivity = true_positive / positives
    specificity = true_negative / negatives

    sens_low, sens_high = exact_binomial_ci(true_positive, positives)
    spec_low, spec_high = exact_binomial_ci(true_negative, negatives)

    print(f"\n{model}")
    print("-" * 60)

    print(
        f"Sensitivity: {sensitivity * 100:.2f}% "
        f"(95% CI: {sens_low * 100:.2f}% - {sens_high * 100:.2f}%)"
    )

    print(
        f"Specificity: {specificity * 100:.2f}% "
        f"(95% CI: {spec_low * 100:.2f}% - {spec_high * 100:.2f}%)"
    )

print("\nTest set: 71 ulcer images, 37 healthy images")
print("=" * 90)