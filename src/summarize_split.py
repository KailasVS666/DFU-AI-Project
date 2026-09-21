from pathlib import Path

ROOT = Path("data/DFU_split")
OUT = Path("results/split_summary.txt")

classes = ["Abnormal(Ulcer)", "Normal(Healthy skin)"]
splits = ["train", "val", "test"]

lines = ["# Dataset Split Summary\n"]

total = 0

for split in splits:
    lines.append(f"## {split}")
    split_total = 0

    for cls in classes:
        count = len(list((ROOT / split / cls).glob("*")))
        split_total += count
        lines.append(f"- {cls}: {count}")

    lines.append(f"- Total: {split_total}\n")
    total += split_total

lines.append(f"## Overall Total\n- {total}")

OUT.parent.mkdir(parents=True, exist_ok=True)
OUT.write_text("\n".join(lines), encoding="utf-8")

print(OUT)