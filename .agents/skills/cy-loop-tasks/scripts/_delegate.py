"""Run the installed Compozy loop helper from its repository-local entry point."""

from __future__ import annotations

import runpy
import sys
from pathlib import Path


def delegate(script_name: str) -> None:
    source = (
        Path.home()
        / ".agents"
        / "skills"
        / "cy-loop-tasks"
        / "scripts"
        / script_name
    )
    if not source.is_file():
        raise SystemExit(
            f"Missing installed Compozy helper: {source}. "
            "Restore the cy-loop-tasks skill before running the loop."
        )
    sys.path.insert(0, str(source.parent))
    runpy.run_path(str(source), run_name="__main__")
