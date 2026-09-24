import os
import json
import random
import numpy as np
import tensorflow as tf

from tensorflow.keras import layers, models, regularizers
from tensorflow.keras.applications import MobileNetV2
from tensorflow.keras.applications.mobilenet_v2 import preprocess_input
from tensorflow.keras.callbacks import EarlyStopping
from sklearn.metrics import (
    accuracy_score,
    confusion_matrix,
    precision_score,
    recall_score,
    f1_score,
    roc_auc_score,
)

# ============================================================
# Configuration
# ============================================================

DATA_DIR = "/kaggle/input/datasets/kailassharji/dfu-ai-split/DFU_split"
OUTPUT_DIR = "/kaggle/working/results"

IMG_SIZE = (224, 224)
BATCH_SIZE = 16
SEED = 42

os.makedirs(OUTPUT_DIR, exist_ok=True)
os.makedirs(os.path.join(OUTPUT_DIR, "models"), exist_ok=True)

# Reproducibility
random.seed(SEED)
np.random.seed(SEED)
tf.random.set_seed(SEED)

print("TensorFlow:", tf.__version__)
print("GPU devices:", tf.config.list_physical_devices("GPU"))

# ============================================================
# Dataset
# ============================================================

CLASS_NAMES = [
    "Normal(Healthy skin)",
    "Abnormal(Ulcer)",
]


def load_dataset(split):
    path = os.path.join(DATA_DIR, split)

    return tf.keras.utils.image_dataset_from_directory(
        path,
        labels="inferred",
        label_mode="binary",
        class_names=CLASS_NAMES,
        image_size=IMG_SIZE,
        batch_size=BATCH_SIZE,
        shuffle=(split == "train"),
        seed=SEED,
    )


train_ds = load_dataset("train")
val_ds = load_dataset("val")
test_ds = load_dataset("test")

print("\nClasses:", CLASS_NAMES)

# ============================================================
# Performance settings
# ============================================================

AUTOTUNE = tf.data.AUTOTUNE

train_ds = train_ds.prefetch(AUTOTUNE)
val_ds = val_ds.prefetch(AUTOTUNE)
test_ds = test_ds.prefetch(AUTOTUNE)

# ============================================================
# Data augmentation
# ============================================================

augmentation = tf.keras.Sequential(
    [
        layers.RandomFlip("horizontal"),
        layers.RandomRotation(0.05),
        layers.RandomZoom(0.10),
    ],
    name="augmentation",
)

# ============================================================
# Build MobileNetV2
# ============================================================

base_model = MobileNetV2(
    include_top=False,
    weights="imagenet",
    input_shape=(224, 224, 3),
)

print("\nMobileNetV2 first layers:")
for layer in base_model.layers[:5]:
    print(layer.name, layer.__class__.__name__)

print("\nMobileNetV2 last layers:")
for layer in base_model.layers[-5:]:
    print(layer.name, layer.__class__.__name__)

# Freeze backbone for Stage 1
base_model.trainable = False

inputs = layers.Input(shape=(224, 224, 3))

x = augmentation(inputs)

# MobileNetV2 requires preprocessing to [-1, 1].
# The preprocessing is applied explicitly so the experiment
# remains clear and reproducible.
x = layers.Lambda(
    preprocess_input,
    name="mobilenetv2_preprocess",
)(x)

x = base_model(x, training=False)

x = layers.GlobalAveragePooling2D()(x)

x = layers.Dense(
    128,
    activation="relu",
    kernel_regularizer=regularizers.l2(1e-4),
)(x)

x = layers.Dropout(0.3)(x)

outputs = layers.Dense(
    1,
    activation="sigmoid",
)(x)

model = models.Model(
    inputs,
    outputs,
    name="MobileNetV2_DFU",
)

model.summary()

# ============================================================
# Stage 1 — Frozen backbone
# ============================================================

model.compile(
    optimizer=tf.keras.optimizers.Adam(
        learning_rate=1e-4
    ),
    loss="binary_crossentropy",
    metrics=["accuracy"],
)

early_stop_stage1 = EarlyStopping(
    monitor="val_loss",
    patience=5,
    restore_best_weights=True,
)

print("\n==============================")
print("MobileNetV2 Stage 1")
print("==============================")

history1 = model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=20,
    callbacks=[early_stop_stage1],
)

