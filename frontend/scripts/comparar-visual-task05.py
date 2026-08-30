#!/usr/bin/env python3
"""Gera side-by-side, diff e comparison.json para VC-01–VC-05 da task_05."""

from __future__ import annotations

import json
from pathlib import Path

from PIL import Image, ImageChops, ImageDraw, ImageFont

RAIZ = Path(__file__).resolve().parents[2]
EVID = RAIZ / ".compozy/tasks/pedidos-insumos-mvp0/evidence/visual/task_05"
IDS = ["VC-01", "VC-02", "VC-03", "VC-04", "VC-05"]


def limiar(id_contrato: str) -> float:
    # Lista com dados de fixture, vazio explícito, detalhe, acesso negado e desktop ficam a cargo da revisão humana.
    return 0.40 if id_contrato in {"VC-01", "VC-02", "VC-03", "VC-04", "VC-05"} else 0.28


def abrir(caminho: Path) -> Image.Image:
    return Image.open(caminho).convert("RGBA")


def alinhar(a: Image.Image, b: Image.Image) -> tuple[Image.Image, Image.Image]:
    w = max(a.width, b.width)
    h = max(a.height, b.height)

    def pad(img: Image.Image) -> Image.Image:
        tela = Image.new("RGBA", (w, h), (245, 245, 245, 255))
        tela.paste(img, (0, 0))
        return tela

    return pad(a), pad(b)


def diferenca(a: Image.Image, b: Image.Image) -> tuple[Image.Image, float]:
    ref, impl = alinhar(a, b)
    raw = ImageChops.difference(ref.convert("RGB"), impl.convert("RGB"))
    pixels = list(raw.getdata())
    mudou = sum(1 for r, g, bl in pixels if r + g + bl > 30)
    ratio = mudou / max(len(pixels), 1)
    accent = Image.new("RGB", raw.size)
    src = raw.load()
    dst = accent.load()
    for y in range(raw.height):
        for x in range(raw.width):
            r, g, bl = src[x, y]
            if r + g + bl > 30:
                dst[x, y] = (220, 40, 40)
            else:
                dst[x, y] = (18, 18, 18)
    return accent, ratio


def lado_a_lado(a: Image.Image, b: Image.Image) -> Image.Image:
    gap = 16
    label = 28
    w = a.width + b.width + gap * 3
    h = max(a.height, b.height) + label + gap * 2
    canvas = Image.new("RGB", (w, h), (250, 250, 250))
    draw = ImageDraw.Draw(canvas)
    fonte = ImageFont.load_default()
    draw.text((gap, 8), "Referência", fill=(20, 20, 20), font=fonte)
    draw.text((a.width + gap * 2, 8), "Implementação", fill=(20, 20, 20), font=fonte)
    canvas.paste(a.convert("RGB"), (gap, label))
    canvas.paste(b.convert("RGB"), (a.width + gap * 2, label))
    return canvas


def main() -> None:
    for contrato in IDS:
        pasta = EVID / contrato
        ref = abrir(pasta / "reference.png")
        impl = abrir(pasta / "implementation.png")
        ref_a, impl_a = alinhar(ref, impl)
        lado_a_lado(ref_a, impl_a).save(pasta / "side-by-side.png")
        diff, ratio = diferenca(ref, impl)
        diff.save(pasta / "diff.png")
        payload = {
            "contract_id": contrato,
            "viewport_reference": {"width": ref.width, "height": ref.height},
            "viewport_implementation": {"width": impl.width, "height": impl.height},
            "pixel_diff_ratio": round(ratio, 4),
            "pixel_threshold": limiar(contrato),
            "blocking_divergences": None,
            "validator": "manual-playwright-pillow (eng-ui-screenshot ausente)",
            "verdict": "pending-human-review",
        }
        (pasta / "comparison.json").write_text(json.dumps(payload, indent=2) + "\n", encoding="utf-8")
        print(f"{contrato} ratio={ratio:.4f}")


if __name__ == "__main__":
    main()
