# Hello Datomic Cloud

By the end of this tutorial you will be able to:
1. learn the basics of datomic cloud
2. integrate a simple REPL development workflow which leads to my favourite part
3. learn about "flow" and flow driven development



### Step 1: Add datomic dev-local dependency


Lets add our second alias `:dev`.


```
{:paths ["src/main"]
 :deps    {org.clojure/clojure {:mvn/version "1.10.3"}
           ring/ring           {:mvn/version "1.9.4"}
           ;; Dependency Management
           integrant/integrant {:mvn/version "0.8.0"}
           ;; Routing
           metosin/reitit {:mvn/version "0.5.15"}}
 :aliases {:dev {:extra-paths ["src/dev"]
                 :extra-deps {nrepl/nrepl {:mvn/version "0.6.0"}
                              integrant/repl {:mvn/version "0.3.2"}
                              com.datomic/dev-local {:mvn/version "1.0.242"}}
                 :main-opts ["-m" "nrepl.cmdline"]}}}

```


In order to load `dev-local` alias we have to restart our repl. Lets do that

```
clj -M:dev
```

This will ensure three things:

1. We can invoke expressions inside `user.clj` file. `development`
2. Download `datomic/dev-local` library - which means we can finally start writing our app
3. Start a network REPL on random PORT


### Step 2: Require dev-local library

Next lets import datomic into the user namespace

Next lets learn about the three fundamental database functions
 - create-database, list-databases and delete-database


```clj
(ns user
  (:require [datomic.client.api :as d]))

;;
(comment
  "3 Database API's"
  (d/delete-database (d/client {:server-type :dev-local
                                :system "dev"})
                     {:db-name "random"})
  (d/list-databases (d/client {:server-type :dev-local
                                :system "dev"})
                    {})
  (d/create-database (d/client {:server-type :dev-local
                                :system "dev"})
                     {:db-name "random"}))
```

### Step 3: Our first datomic function - `create-database`

Creating a datomic database is a two step process

1. create a datomic

*Q: How do you create a datomic client*

Datomic has a `client` function. We need provide a map containing two things
 - `:server-type`
 - `:system`

Lets create an example client called `novus-client`

```clj
(def novus-client (d/client {:server-type :dev-local
                             :system "dev"}))
```

Now that we have our client, we can create our first database called "novus". We can create a new database using
`create-database`

`create-database` function takes two arguments
 - a datomic `client`
 - a map containing database name (`:db-name`)


```clj
(def db-name {:db-name "novus"})
(comment
   (d/create-database client db-name))
```

Next let's create a datomic connection. We need to create datomic connection because without connection we wont be able to do anything.

We can create a connection using the `connect` function. This function accepts a datomic client and a map containing :db--

```
(def conn (d/connect client db-name)
```

### Step 4: Create and transact datomic schema

In a relational database, you must specify a table schema that enumerates in advance the attributes (columns) an entity can have. By contrast, Datomic requires only that you specify the properties of individual attributes. Any entity can then have any attribute. Because all datoms are part of a single relation, this is called a universal schema.

Each Datomic database has a schema that describes the set and kind of attributes that can be associated with your domain entities.

A schema only defines the characteristics of the attributes themselves. It does not define which attributes can be associated with which entities. Decisions about which attributes apply to which entities are made by your application.

This gives applications a great degree of freedom to evolve over time. For example, an application that wants to model a person as an entity does not have to decide up front whether the person is an employee or a customer. It can associate a combination of attributes describing customers and attributes describing employees with the same entity. An application can determine whether an entity represents a particular abstraction, customer or employee, simply by looking for the presence of the appropriate attributes.

There are two kinds of attributes in Datomic:

