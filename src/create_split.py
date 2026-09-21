from pathlib import Path
import hashlib
import shutil

from PIL import Image
import imagehash


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


def sha256(path):
    h = hashlib.sha256()
    with open(path, "rb") as f:
        for chunk in iter(lambda: f.read(1024 * 1024), b""):
            h.update(chunk)
    return h.hexdigest()


def phash(path):
    with Image.open(path) as img:
        return imagehash.phash(img.convert("RGB"))


def main():
    import random

    random.seed(SEED)

    images = []

    for class_name in CLASSES:
        folder = DATASET / class_name

        for path in folder.iterdir():
            if path.suffix.lower() not in {
                ".jpg", ".jpeg", ".png", ".webp", ".gif"
            }:
                continue

            images.append({
                "path": path,
                "class": class_name,
                "sha": sha256(path),
                "phash": phash(path),
            })

    print(f"Found {len(images)} labeled images.")

    # Group exact duplicates first.
    exact_groups = {}

    for item in images:
        exact_groups.setdefault(item["sha"], []).append(item)

    groups = list(exact_groups.values())

    # Merge near-duplicates using pHash.
    merged = []

    for group in groups:
        placed = False

        for existing in merged:
            if any(
                item["phash"] - other["phash"] <= 6
                for item in group
                for other in existing
            ):
                existing.extend(group)
                placed = True
                break

        if not placed:
            merged.append(group)

    print(f"Duplicate/near-duplicate groups: {len(merged)}")

    # Split groups, never individual images.
    by_class = {c: [] for c in CLASSES}

    for group in merged:
        # All members should normally have the same label.
        labels = {item["class"] for item in group}

        if len(labels) != 1:
            raise RuntimeError(
                f"Group contains multiple labels: {labels}"
            )

        label = next(iter(labels))
        by_class[label].append(group)

    for class_name, class_groups in by_class.items():
        random.shuffle(class_groups)

        n = len(class_groups)

        train_end = int(n * TRAIN_RATIO)
        val_end = train_end + int(n * VAL_RATIO)

        splits = {
            "train": class_groups[:train_end],
            "val": class_groups[train_end:val_end],
            "test": class_groups[val_end:],
        }

        for split, groups_in_split in splits.items():
            output_dir = OUTPUT / split / class_name
            output_dir.mkdir(parents=True, exist_ok=True)

            for group in groups_in_split:
                for item in group:
                    destination = output_dir / item["path"].name

                    # Avoid filename collisions.
                    if destination.exists():
                        destination = (
                            output_dir
                            / f"{item['sha'][:10]}_{item['path'].name}"
                        )

                    shutil.copy2(item["path"], destination)

    print("\nSplit created:")
    print(OUTPUT)
    print("\nIMPORTANT: original dataset was not modified.")


if __name__ == "__main__":
    main()