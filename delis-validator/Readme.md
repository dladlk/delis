# DELIS Validator

Spring-boot web application for REST service to validate documents by schema and schematron.

It can be used as a light-weight validation service, independent from Delis database, but which reuses Delis validation artifacts.

# Docker image versions

1.0.0 - initial version
1.1.0 - added support of compressed payload to validate - if optional attribute compressed is true. Required for Domibus 5.0