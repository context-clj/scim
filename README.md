# Clojure SCIM implementation


## The Relationship Between ResourceType, Schema, and ServiceProviderConfig in SCIM

In the SCIM (System for Cross-domain Identity Management) protocol,
ResourceType, Schema, and ServiceProviderConfig work together as a comprehensive
metadata system that enables standardized identity management across different
platforms. ServiceProviderConfig provides information about the SCIM provider's
capabilities and features, such as which operations (patch, bulk, filter, etc.)
are supported and what authentication methods can be used. This resource acts as
the foundation that informs clients about what functionality is available in the
implementation.

**Schemas** define the data structure and validation rules for resources in SCIM,
specifying all possible attributes with their characteristics such as data type,
mutability, and uniqueness requirements. For example, the User Schema defines
attributes like userName and emails along with their properties. Schemas are
identified by unique URIs and can be referenced by ResourceTypes, creating a
hierarchy of data definitions that can be extended to meet custom requirements.

**ResourceTypes*** serve as connectors between Schemas and API endpoints, telling
clients where to access resources of a specific type. Each ResourceType
references one primary Schema through the "schema" attribute and may include
additional extension schemas through the "schemaExtensions" array. This
mechanism allows for flexible extension of standard resource definitions while
maintaining compatibility with the SCIM protocol.

Together, these three metadata components form a self-describing system that
enables automatic discovery and interoperability. Clients typically start by
querying the ServiceProviderConfig to understand the server's capabilities, then
explore available ResourceTypes to discover what kinds of resources exist and
which endpoints to use, and finally examine the associated Schemas to understand
the structure of these resources. This design approach allows SCIM to
accommodate diverse implementation requirements while maintaining a standardized
approach to identity management across organizational boundaries.

## Implementation Details

Create ResourceType - trigger table
Create SPC - trigger API
Schemas are used for validation

* login impl
* no registration


## Provide Universal UI for administration

* create schemas, resourcetypes, users, groups

