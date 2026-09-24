from pathlib import Path

import matplotlib.pyplot as plt
import numpy as np
import tensorflow as tf
from PIL import Image


MODEL_PATH = Path("results/models/final_efficientnet_b0.keras")
IMAGE_PATH = Path(
    "data/DFU_split/test/Normal(Healthy skin)/40.jpg"
)

IMG_SIZE = (224, 224)
OUTPUT_PATH = Path("results/gradcam/gradcam_false_positive_40.jpg")


def generate_gradcam():
    print("Loading model...")
    model = tf.keras.models.load_model(MODEL_PATH)
    print("Model loaded.")

    augmentation = model.get_layer("augmentation")
    base_model = model.get_layer("efficientnetb0")
    gap = model.get_layer("global_average_pooling2d")
    dense = model.get_layer("dense")

    last_conv = base_model.get_layer("top_conv")
    print("Grad-CAM layer:", last_conv.name)

    cam_model = tf.keras.Model(
        inputs=base_model.input,
        outputs=[last_conv.output, base_model.output],
    )

    original = Image.open(IMAGE_PATH).convert("RGB")
    resized = original.resize(IMG_SIZE)

    x = np.array(resized).astype("float32")
    x = np.expand_dims(x, axis=0)

    x_aug = augmentation(x, training=False)

    with tf.GradientTape() as tape:
        conv_output, features = cam_model(
            x_aug,
            training=False,
        )

        pooled = gap(features)
        prediction = dense(pooled)
        loss = prediction[:, 0]

    grads = tape.gradient(loss, conv_output)

    weights = tf.reduce_mean(
        grads,
        axis=(1, 2),
    )

    cam = tf.reduce_sum(
        conv_output * weights[:, tf.newaxis, tf.newaxis, :],
        axis=-1,
    )

    cam = tf.maximum(cam, 0)

    cam = cam[0].numpy()

    if cam.max() > 0:
        cam = cam / cam.max()

    heatmap = Image.fromarray(
        np.uint8(cam * 255)
    ).resize(original.size)

    heatmap = np.array(heatmap)

    probability = float(prediction[0][0])

    print(f"Ulcer probability: {probability:.6f}")

    OUTPUT_PATH.parent.mkdir(
        parents=True,
        exist_ok=True,
    )

    plt.figure(figsize=(10, 5))

    plt.subplot(1, 2, 1)
    plt.imshow(original)
    plt.axis("off")
    plt.title("False Positive — Healthy")

    plt.subplot(1, 2, 2)
    plt.imshow(original)
    plt.imshow(
        heatmap,
        cmap="jet",
        alpha=0.45,
    )
    plt.axis("off")
    plt.title("Grad-CAM")

    plt.tight_layout()

    plt.savefig(
        OUTPUT_PATH,
        dpi=300,
        bbox_inches="tight",
    )

    plt.show()

    print(f"Saved: {OUTPUT_PATH}")


if __name__ == "__main__":
    generate_gradcam()