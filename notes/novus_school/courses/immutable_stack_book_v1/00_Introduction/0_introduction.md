# Introduction

> “A complex system, contrary to what people believe, does not require complicated systems and regulations and intricate policies. The simpler, the better. Complications lead to multiplicative chains of unanticipated effects. Because of opacity, an intervention leads to unforeseen consequences, followed by apologies about the “unforeseen” aspect of the consequences, then to another intervention to correct the secondary effects, leading to an explosive series of branching “unforeseen” responses, each one worse than the preceding one.
Yet simplicity has been difficult to implement in modern life because it is against the spirit of a certain brand of people who seek sophistication so they can justify their profession.
Less is more and usually more effective. Thus I will produce a small number of tricks, directives, and interdicts—how to live in a world we don’t understand, or, rather, how to not be afraid to work with things we patently don’t understand, and, more principally, in what manner we should work with these. Or, even better, how to dare to look our ignorance in the face and not be ashamed of being human—be aggressively and proudly human. But that may require some structural changes.” - Antifragile, Things That Gain From Disorder


The main objective of this book is this: to give you a different point of view, when it comes to building web applications aka this book is about building simpler distributed systems.

Software development has never been this complex, ever and it's only going to get worse. JavaScript is the de facto programming language for the web, and PERN (Postgres, Express, React, Node) stack is probably one of the most popular stack choices for most startups (or at least that was in my case).

In this book I want to Introduce CARD Stack aka Immutable Stack. CARD stands for Clojure/Script AWS, Reagent and Datomic Cloud. Immutable Stack is probably the most powerful stack there is currently - some of the benefits of using Immutable Stack is out of the box auditing, simpler data model, powerful query engine


## Who is this book for

This book is for anyone who wants to learn how to build new kinds of distributed applications using the Immutable Stack.

This book is written with beginners in mind.

This book will also teach you how functional programming principles can be used to write simpler systems

## What is Complexity

"Unreliability, late delivery, lack of security — often even poor performance in large-scale systems can all be seen as deriving ultimately from unmanageable complexity." - Out of the Tarpit

In the classic paper "Out of the Tar Pit", Ben Moseley and Peter Marks believe that complexity of the system is its own enemy. Complexity makes it hard to build software. They argue that complexity is the root cause of the vast majority of problems with software today.

“Complexity kills,” Lotus Notes creator and Microsoft veteran Ray Ozzie famously wrote in a 2005 internal memo. “It sucks the life out of developers; it makes products difficult to plan, build, and test; it introduces security challenges; and it causes user and administrator frustration.”

On their prolific paper they also said this

"Simplicity is Hard".

That paper was released in February 06, 2006. At was also the time when Rich Hickey, creator of Clojure took Sabbatical leave to work on the Clojure programming language, which is the main focus of this book.

Rich worked as a C++/Java developer for 20+ years. He had seen what complexity was like. But what made him write Clojure? Because the guy was frustrated, he was tired of dealing with all the incidental complexities, which could have been completely avoided. He was tired of working in a C++/Java system. It was not worth the stress and pressure after all, stress kills

So he took two year sabbatical and two years later, Clojure was his answer. It had everything he needed to write simpler systems - persistent immutable data structures, modern lisp, designed to leverage concurrency, interactive development experience, hosted etc.


Clojure was his answer to functional level complexity, but what about system level.

## Complexity at a System Scale

SQL relational databases are the #1 choice for startup companies as well as enterprise companies. But SQL was not designed for building distributed systems, can only scale vertically without making drastic changes to the data model (sharding). Scaling horizontally is a big risk - failure to understand data can lead to big problems - thats why planning is so important when breaking you are breaking a monolith SQL based system into microservice. The moment you break down your data into microservices.

1. you lose querying power
2. you lost ACID transactions capability

So even if you do manage to horizontally scale, you are still losing some of the database capabilities.

Market answer to scalability problem was noSQL. But noSQL only solves one problem in my opinion - they solve the problem of distributed storage and solves many of the scalability problems posted by relational database - but noSQL databases are lacks the following features: expressive query capabilities of sql, strong consistency and ACID semantics

Rich believes that SQL databases add complexity at a System Scale - traditional relational databases are monolith systems with way too much responsibility: reading, transaction and storage - his answer - Datomic - a distributed database - which delegates reading, transaction and storage into its own processes.

Another problem with SQL is that read blocks write. This can be a big pain the ass for many reasons. People browsing the products shouldn't be affect by people buying the products, those are two completely separate activities. Long wait times are considered bad UX and you might even lose the potential customers. In Datomic reads don't block writes, and reads don't block each other. In short, reading is distributed in Datomic.

If you any work experience, then I am sure you must have done those mandatory security training - and one the thing that you will learn is about "SQL injection attacks".

According to google

"SQL injection, also known as SQLI, is a common attack vector that uses malicious SQL code for backend database manipulation to access information that was not intended to be displayed. This information may include any number"

To avoid such costly attacks, you are then taught about Parametrized queries. Parameterized queries force the developer to first define all the SQL code, and then pass in each parameter to the query later. This coding style allows the database to distinguish between code and data, regardless of what user input is supplied.


Okay lets read the last sentence again:

This coding style allows the database to distinguish between code and data, regardless of what user input is supplied.

Wait a second, if SQL query language was only defined in data and not in string, we would have NEVER had such problem but yet here we are using such ancient tool to build the future...


Luckily we don't see such problems in Datomic, since its everything data - queries are data, transactions are reified, schema is data