Domain attributes - describe aspects of your domain data. You use domain attributes to describe the data about your domain entities.
Schema attributes - describe aspects of the schema itself. Schema attributes are built-in and cannot be extended. You use schema attributes to define your domain attributes.
For more information see the [schema documentation](https://docs.datomic.com/cloud/schema/schema-reference.html).


For our case, let start with modelling the student. To keep it simple we will define only 4 attributes:
- ID
- first name
- last name
- school


First lets add `src/resources/novus/schema.edn` file

```clj
  [{:db/ident :student/id
    :db/doc "The id of the account"
    :db/unique :db.unique/identity
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one}

   {:db/ident :student/first-name
    :db/doc "The first name of the account"
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :student/last-name
    :db/doc "The last name of the account"
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :student/school
    :db/doc "School of the student"
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}]
```

Now that we have defined our schema, lets transact it using the `transact` function. This function accepts two argument: `conn` and map containing :tx-data - value must be the schema.

Keep in mind that Datomic uses the same function to transact core data. Meaning schema is added the same manner as any other data, keeping the API simple.


```clj
(def schema
   (-> "src/resources/novus/schema.edn" slurp edn/read-string))

;; transact schema
(comment
  (d/transact conn {:tx-data schema}))

```


### Step 5: Transact mock students

First lets add the seed data insude `novus` directory

`/src/resources/novus/seed.edn`

```clj
[; 1. student attributes
 {:db/id "student-1"
  :student/id #uuid "0515a5fa-f177-44f0-8144-d6bdcc403564"
  :student/first-name "Lynn"
  :student/last-name "Margulis"}
 {:db/id "student-2"
  :student/id #uuid "1c1bae77-13fa-4cd1-b595-6c86fdd55946"
  :student/first-name "Galileo"
  :student/last-name "Galilei"}]

```

With our seed in place, we are now ready to transact mock data
```clj
(def mock-data (-> "src/resources/seed.edn" slurp edn/read-string))

(comment)
  (d/transact conn {:tx-data mock-data})
```

Notice the api for transacting data is the same as schema leading to code api consistency


### Step 6: Query

To query a database, you must first obtain a `connection` and a `database value`. The example below shows a simple query using the Synchronous API. This query fetches all the students.

`datomic.client.ap/q` is the primary entry point for Datomic query.

`q` Performs the query described by query and args, and returns a collection of tuples.

 - :query - The query to perform: a map, list, or string. Complete description.
  - :find - specifies the tuples to be returned.
  - :with - is optional, and names vars to be kept in the aggregation set but not returned
  - :in - is optional. Omitting ':in …' is the same as specifying ':in $'
  - :where - limits the result returned
 - :args - Data sources for the query, e.g. database values retrieved from a call to db, and/or rules.


#### Query 1: Give me list of all the students

```
;; get db value
(def db (d/db conn))

;; query
(d/q '[:find (pull ?student [*])
       :where [?student :student/id]]
      db)

;; =>
[[{:db/id 87960930222227,
   :student/id #uuid "0515a5fa-f177-44f0-8144-d6bdcc403564",
   :student/first-name "Lynn",
   :student/last-name "Margulis"}
 [{:db/id 87960930222228,
   :student/id #uuid "1c1bae77-13fa-4cd1-b595-6c86fdd55946",
   :student/first-name "Galileo",
   :student/last-name "Galilei"}]]

```


#### Query 2: Give me count of all the students
```
(d/q '[:find (count ?student)
       :where [?student :student/id]]
  (d/db conn)))

;; => [[2]]
```

Okay for now we are going to stop at datomic


## Summary
1. Learn the basics of `deps.edn` project
2. Learn the basics of `:paths` (specify custom path), `:deps` (dependencies) and `:aliases`
3. Learn how to start a network REPL in your `deps.edn`
4. Learn how to add custom dependencies
5. Learn how to overwrite default path properties
6. Learn how `:aliases` work
7. Learn how to run aliases using `-` command
8. Learn how to use `chlorine` to connect atom IDE to a nREPL
9. Learn how to add new dependencies
10. Learn how to create a basic datomic database
11. Learn the basics of Datomic Schema + how to add it to our database using `dispatch` function
12. Learn to leverage the transact function to add mock data
13. Learn the basics of query API - q function
