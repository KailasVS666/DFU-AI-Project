from pathlib import Path
import hashlib

ROOT = Path("data/DFU_split")

for split in ["train", "val", "test"]:
    files = [
        p for p in (ROOT / split).rglob("*")
        if p.is_file()
    ]

    hashes = {}

    for path in files:
        h = hashlib.sha256(path.read_bytes()).hexdigest()
        hashes.setdefault(h, []).append(path)

    duplicate_groups = [
        group for group in hashes.values()
        if len(group) > 1
    ]

    duplicate_files = sum(len(group) for group in duplicate_groups)

    print(f"{split}:")
    print(f"  Files: {len(files)}")
    print(f"  Unique images: {len(hashes)}")
    print(f"  Duplicate groups: {len(duplicate_groups)}")
    print(f"  Files in duplicate groups: {duplicate_files}")