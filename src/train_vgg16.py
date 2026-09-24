import json

import numpy as np
import tensorflow as tf
from pathlib import Path
from sklearn.metrics import (
    accuracy_score,
    precision_score,
    recall_score,
    f1_score,
    confusion_matrix,
    roc_auc_score,
)

# ============================================================
# Configuration
# ============================================================

DATA_DIR = Path("data/DFU_split")
IMG_SIZE = (224, 224)
BATCH_SIZE = 16
SEED = 42

INITIAL_EPOCHS = 20
FINETUNE_EPOCHS = 10

INITIAL_LR = 1e-4
FINETUNE_LR = 1e-5

RESULT_DIR = Path("results")
MODEL_DIR = RESULT_DIR / "models"

# ============================================================
# Load datasets
# ============================================================

train_ds = tf.keras.utils.image_dataset_from_directory(
    DATA_DIR / "train",
    image_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    shuffle=True,
    seed=SEED,
)

val_ds = tf.keras.utils.image_dataset_from_directory(
    DATA_DIR / "val",
    image_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    shuffle=False,
)

test_ds = tf.keras.utils.image_dataset_from_directory(
    DATA_DIR / "test",
    image_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    shuffle=False,
)

print("Classes:", train_ds.class_names)

AUTOTUNE = tf.data.AUTOTUNE

train_ds = train_ds.prefetch(AUTOTUNE)
val_ds = val_ds.prefetch(AUTOTUNE)
test_ds = test_ds.prefetch(AUTOTUNE)

# ============================================================
# Data augmentation
# ============================================================

augmentation = tf.keras.Sequential(
    [
        tf.keras.layers.RandomFlip("horizontal"),
        tf.keras.layers.RandomRotation(0.05),
        tf.keras.layers.RandomZoom(0.10),
    ],
    name="augmentation",
)

# ============================================================
# VGG16
# ============================================================

base_model = tf.keras.applications.VGG16(
    include_top=False,
    weights="imagenet",
    input_shape=(224, 224, 3),
)

print("Base model first layers:", [l.name for l in base_model.layers[:3]])

base_model.trainable = False

inputs = tf.keras.Input(shape=(224, 224, 3))

x = augmentation(inputs)

# VGG16 pretrained weights expect caffe-preprocessed inputs
# (RGB->BGR plus ImageNet channel mean subtraction).
x = tf.keras.layers.Lambda(
    tf.keras.applications.vgg16.preprocess_input,
    name="vgg16_preprocess_input",
)(x)

x = base_model(x, training=False)
x = tf.keras.layers.GlobalAveragePooling2D()(x)

x = tf.keras.layers.Dense(
    128,
    activation="relu",
    kernel_regularizer=tf.keras.regularizers.l2(1e-4),
)(x)

x = tf.keras.layers.Dropout(0.3)(x)

outputs = tf.keras.layers.Dense(
    1,
    activation="sigmoid",
)(x)

model = tf.keras.Model(inputs, outputs)

# ============================================================
# Stage 1: Train classifier with frozen backbone
# ============================================================

model.compile(
    optimizer=tf.keras.optimizers.Adam(
        learning_rate=INITIAL_LR
    ),
    loss="binary_crossentropy",
    metrics=[
        "accuracy",
        tf.keras.metrics.Precision(name="precision"),
        tf.keras.metrics.Recall(name="recall"),
    ],
)

print("\n========== STAGE 1 ==========")
print("Training classifier with frozen VGG16")

stage1_callbacks = [
    tf.keras.callbacks.EarlyStopping(
        monitor="val_loss",
        patience=5,
        restore_best_weights=True,
    ),
    tf.keras.callbacks.ModelCheckpoint(
        "vgg16_stage1_best.keras",
        monitor="val_loss",
        save_best_only=True,
    ),
]

model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=INITIAL_EPOCHS,
    callbacks=stage1_callbacks,
)

# ============================================================
# Stage 2: Fine-tune final 20 layers
# ============================================================

print("\n========== STAGE 2 ==========")
print("Fine-tuning final 20 VGG16 layers")

