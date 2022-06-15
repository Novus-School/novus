# Clojure Primer

Contents
- What is Clojure
- State
- Clojure is a Value Oriented Language
-

## What is Clojure?


- Clojure is a functional programming language designed by Rich Hickey. Clojure is a modern lisp, it has no host, but rather runs on top of JVM. Clojure gives you a lot of leverage. Cooperative in a sense that it does try to reinvent the wheel for things that need no reinventing. But rather, Clojure can consume any java library. Combination of Immutablity + edn syntax + interactive development experience and hosted language makes it extremely well positioned for writing modern applications.

As the application grows, it’s imperative to be able to reason about parts of the application in isolation. It’s equally important to have code that is testable and reusable. Let’s take a look at the aspects of functional programming that facilitate these qualities.


## State

### Clojure is a Value Oriented Language


" Anyone who has ever telephoned a support desk for a software system and
been told to “try it again”, or “reload the document”, or “restart the program”, or “reboot your computer” or “re-install the program” or even “reinstall the operating system and then the program” has direct experience of
the problems that state1 causes for writing reliable, understandable software."
- Complexity Caused by State, Out of the tarpit


Managing State in an OOP based languages like JavaScript can be a tricky business

JavaScript provides to main collections for data modelling: Object and Array

Note: Set, Map et all are used in a specific scenarios

Lets talk about the properties/insight of these two collections

Insight #1: Object and Array fail equality comparison

```js
// object
{name: "vishal"} === {name: "vishal"}
// => fails

[42] === [42]
// => false
```

Reason: Objects/Array are like values

Consequence: Makes it diffucult to do basic things like comparing two objects

For example in clojure its simple use the `=` function to compare two collections and if they are the same, then you get true otherwise false

In JavaScript, one way to compare Object/Array is first converting the array into an object via JSON.stringify, and then comparing the two strings [strings are values in JavaScript]

```js
// Objects
JSON.stringify({name: "vishal"}) === JSON.stringify({name: "vishal"})
// => true

// Arrays
JSON.stringify([42]) === JSON.stringify([42])
// => true

```


In clojure there is only one way to pass the data, by value since everything in clojure is a value.


Clojure provides immutable data structures, which is a powerful way for modelling your domain. Let’s look at what makes these data structures such a powerful tool.

### Clojure offloads the reference tracking to language runtime

In most languages, data can be passed around either by value or by reference. Passing data by value is safe since we know that any changes we make to the data won’t have any effect outside the function. However, it’s also pro- hibitively expensive in many cases, so any substantial amount of data is passed around by reference. This makes code more difficult to reason about, as you have to know all the places where a piece of data is referenced to update it safely.

Another tactic to deal with reference problem is to deep copying the object before passing them around - deep copying reduces the risk of object mutation but it can also be resource expensive

Clojure's answer to this is Persistant Data Structures. In Clojure, every time a change is made to a data structure, a new revision is created. The price we pay when altering the data is proportional to the size of the change. When a piece of data is no longer referenced, it simply gets garbage-collected.

Instead of having to manually track every reference to a piece of data, we can offload this work to the language runtime. This allows us to effectively “copy” data anytime we make a change, without having to worry about where it comes from or what the scope of our change will be.

which leads to next point

### Immutability encourages writing pure functions

A pure function is simply a function that has no side effects i.e they are referentially transparent. Pure functions are awesome! They are one of the easiest code to main, test and can be reasoned about in isolation and because of these reasons the applications written using them are composed of individual self-contained components.


### In Clojure Programs, data and logic are kept seperate

To understand the importance of this principal, lets look at a very simple javascript application

```js
const jon = {
  name: "jon",
  age: 22,
  incrementAge: function() {
    this.age = this.age + 1
  }
}

jon.age // 22
jon.incrementAge()
jon.age // 23
```

