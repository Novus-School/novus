# Datomic Architecture

Datomic's data model - based on immutable s stored over time - enables a physical design that is fundamentally different from traditional RDBMSs. Instead of processing all requests in a single server component, Datomic distributes ACID transactions, queries, indexing, caching, and SQL analytics support to provide high availability, horizontal scaling, and elasticity. Datomic also allows for dynamic assignment of compute resources to tasks without any kind of pre-assignment or sharding.

Datomic is designed from the ground up to run on AWS. Datomic automates AWS resources, deployment and security so that you can focus on your application.

The Day of Datomic videos discuss Datomic architecture in detail.

System
A complete Datomic installation is called a . A system consists of storage resources plus one or more compute groups.

```
IMAGE
```

### Storage Resources
The durable elements managed by Datomic are called , including:
- the DynamoDB Transaction Log
- S3 storage of Indexes
- an EFS cache layer
- operational logs
- A VPC and subnets in which computational resources will run

These resources are retained even when no computational resources are active, so you can shut down all the active elements of Datomic while maintaining your data.

### How Datomic Uses Storage

Datomic leverages the attribute's of multiple AWS storage options to satisfy its semantic and performance characteristics. As indicated in the tables below, different AWS storage services provide different latencies, costs, and semantic behaviors.

Datomic utilizes a stratified approach to provide high performance, low cost, and strong reliability guarantees. Specifically:

ACID semantics are ensured via conditional writes with DynamoDB
S3 provides highly reliable low cost persistence
EFS and EC2 instance SSD storage provide very fast local caching
