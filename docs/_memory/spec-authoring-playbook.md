# Specification authoring playbook

## Purpose

Specifications turn validated product discovery into independently executable work.
They are the source of truth for implementation agents, reviewers, and the delivery
loop.

## Authoring flow

1. Research the repository, architecture notes, existing decisions, market evidence,
   and applicable regulation.
2. Grill Product decisions before writing Part I. Keep Part I focused on users,
   business rules, outcomes, non-goals, and open questions.
3. Record irreversible choices as ADRs when they have meaningful alternatives.
4. Freeze Part I after user confirmation.
5. Define API and UI surfaces, grill them, then write Part II.
6. Define concrete unit, integration, architecture, and end-to-end test cases.
7. Decompose the approved specification into a dependency graph of vertical tasks.

## Quality bar

Every requirement is observable, every rule has an owner, every external dependency
has a failure outcome, and every test is owned by exactly one task. Do not use
placeholders, compatibility shims, or unstated defaults.

## Project vocabulary

Use the glossary in this directory. Preserve legally relevant Brazilian agricultural
terms in Portuguese when translating them loses precision.
