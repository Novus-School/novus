source: https://docs.datomic.com/cloud/whatis/architecture.html

# Datomic Cloud Architecture


Datomic's data model - based on immutable s stored over time - enables a physical design that is fundamentally different from traditional RDBMSs. Instead of processing all requests in a single server component, Datomic distributes ACID transactions, queries, indexing, caching, and SQL analytics support to provide high availability, horizontal scaling, and elasticity. Datomic also allows for dynamic assignment of compute resources to tasks without any kind of pre-assignment or sharding.

Datomic is designed from the ground up to run on AWS. Datomic automates AWS resources, deployment and security so that you can focus on your application.

The Day of Datomic videos discuss Datomic architecture in detail.

## System

A complete Datomic installation is called a . A system consists of storage resources plus one or more compute groups.

```



INTERACTIVE DATOMIC SYSTEM GOES HERE
```

## Storage Resources
The durable elements managed by Datomic are called , including:

- the DynamoDB Transaction Log
- S3 storage of Indexes
- an EFS cache layer
- operational logs
- A VPC and subnets in which computational resources will run

These resources are retained even when no computational resources are active, so you can shut down all the active elements of Datomic while maintaining your data.


## How Datomic Uses Storage

- Datomic leverages the s of multiple AWS storage options to satisfy its semantic and performance characteristics.
- As indicated in the tables below, different AWS storage services provide different latencies, costs, and semantic behaviors.
- Datomic utilizes a stratified approach to provide high performance, low cost, and strong reliability guarantees. Specifically:
 - ACID semantics are ensured via conditional writes with DynamoDB
 - S3 provides highly reliable low cost persistence
 - EFS and EC2 instance SSD storage provide very fast local caching


 |     Technology     |      Purpose      |
 |--------------------|-------------------|
 | DynamoDB           | ACID              |
 | S3                 | Storage of Record |
 | Memory > SSD > EFS | Cache             |
 | S3 + DDB + EFS     | Reliability       |

 This multi-layered persistence architecture ensures high reliability, as data missing from any given layer can be recovered from deeper within the stack, as well as excellent cache locality and latency via the multi-level distributed cache.

## Indexes

Databases provide not just storage, but leverage over data. This leverage comes from two sources: useful indexes into data, and powerful query languages that use those indexes.

In Datomic Cloud, every datom is automatically indexed in four different sort orders, to automatically support multiple styles of data access: row-oriented, column-oriented, document-oriented, key/value, and graph. This makes it possible for the same database to serve a variety of usage patterns without the need for per-use custom configuration or data transformation.

Datomic's datalog query automatically uses the appropriate indexes. Datomic indexes are transparent to application code and configuration.

## Log

The Datomic Cloud log is indelible, chronological, transactional, and accessible.

Indelible: The log accumulates new information and never removes information. Where update-in-place databases would delete, Datomic instead adds a new retraction.
Chronological: The log contains the entire history of the database, in time order.
Transactional: Datomic writes are always ACID transactions, recorded with compare-and-swap operations against DynamoDB.
Tangible: Rather than being an implementation detail, the log is part of Datomic's information model. You can query the log directly with the log API.

## Large Data Sets

Datomic is designed for use with data sets much larger than can fit in memory, while providing in-memory performance for query to the extent that memory is available. To support large data sets, Datomic:

Stores indexes as shallow trees of s, where each segment typically contains thousands of s.
Merges index trees with an in-memory representation of recent change so that all processes see up-to-date and consistent indexes.
Creates new index trees only occasionally, via background indexing jobs.
Uses an adaptive indexing algorithm that has a sub-linear relationship with total database size.
Transparently manages a multi-layer cache of immutable segments, so that applications can achieve in-memory performance to the degree that their working sets do fit into memory.

## Compute Groups

A compute group is an independent unit of computation, scaling, code deployment and caching. Every Datomic system has a primary compute group, plus zero or more query groups.

Every compute group comprises one or more compute nodes, and has its own Auto Scaling group and Application Load Balancer. Because databases are immutable, compute group instances require no coordination for query.

### Primary Compute Stack

Every running **system** [A complete Datomic installation, consisting of storage resources, a primary compute stack, and optional query groups.] has a single **primary compute stack** [a CloudFormation stack providing computational resources. Every Datomic system has a single primary compute stack, and may also have multiple query groups.] which provides computational resources and a means to access those resources. A Primary Compute Stack consists of:


- compute nodes dedicated to transactions, indexing, and caching
- Route53 and Application Load Balancer (ALB) endpoints

## Query Groups

Query groups are valuable if users of your data differ in any of the following ways:
- application code
- computational requirements
- cacheable working sets
- scaling requirements

A query group is a compute group that
- Extends the abilities of an existing Datomic system
- Is a deployment target for its own distinct application code
- Has its own clustered nodes
- Manages its own working set cache
- Can elastically auto-scale application reads without any up-front planning or sharding

Query groups deliver the entire semantic model of Datomic. In particular:
- Client code does not know or care whether it is talking to the primary compute group or to a query group.
- Query groups are peers with the primary compute group.

You can add, modify, or remove query groups at any time. For example, you might initially release a transactional application that uses only a primary compute group. Later, you might decide to split out multiple query groups:

an autoscaling query group for transactional load
a fixed query group with one large instance for analytic queries
a fixed query group with a smaller instance for support
