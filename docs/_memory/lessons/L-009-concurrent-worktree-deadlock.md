# L-009: Concurrent worktree safety

Parallel work must use isolated worktrees and avoid shared mutable ports, databases,
and generated artifacts. QA must document its environment isolation.
