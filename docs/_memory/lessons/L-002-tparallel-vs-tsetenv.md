# L-002: Test isolation

Tests must isolate state, configuration, and fixtures. Parallel tests cannot mutate
process-wide configuration without restoration and synchronization.
