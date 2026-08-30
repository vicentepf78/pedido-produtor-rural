SHELL := /bin/bash
SLUG ?= pedidos-insumos-mvp0
SPEC := .compozy/tasks/$(SLUG)/_spec.md

.PHONY: gate check-spec

check-spec:
	@test -f "$(SPEC)" || (echo "Missing $(SPEC)"; exit 1)
	python3 scripts/check-spec-part1-leak.py "$(SPEC)"
	python3 scripts/check-spec-markers.py "$(SPEC)"

gate: check-spec
	@echo "Specification gate passed for $(SLUG)."