# ============================================================
# Stage 2 — Fine-tune final 20 layers
# ============================================================

base_model.trainable = True

for layer in base_model.layers[:-20]:
    layer.trainable = False

for layer in base_model.layers[-20:]:
    layer.trainable = True

model.compile(
    optimizer=tf.keras.optimizers.Adam(
        learning_rate=1e-5
    ),
    loss="binary_crossentropy",
    metrics=["accuracy"],
)

early_stop_stage2 = EarlyStopping(
    monitor="val_loss",
    patience=3,
    restore_best_weights=True,
)

print("\n==============================")
print("MobileNetV2 Stage 2")
print("==============================")

history2 = model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=10,
    callbacks=[early_stop_stage2],
)

# ============================================================
# Test predictions
# ============================================================

print("\n==============================")
print("Evaluating MobileNetV2")
print("==============================")

y_true = []
y_prob = []

for images, labels in test_ds:
    probs = model.predict(
        images,
        verbose=0,
    ).ravel()

    y_prob.extend(probs)
    y_true.extend(
        labels.numpy().astype(int).ravel()
    )

y_true = np.array(y_true)
y_prob = np.array(y_prob)

y_pred = (y_prob >= 0.5).astype(int)

# ============================================================
# Metrics
# ============================================================

accuracy = accuracy_score(
    y_true,
    y_pred,
)

sensitivity = recall_score(
    y_true,
    y_pred,
    pos_label=1,
)

specificity = recall_score(
    y_true,
    y_pred,
    pos_label=0,
)

precision = precision_score(
    y_true,
    y_pred,
    zero_division=0,
)

f1 = f1_score(
    y_true,
    y_pred,
    zero_division=0,
)

roc_auc = roc_auc_score(
    y_true,
    y_prob,
)

cm = confusion_matrix(
    y_true,
    y_pred,
)

print("\n==============================")
print("MobileNetV2 TEST RESULTS")
print("==============================")

print(f"Accuracy:    {accuracy:.4f}")
print(f"Sensitivity: {sensitivity:.4f}")
print(f"Specificity: {specificity:.4f}")
print(f"Precision:   {precision:.4f}")
print(f"F1-score:    {f1:.4f}")
print(f"ROC-AUC:     {roc_auc:.4f}")

print("\nConfusion Matrix:")
print(cm)

# ============================================================
# Save model
# ============================================================

model_path = os.path.join(
    OUTPUT_DIR,
    "models",
    "mobilenetv2_model.keras",
)

model.save(model_path)

# ============================================================
# Save metrics
# ============================================================

results = {
    "model": "MobileNetV2",
    "input_size": [224, 224],
    "batch_size": BATCH_SIZE,
    "seed": SEED,
    "threshold": 0.5,
    "test_size": int(len(y_true)),
    "accuracy": float(accuracy),
    "sensitivity": float(sensitivity),
    "specificity": float(specificity),
    "precision": float(precision),
    "f1_score": float(f1),
    "roc_auc": float(roc_auc),
    "confusion_matrix": cm.tolist(),
}

results_path = os.path.join(
    OUTPUT_DIR,
    "mobilenetv2_results.json",
)

with open(results_path, "w") as f:
    json.dump(
        results,
        f,
        indent=2,
    )

# ============================================================
# Save predictions
# ============================================================

np.save(
    os.path.join(
        OUTPUT_DIR,
        "mobilenetv2_y_true.npy",
    ),
    y_true,
)

np.save(
    os.path.join(
        OUTPUT_DIR,
        "mobilenetv2_y_prob.npy",
    ),
    y_prob,
)

np.save(
    os.path.join(
        OUTPUT_DIR,
        "mobilenetv2_y_pred.npy",
    ),
    y_pred,
)

# ============================================================
# Final output
# ============================================================

print("\n==============================")
print("FILES SAVED")
print("==============================")

print(model_path)
print(results_path)
print(
    os.path.join(
        OUTPUT_DIR,
        "mobilenetv2_y_true.npy",
    )
)
print(
    os.path.join(
        OUTPUT_DIR,
        "mobilenetv2_y_prob.npy",
    )
)
print(
    os.path.join(
        OUTPUT_DIR,
        "mobilenetv2_y_pred.npy",
    )
)

print("\nMobileNetV2 training and evaluation completed.")