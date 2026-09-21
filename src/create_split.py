from pathlib import Path
import hashlib
import random
import shutil

from PIL import Image
import imagehash


# =========================
# Configuration
# =========================

DATASET = Path("data/DFU/Patches")
OUTPUT = Path("data/DFU_split")

SEED = 42

TRAIN_RATIO = 0.70
VAL_RATIO = 0.15
TEST_RATIO = 0.15

CLASSES = {
    "Abnormal(Ulcer)": 1,
    "Normal(Healthy skin)": 0,
}

IMAGE_EXTENSIONS = {
    ".jpg",
    ".jpeg",
    ".png",
    ".webp",
    ".gif",
}

PHASH_THRESHOLD = 6


# =========================
# Hash functions
# =========================

def sha256(path):
    """Calculate exact SHA-256 hash of an image."""
    h = hashlib.sha256()

    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)

    return h.hexdigest()


def phash(path):
    """Calculate perceptual hash of an image."""
    with Image.open(path) as img:
        return imagehash.phash(img.convert("RGB"))


# =========================
# Union-Find
# =========================

def find(parent, x):
    while parent[x] != x:
        parent[x] = parent[parent[x]]
        x = parent[x]

    return x


def union(parent, a, b):
    root_a = find(parent, a)
    root_b = find(parent, b)

    if root_a != root_b:
        parent[root_b] = root_a


# =========================
# Main
# =========================

def main():

    random.seed(SEED)

    # ---------------------------------
    # Load images
    # ---------------------------------

    images = []

    for class_name in CLASSES:

        folder = DATASET / class_name

        if not folder.exists():
            raise FileNotFoundError(
                f"Dataset folder not found: {folder}"
            )

        for path in folder.iterdir():

            if path.suffix.lower() not in IMAGE_EXTENSIONS:
                continue

            images.append({
                "path": path,
                "class": class_name,
                "sha": sha256(path),
                "phash": phash(path),
            })

    print(f"Found {len(images)} labeled images.")

    # ---------------------------------
    # Build duplicate / near-duplicate
    # connected components
    # ---------------------------------

    n = len(images)

    parent = list(range(n))

    # Exact duplicates
    sha_to_indices = {}

    for i, item in enumerate(images):

        sha_to_indices.setdefault(
            item["sha"],
            []
        ).append(i)

    for indices in sha_to_indices.values():

        for i in indices[1:]:

            union(
                parent,
                indices[0],
                i
            )

    # Near duplicates using pHash
    for i in range(n):

        for j in range(i + 1, n):

            distance = (
                images[i]["phash"]
                - images[j]["phash"]
            )

            if distance <= PHASH_THRESHOLD:

                union(
                    parent,
                    i,
                    j
                )

    # ---------------------------------
    # Create connected components
    # ---------------------------------

    components = {}

    for i in range(n):

        root = find(parent, i)

        components.setdefault(
            root,
            []
        ).append(images[i])

    groups = list(components.values())

    print(
        f"Duplicate/near-duplicate groups: {len(groups)}"
    )

    # ---------------------------------
    # Verify that groups do not mix
    # classes
    # ---------------------------------

    for group in groups:

        labels = {
            item["class"]
            for item in group
        }

        if len(labels) != 1:

            raise RuntimeError(
                "Duplicate/near-duplicate group "
                f"contains multiple labels: {labels}"
            )

    # ---------------------------------
    # Remove previous split
    # ---------------------------------

    if OUTPUT.exists():

        shutil.rmtree(OUTPUT)

    # ---------------------------------
    # Organize groups by class
    # ---------------------------------

    by_class = {
        class_name: []
        for class_name in CLASSES
    }

    for group in groups:

        class_name = group[0]["class"]

        by_class[class_name].append(group)

    # ---------------------------------
    # Split groups
    # ---------------------------------

    for class_name, class_groups in by_class.items():

        random.shuffle(class_groups)

        n_groups = len(class_groups)

        train_end = int(
            n_groups * TRAIN_RATIO
        )

        val_end = (
            train_end
            + int(n_groups * VAL_RATIO)
        )

        splits = {
            "train": class_groups[:train_end],

            "val": class_groups[
                train_end:val_end
            ],

            "test": class_groups[
                val_end:
            ],
        }

        # ---------------------------------
        # Copy ONE representative per group
        # ---------------------------------

        for split, groups_in_split in splits.items():

            output_dir = (
                OUTPUT
                / split
                / class_name
            )

            output_dir.mkdir(
                parents=True,
                exist_ok=True
            )

            for group in groups_in_split:

                # IMPORTANT:
                # Keep only one representative
                # from each duplicate/near-duplicate
                # group.

                item = group[0]

                destination = (
                    output_dir
                    / item["path"].name
                )

                shutil.copy2(
                    item["path"],
                    destination
                )

    # ---------------------------------
    # Final message
    # ---------------------------------

    print("\nSplit created:")
    print(OUTPUT)

    print(
        "\nEach duplicate/near-duplicate "
        "group contributes only ONE image."
    )

    print(
        "\nIMPORTANT: original dataset "
        "was not modified."
    )


if __name__ == "__main__":
    main()