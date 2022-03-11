# Introduction

**What is Datomic?**

Datomic is a distributed and immutable database offered by Cognitect. Datomic has a unique design that offers some interesting properties: these could add significant business value in the right circumstances. It offers:


https://www.quora.com/When-is-Datomic-a-good-choice-over-other-databases
- Unlimited read/query scalability - useful if you have many read-only users of a database performing arbitrary queries. You can give query-intensive users (e.g. people performing heavy analytics) their own clusters of servers so that they don't disrupt general usage of a live application. Business uses: data warehousing, analytics, data provision for internet scale websites, big data processing
- Time based queries / history - you can query the database as it existed at any previous point in time. It cannot be overstated how useful this is: the most important point is that you don't need to re-engineer your database and queries to retain historical data, it happens automatically. This is fantastically useful for audit purposes, but it also makes application development much easier since you don't need to create complex new queries - you just query the database "as-of" some previous point in time. Business uses: meeting audit requirements, healthcare patient records, tracking history of CRM interactions etc.
- Transactional guarantees - Datomic has strong transactional guarantees enforced through a "transactor". This may make it useful for applications that need transactional integrity - some of the other currently popular NoSQL solutions fall short in this regard. Business uses: financial transactions, order management
- EAV-style schemas - Datomic stores data in "datoms" that are effective Enity+Attribute+Value+Time. It shares a lot in common with the concept of RDF triples. This approach has it's advantages and disadvantages vs traditional table-based database approaches. It is likely to have most value in areas where there are flexible schemas that possibly change over time, or where you have large graphs of semi-structured data. Business uses: complex semi-structured documents, social graphs, metadata/master data management

My overall take on Datomic is that it is a significant advance on previous database engine designs - for applications that are right for the sweet spots indicated above, it's likely to an ideal choice. The tools probably need a bit more time to mature but this is likely to happen fairly fast and then it will be a game-changer in the database world.


- Indelible and Chronological Database - Understand how and when changes were made. Datomic stores all history, and lets you query against any point in time.
- Flexible Schema and Hierarchical data modelling
  - Model your data once, with agility.
  - Handle row-oriented, column-oriented, graph, and hierarchical data in a single system.
  - Add attributes dynamically at any time.
- Programmability - data queries and transaction operations are done using Clojure data structure. The query language is programmable and has primitives for abstraction and reuse (rules, predicates, database functions)
- a managed experience, so you won't be SSH-ing into servers to upgrade the crypto libraries;
-  Few superpowers like travelling to the past, speculative writes or branching reality.


Datomic is a particularly good fit for the following use cases:

1. Read heavy Applications with complex data relationships
2. Applications that requires data auditing to make sense of the past
3. Data sets with complex, unknown data access patterns

### Ready to learn more?

Start with the key concepts to learn about datom, schema, entity, and other basic elements of Datomic. If you want the computer science background on Datomic, check out the section on the Datomic Paper.

If you want to get your hands dirty, set up your environment then start with the section on working with single items. Then you can move on to working with multiple items using Queries, Pull & Entity API.


Still want more? Head to Additional Reading to find the best community resources on Datomic.