This is an JavaScript object - and as you can see objects couple `data` with `code`. Now obviously this example can be written in a more functional manner. but thats not the point, the point is in real world you might have a large class with thousands of lines of code, and inside that class you might have many methods. Because object couples data code. It makes it very hard to re-use its methods.


Clojure makes a strict separation between data and code.

First to deal with data, the language provides a small set of common data structures, such as lists, maps, and sets.  All the functions (code) operate on these `data` structures; and when we come to a new problem, we can easily reuse any function we write.

Each function transform the data from one value to another. Clojure's way of solving the problem is declarative: first understand what kinds of data transformation that needs to be done and compose functions to make those transformations on data.

Declarative code focuses on what rather than how.


### Clojure Supports lazy programming

Why this is awesome: By leveraging the power of lazy sequences, we can work with memory larger than our computer

### Clojure is designed for Multi Core Programmming

Pure functions are parallelisable. Since they only depend on their arguments and nothing else, they can be safely computed in parallel. This means we can leverage extra cores and run many parallel algorithms.

Example: Let's say that we have a collection that we need to transform. We can start by writing a version using the map function. Should we discover that each operation takes a significant amount of time, then we can simply switch to using pmap to run the operations in parallel.


Traditional languages like Java uses locks and mutexes to manage state in multi threaded environment. The complexity is handed over to the poor developer, who is already crushed by other incidential complexities.

Clojure answer to this is software transactional memory (STM) API based on immutable data structures. With transactional memory, we no longer have to worry about manual locking when dealing with threads. Additionally, the data only needs to be locked for writing. Since the existing data is immutable, it can be read safely even while an update is happening.

Aka Clojure takes the stress from the programmer and instead offloads it to the STM, merci STM


### Clojure is symbiotic with an established Platform

VMs, not OSes, are the platforms of the future, providing:
  - type system,
  - libraries
  - Memory and other resource management
  - bytecode + jit compilation

Language as platform vs. language + platform
  - Old way - each language defines its own runtime.
    - GC, bytecode, type system, libraries etc
  - New way (JVM, .Net)
    - Common runtime independent of language


Language built for platform vs language ported-to platform

- Many new languages still take 'Language as platform' approach
- When ported, have platform-on-platform issues
   - Memory management, type-system, threading issues
   - Library duplication
   - If original language based on C, some extension libraries written in C don’t come over

Platforms are dictated by clients

- 'Must run on JVM' or .Net vs 'must run on Unix' or Windows
- JVM has established track record and trust level
  - Now also open source
- Interop with other code required
  - C linkage insufficient these days

Java/JVM is language + platform
- Not the original story, but other languages for JVM always existed, now embraced by Sun
- Java can be tedious, insufficiently expressive
- Lack of first-class functions, no type inference, etc
- Ability to call/consume Java is critical

Clojure is the language, JVM the platform

### Object Orientation is overrated

Born of simulation, now used for everything, even when inappropriate
 - Encouraged by Java/C# in all situations, due to their lack of (idiomatic) support for anything else

Mutable stateful objects are the new spaghetti code
- Hard to understand, test, reason about
- Concurrency disaster

Inheritance is not the only way to do polymorphism

"It is better to have 100 functions operate on one data structure than to have 10 functions operate on 10 data structures." - Alan J. Perlis

Clojure models its data structures as immutable objects represented by interfaces, and otherwise does not offer its own class system.

Many functions defined on few primary data structures (seq, map, vector, set).

Write Java in Java, consume and extend Java from Clojure.


### Polymorphism is a good thing

- Switch statements, structural matching etc yield brittle systems

- Polymorphism yields extensible, flexible systems

- Clojure multimethods decouple polymorphism from OO and types
  - Supports multiple taxonomies
  - Dispatches via static, dynamic or external properties, metadata, etc


### Concurrency and the multi-core future

- Immutability makes much of the problem go away
  - Share freely between threads
- But changing state a reality for simulations and for in-program proxies to the outside world
- Locking is too hard to get right over and over again
- Clojure’s software transactional memory and agent systems do the hard part
