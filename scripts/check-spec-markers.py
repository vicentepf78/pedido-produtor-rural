#!/usr/bin/env python3
"""Check the six mandatory technical-specification markers for this project."""

from __future__ import annotations

import re
import sys
from pathlib import Path


def present(text: str, pattern: str) -> bool:
    return bool(re.search(pattern, text, re.IGNORECASE | re.MULTILINE | re.DOTALL))


def main() -> int:
    if len(sys.argv) != 2:
        print("Usage: check-spec-markers.py <_spec.md>", file=sys.stderr)
        return 2
    path = Path(sys.argv[1])
    if not path.is_file():
        print(f"FILE ERROR: {path} not found", file=sys.stderr)
        return 2
    text = path.read_text(encoding="utf-8")
    checks = {
        "limite do MVP": present(text, r"^#+\s*(MVP Boundary|Limite do MVP)\b"),
        "limites arquiteturais": present(
            text, r"^#+\s*(Architectural Boundaries|Limites arquiteturais)\b"
        ),
        "Java interface signatures": bool(
            re.search(r"```java[\s\S]*?\binterface\s+\w+", text, re.IGNORECASE)
        ),
        "justificativa do modelo de dados": present(
            text, r"^#+\s*(Data Models|Modelos de dados)\b"
        ),
        "decisão relacional-versus-JSON": bool(
            re.search(r"\b(JSON|JSONB)\b", text, re.IGNORECASE)
            and re.search(
                r"\b(table|relational|side table|tabela|relacional|tabela auxiliar)\b",
                text,
                re.IGNORECASE,
            )
        ),
        "invariantes de segurança numeradas": bool(
            re.search(
                r"^#+\s*(Safety Invariants|Invariantes de segurança)\b[\s\S]*?^\s*1[.)]\s+",
                text,
                re.IGNORECASE | re.MULTILINE,
            )
        ),
    }
    missing = [name for name, is_present in checks.items() if not is_present]
    if not missing:
        print(f"OK: {path} carries all six required markers.")
        return 0
    print("MISSING:", ", ".join(missing), file=sys.stderr)
    return 1


raise SystemExit(main())
