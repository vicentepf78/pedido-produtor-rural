SHELL := /bin/bash
SLUG ?= pedidos-insumos-mvp0
SPEC := .compozy/tasks/$(SLUG)/_spec.md

.PHONY: gate check-spec test test-integration test-e2e-web

check-spec:
	@test -f "$(SPEC)" || (echo "Missing $(SPEC)"; exit 1)
	python3 scripts/check-spec-part1-leak.py "$(SPEC)"
	python3 scripts/check-spec-markers.py "$(SPEC)"

test:
	cd backend && ./mvnw --batch-mode test

test-integration:
	cd backend && ./mvnw --batch-mode test-compile failsafe:integration-test failsafe:verify

test-e2e-web:
	@if [ -d frontend/e2e ]; then cd frontend && npm test; else echo "No E2E web tests in the foundation slice."; fi

gate: check-spec test test-integration
	@echo "Specification and application gates passed for $(SLUG)."
