## What is Immutable Stack? (CARD Stack)

Immutable stack aka CARD Stack is a web development framework. It consists of Clojure/Script, AWS, Reagent, and Datomic Cloud as its working components. Here are the details of what each of these components is used for in developing a web application when using CARD stack:

### Clojure/Script

Clojure is a dynamic, general-purpose programming language, combining the approachability and interactive development of a scripting language with an efficient and robust infrastructure for multithreaded programming. Clojure is a compiled language, yet remains completely dynamic – every feature supported by Clojure is supported at runtime. Clojure provides easy access to the Java frameworks, with optional type hints and type inference, to ensure that calls to Java can avoid reflection.

Clojure is a dialect of Lisp, and shares with Lisp the code-as-data philosophy and a powerful macro system. Clojure is predominantly a functional programming language, and features a rich set of immutable, persistent data structures. When mutable state is needed, Clojure offers a software transactional memory system and reactive Agent system that ensure clean, correct, multithreaded designs.

### AWS

Amazon Web Services (AWS) is the world’s most comprehensive and broadly adopted cloud platform, offering over 200 fully featured services from data centers globally.

### Reagent

Reagent provides a minimalistic interface between ClojureScript and React. It allows you to define efficient React components using nothing but plain ClojureScript functions and data, that describe your UI using a Hiccup-like syntax.

### Datomic Cloud

Datomic Cloud is a distributed database that provides ACID transactions, flexible schema, powerful Datalog queries, complete data history, and SQL analytics support. Datomic is highly available, scales horizontally, integrates with AWS security best practices, and can serve your entire application with ions.

---

## DIAGRAM

---

As shown in the illustration above, the user interacts with the UI components (Reagent/React) at the application front-end residing in the browser. This frontend is served by the application backend residing in a ring server, written in Clojure

Any interaction that causes a data change request is sent to the Clojure based ring/jetty server, which grabs data from the Datomic Cloud database if required, and returns the data to the frontend of the application, which is then presented to the user.

## Why Immutable Stack?

- Interactive Experience
- No More Data Loss
- Cloud First Design
- Simpler systems

AWS

- Usage-based pricing, elastic scaling, and simplified operations.

Clojure/Script

- Functional, Hosted, Lisp
- Designed for concurrency

Datomic

- Data Oriented Design
- Entity Orientation
- Read Scalability
- Flexibility (Read operations separated read from write)
- Treat Database as a Value
- Point In Time Access
- Upsert
