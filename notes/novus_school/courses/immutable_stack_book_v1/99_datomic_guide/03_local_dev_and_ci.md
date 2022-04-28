## Local Dev and CI with dev-local

With Datomic dev-local you can develop and test applications with minimal connectivity and setup.
Get the dev-local library, add it to your classpath, and you have full access to the Client API. This allows you to:

- Develop and test Datomic Cloud applications without connecting to a server and without changing your application code.
- Create small, single-process Datomic applications and libraries.

Dev-local is available at no cost. Note that dev-local is not redistributable.

NOTE: If you make Datomic apps/libs for others to use they must get a copy of dev-local themselves.

This document includes everything you need to know to use dev-local:

- how to get and configure dev-local
- how to add dev-local to your classpath and create a client
- how to create durable and in-memory databases
- API for using dev-local as a test system for Datomic Cloud
- operational limits
- the changelog