base_model.trainable = True

# Freeze everything except the final 20 layers
for layer in base_model.layers[:-20]:
    layer.trainable = False

for layer in base_model.layers[-20:]:
    layer.trainable = True

model.compile(
    optimizer=tf.keras.optimizers.Adam(
        learning_rate=FINETUNE_LR
    ),
    loss="binary_crossentropy",
    metrics=[
        "accuracy",
        tf.keras.metrics.Precision(name="precision"),
        tf.keras.metrics.Recall(name="recall"),
    ],
)

stage2_callbacks = [
    tf.keras.callbacks.EarlyStopping(
        monitor="val_loss",
        patience=3,
        restore_best_weights=True,
    ),
    tf.keras.callbacks.ModelCheckpoint(
        "vgg16_stage2_best.keras",
        monitor="val_loss",
        save_best_only=True,
    ),
]

model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=FINETUNE_EPOCHS,
    callbacks=stage2_callbacks,
)

# ============================================================
# Test evaluation
# ============================================================

print("\n========== TEST EVALUATION ==========")

y_true = []
y_prob = []

for images, labels in test_ds:
    predictions = model.predict(images, verbose=0)

    y_true.extend(labels.numpy())
    y_prob.extend(predictions.ravel())

y_true = np.array(y_true)
y_prob = np.array(y_prob)

# 0.5 threshold
y_pred = (y_prob >= 0.5).astype(int)

# Confusion matrix
tn, fp, fn, tp = confusion_matrix(
    y_true,
    y_pred,
    labels=[0, 1],
).ravel()

accuracy = accuracy_score(y_true, y_pred)
precision = precision_score(y_true, y_pred, zero_division=0)
sensitivity = recall_score(y_true, y_pred, zero_division=0)
f1 = f1_score(y_true, y_pred, zero_division=0)

specificity = tn / (tn + fp) if (tn + fp) > 0 else 0.0

roc_auc = roc_auc_score(y_true, y_prob)

print(f"Accuracy:     {accuracy:.4f}")
print(f"Sensitivity:  {sensitivity:.4f}")
print(f"Specificity:  {specificity:.4f}")
print(f"Precision:    {precision:.4f}")
print(f"F1 Score:     {f1:.4f}")
print(f"ROC-AUC:      {roc_auc:.4f}")

print("\nConfusion Matrix:")
print(f"TN: {tn}")
print(f"FP: {fp}")
print(f"FN: {fn}")
print(f"TP: {tp}")

# ============================================================
# Save model, predictions and results
# ============================================================

MODEL_DIR.mkdir(parents=True, exist_ok=True)

model_path = MODEL_DIR / "vgg16_model.keras"
model.save(model_path)

np.save(RESULT_DIR / "vgg16_y_true.npy", y_true)
np.save(RESULT_DIR / "vgg16_y_prob.npy", y_prob)
np.save(RESULT_DIR / "vgg16_y_pred.npy", y_pred)

class_names = train_ds.class_names

results = {
    "dataset": "Clean Deduplicated Split",
    "model": "VGG16",
    "input_size": list(IMG_SIZE),
    "batch_size": BATCH_SIZE,
    "seed": SEED,
    "class_names": list(class_names),
    "positive_class": class_names[1],
    "confusion_matrix": [
        [int(tn), int(fp)],
        [int(fn), int(tp)],
    ],
    "accuracy": float(accuracy),
    "sensitivity": float(sensitivity),
    "specificity": float(specificity),
    "precision": float(precision),
    "f1": float(f1),
    "roc_auc": float(roc_auc),
    "threshold": 0.5,
    "test_images": int(len(y_true)),
}

result_path = RESULT_DIR / "vgg16_results.json"

with open(result_path, "w") as f:
    json.dump(results, f, indent=2)

print("\nSaved:")
print(model_path)
print(result_path)
print(RESULT_DIR / "vgg16_y_true.npy")
print(RESULT_DIR / "vgg16_y_prob.npy")
print(RESULT_DIR / "vgg16_y_pred.npy")