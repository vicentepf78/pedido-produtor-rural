# L-001: Request lifetime differs from execution lifetime

Do not make a producer wait for an external integration. Persist the local order
before invoking a varying external boundary, and expose a truthful local status.
