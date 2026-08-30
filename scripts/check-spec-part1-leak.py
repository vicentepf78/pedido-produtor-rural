#!/usr/bin/env python3
"""Reject implementation details from Part I of a product specification."""

from __future__ import annotations

import re
import sys
from pathlib import Path


TOKENS = {
    "framework": r"\b(react|spring|spring boot|vite|maven|flyway)\b",
    "storage": r"\b(postgresql|redis|mysql|sqlite|cloudinary)\b",
    "protocol": r"\b(http|rest|json|websocket|kafka|rabbitmq)\b",
    "auth": r"\b(jwt|oauth|oidc|csrf|httponly)\b",
}


def product_part(text: str) -> str:
    return re.split(r"^#\s*(Part II|Parte II)\b.*$", text, maxsplit=1,
                    flags=re.MULTILINE | re.IGNORECASE)[0]


def clean(text: str) -> str:
    without_fences = re.sub(r"```[\s\S]*?```", "", text)
    return re.sub(r"`[^`]+`", "", without_fences)


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: check-spec-part1-leak.py <_spec.md>", file=sys.stderr)
        return 2
    path = Path(sys.argv[1])
    if not path.is_file():
        print(f"FILE ERROR: {path} not found", file=sys.stderr)
        return 2
    text = clean(product_part(path.read_text(encoding="utf-8")))
    findings = [
        (category, match.group(0))
        for category, pattern in TOKENS.items()
        for match in re.finditer(pattern, text, re.IGNORECASE)
    ]
    if not findings:
        print(f"OK: Part I of {path} contains no implementation leaks.")
        return 0
    print(f"FOUND {len(findings)} implementation leaks in Part I:", file=sys.stderr)
    for category, token in findings:
        print(f"  [{category}] {token}", file=sys.stderr)
    return 1


raise SystemExit(main())
