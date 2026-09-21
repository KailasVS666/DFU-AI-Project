from pathlib import Path
import hashlib
from PIL import Image
import imagehash

ROOT = Path("data/DFU_split")
splits = ["train", "val", "test"]

files = {}
for split in splits:
    files[split] = []
    for p in (ROOT / split).rglob("*"):
        if p.is_file():
            files[split].append(p)

def sha256(p):
    return hashlib.sha256(p.read_bytes()).hexdigest()

def phash(p):
    with Image.open(p) as im:
        return imagehash.phash(im.convert("RGB"))

for i, a in enumerate(splits):
    for b in splits[i+1:]:
        exact = set(sha256(p) for p in files[a]) & set(sha256(p) for p in files[b])

        near = 0
        hashes_a = [phash(p) for p in files[a]]
        hashes_b = [phash(p) for p in files[b]]

        for ha in hashes_a:
            if any(ha - hb <= 6 for hb in hashes_b):
                near += 1

        print(f"{a} vs {b}:")
        print(f"  Exact overlaps: {len(exact)}")
        print(f"  Near-duplicate overlaps: {near}")