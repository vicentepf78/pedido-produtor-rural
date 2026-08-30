# Glossary

| Term | Meaning |
| --- | --- |
| Tenant | The commercial company whose catalog and orders are isolated in the platform. MVP0 configures one tenant. |
| Producer | Rural customer who creates and confirms an order. |
| Property | Rural property selected by the producer for the order context. |
| Order | The local commercial record confirmed by the producer. It is independent from an ERP record. |
| Mock ERP | A test adapter that accepts an order without calling an external ERP. |
| Curated catalog | The fixed set of 30 demonstration products manually supplied for MVP0. |
| Regulated product | A product subject to legal prescription or operational controls. It is excluded from MVP0 ordering. |
| Capability | An observable user or operator outcome, not an implementation mechanism. |
| Port | A domain-owned contract for a varying external concern. |
| Adapter | An implementation of a port that translates an external provider's model. |
| Task | A dependency-ordered, independently executable delivery slice. |
| Naming convention | Production identifiers use PT-BR. Attributes, fields, and API properties use `camelCase`; PostgreSQL identifiers are quoted to preserve this case. |
