import os
import json
import random
import numpy as np
import tensorflow as tf

from tensorflow.keras import layers, models, regularizers
from tensorflow.keras.applications import InceptionV3
from tensorflow.keras.applications.inception_v3 import preprocess_input
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

CLASS_NAMES = [
    "Normal(Healthy skin)",
    "Abnormal(Ulcer)",
]


# ============================================================
# Reproducibility
# ============================================================

os.environ["PYTHONHASHSEED"] = str(SEED)

random.seed(SEED)
np.random.seed(SEED)
tf.random.set_seed(SEED)


# ============================================================
# GPU check
# ============================================================

print("TensorFlow:", tf.__version__)

gpus = tf.config.list_physical_devices("GPU")
print("GPU devices:", gpus)


# ============================================================
# Dataset loading
# ============================================================

train_ds = tf.keras.utils.image_dataset_from_directory(
    os.path.join(DATA_DIR, "train"),
    labels="inferred",
    label_mode="binary",
    class_names=CLASS_NAMES,
    image_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    shuffle=True,
    seed=SEED,
)

val_ds = tf.keras.utils.image_dataset_from_directory(
    os.path.join(DATA_DIR, "val"),
    labels="inferred",
    label_mode="binary",
    class_names=CLASS_NAMES,
    image_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    shuffle=False,
)

test_ds = tf.keras.utils.image_dataset_from_directory(
    os.path.join(DATA_DIR, "test"),
    labels="inferred",
    label_mode="binary",
    class_names=CLASS_NAMES,
    image_size=IMG_SIZE,
    batch_size=BATCH_SIZE,
    shuffle=False,
)

print("\nClasses:", train_ds.class_names)


# ============================================================
# Dataset performance
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
# InceptionV3 base model
# ============================================================

base_model = InceptionV3(
    include_top=False,
    weights="imagenet",
    input_shape=(IMG_SIZE[0], IMG_SIZE[1], 3),
)

print("\nInceptionV3 first layers:")
for layer in base_model.layers[:5]:
    print(layer.name, layer.__class__.__name__)

print("\nInceptionV3 last layers:")
for layer in base_model.layers[-5:]:
    print(layer.name, layer.__class__.__name__)


# ============================================================
# Model
# ============================================================

inputs = layers.Input(
    shape=(IMG_SIZE[0], IMG_SIZE[1], 3),
    name="input",
)

x = augmentation(inputs)

# InceptionV3 preprocessing: [0, 255] -> [-1, 1]
x = layers.Lambda(
    preprocess_input,
    name="inceptionv3_preprocess",
)(x)

base_model.trainable = False

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
    inputs=inputs,
    outputs=outputs,
    name="InceptionV3_DFU",
)


# ============================================================
# Model summary
# ============================================================

model.summary()


# ============================================================
# Stage 1 — Frozen backbone
# ============================================================

print("\n==============================")
print("InceptionV3 Stage 1")
print("==============================")

model.compile(
    optimizer=tf.keras.optimizers.Adam(
        learning_rate=1e-4
    ),
    loss="binary_crossentropy",
    metrics=["accuracy"],
)

early_stopping_stage1 = EarlyStopping(
    monitor="val_loss",
    patience=5,
    restore_best_weights=True,
)

model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=20,
    callbacks=[early_stopping_stage1],
)


# ============================================================
# Stage 2 — Fine-tuning
# ============================================================

print("\n==============================")
print("InceptionV3 Stage 2")
print("==============================")

base_model.trainable = True

# Freeze all layers except the final 20
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

early_stopping_stage2 = EarlyStopping(
    monitor="val_loss",
    patience=3,
    restore_best_weights=True,
)

model.fit(
    train_ds,
    validation_data=val_ds,
    epochs=10,
    callbacks=[early_stopping_stage2],
)


# ============================================================
# Test evaluation
# ============================================================

print("\n==============================")
print("Evaluating InceptionV3")
print("==============================")


y_true = []
y_prob = []


for images, labels in test_ds:
    probabilities = model.predict(
        images,
        verbose=0,
    ).ravel()

    y_prob.extend(probabilities)
    y_true.extend(
        labels.numpy().astype(int).ravel()
    )


y_true = np.array(y_true)
y_prob = np.array(y_prob)

# Classification threshold
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
    zero_division=0,
)

specificity = recall_score(
    y_true,
    y_pred,
    pos_label=0,
    zero_division=0,
)

precision = precision_score(
    y_true,
    y_pred,
    pos_label=1,
    zero_division=0,
)

f1 = f1_score(
    y_true,
    y_pred,
    pos_label=1,
    zero_division=0,
)

roc_auc = roc_auc_score(
    y_true,
    y_prob,
)

cm = confusion_matrix(
    y_true,
    y_pred,
    labels=[0, 1],
)

tn, fp, fn, tp = cm.ravel()


# ============================================================
# Print results
# ============================================================

print("\n==============================")
print("InceptionV3 TEST RESULTS")
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
# Create output directories
# ============================================================

os.makedirs(
    os.path.join(OUTPUT_DIR, "models"),
    exist_ok=True,
)


# ============================================================
# Save model
# ============================================================

model_path = os.path.join(
    OUTPUT_DIR,
    "models",
    "inceptionv3_model.keras",
)

model.save(model_path)


# ============================================================
# Save metrics
# ============================================================

results = {
    "model": "InceptionV3",
    "dataset": "DFU_split",
    "seed": SEED,
    "input_size": list(IMG_SIZE),
    "batch_size": BATCH_SIZE,
    "class_names": CLASS_NAMES,
    "positive_class": "Abnormal(Ulcer)",
    "threshold": 0.5,
    "accuracy": float(accuracy),
    "sensitivity": float(sensitivity),
    "specificity": float(specificity),
    "precision": float(precision),
    "f1_score": float(f1),
    "roc_auc": float(roc_auc),
    "confusion_matrix": cm.tolist(),
    "tn": int(tn),
    "fp": int(fp),
    "fn": int(fn),
    "tp": int(tp),
    "test_images": int(len(y_true)),
}

results_path = os.path.join(
    OUTPUT_DIR,
    "inceptionv3_results.json",
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
        "inceptionv3_y_true.npy",
    ),
    y_true,
)

np.save(
    os.path.join(
        OUTPUT_DIR,
        "inceptionv3_y_prob.npy",
    ),
    y_prob,
)

np.save(
    os.path.join(
        OUTPUT_DIR,
        "inceptionv3_y_pred.npy",
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
        "inceptionv3_y_true.npy",
    )
)

print(
    os.path.join(
        OUTPUT_DIR,
        "inceptionv3_y_prob.npy",
    )
)

print(
    os.path.join(
        OUTPUT_DIR,
        "inceptionv3_y_pred.npy",
    )
)

print("\nInceptionV3 training and evaluation completed.")