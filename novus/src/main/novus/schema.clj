(ns ^{:doc "Comprehensive Datomic Schema tutorial"
      :author "Vishal Gautam"}
    novus.schema
  (:require [datomic.client.api :as d]))


;; Intro ===============================
(comment
  " What?
  - The facts that a Datomic database stores are represented by datoms.
  - Each datom is an addition or retraction of a relation between
      - an entity,
      - an attribute,
      - a value, and
      - a transaction.
   - The set of possible attributes a datom can specify is defined by a database's schema.

   Design Decision
   - Each Datomic database has a schema that describes the set of attributes that can be associated
     with entities.
   - A schema only defines the characteristics of the attributes themselves. It does not define which
     attributes can be associated with which entities.
   - Decisions about which attributes apply to which entities are made by an application.

   Consequence
   - This gives applications a great degree of freedom to evolve over time. For example,
     an application that wants to model a person as an entity does not have to decide up front whether
     the person is an employee or a customer. It can associate a combination of attributes describing
     customers and attributes describing employees with the same entity.
   - An application can determine whether an entity represents a particular abstraction, customer or employee,
     simply by looking for the presence of the appropriate attributes.
")


;; Attributes ===============================
(comment
  "- Schema attributes are defined using the same data model used for application data.
   - That is, attributes are themselves entities with associated attributes.
   - Datomic defines a set of built-in system attributes that are used to define new attributes.")

;; Required schema attributes
(comment
 "Every new attribute is described by three required attributes:
     - :db/ident
     - :db/valueType
     - :db/cardinality
")

;; 1. :db/ident
(comment
  "
  - :db/ident specifies the unique name of an attribute. It's value is a namespaced keyword with the lexical
    form :<namespace>/<name>.
  - It is possible to define a name without a namespace, as in :<name>, but a namespace is preferred
    in order to avoid naming collisions.
  - Namespaces can be hierarchical, with segments separated by ".", as in :<namespace>.<nested-namespace>/<name>.
  - The :db namespace is reserved for use by Datomic itself.
")

;; ========
;; 2. :db/valueType
(comment
  "
  - :db/valueType specifies the type of value that can be associated with an attribute.
  - The type is expressed as a keyword. Allowable values are listed below (there are 15 in total)

   1. Number
    - :db.type/bigdec (ex: 151.81M, 2946.3M)
    - :db.type/bigint (ex: 7N)
    - :db.type/double (ex: 3.14)
    - :db.type/float  (ex: 1.0)
    - :db.type/long   (ex: 42)

   2. Boolean
    - :db.type/boolean (ex: true | false)

   3. String
    - :db.type/string (ex: \"Bhutan\")

   4. Keyword
    - :db.type/keyword

   5. Reference to another entity
    - :db.type/ref)

   6. UUID Type
    - :db.type/uuid

   7. Time
    - :db.type/instant

   8. Tuple
    - :db.type/tuple

   9. Uniform Resource Type (URI)
    - :db.type/uri  (ex: \"https://www.datomic.com/details.html\")

   10. Small Binary Data
   - :db.type/bytes

   11. Symbol
    - :db.type/symbol")

(comment
   " Notes on Value Types

   - Keywords map to the native interned-name type in languages that support them.
   - Instants are stored as the number of milliseconds since the epoch.
   - Symbols map to the symbol type in languages that support them, e.g.
     clojure.lang.Symbol in Clojure
   - Consistent results in query depend on the scale matching for all BigDecimal comparisons.
     You are strongly encouraged to use a consistent scale per attribute.")

;; ========
;; 3. :db/cardinality
(comment
  "
   - :db/cardinality specifies whether an attribute associates a single value or a set of values
     with an entity. The values allowed for :db/cardinality are:
   - :db.cardinality/one - the attribute is single valued, it associates a single value with an entity
   - :db.cardinality/many - the attribute is multi valued, it associates a set of values with an entity
   - Transactions can add or retract individual values for multi-valued attributes.
")

;; Lab Time: Designing Duolingo Schema


;; Optional schema attributes

;; 1. Doc
(comment
  " - :db/doc specifies a documentation string.")

;; 2. Unique
(comment
  " - :db/unique - specifies a uniqueness constraint for the values of an attribute. Setting
      an attribute :db/unique also implies :db/index. The values allowed for :db/unique are:
       - :db.unique/value - only one entity can have a given value for this attribute.
         Attempts to assert a duplicate value for the same attribute for a different entity id will fail.
         More documentation on unique values is available here:
           https://docs.datomic.com/on-prem/schema/identity.html#unique-values
       - :db.unique/identity - only one entity can have a given value for this
         attribute and \"upsert\" is enabled; attempts to insert a duplicate value for a temporary
         entity id will cause all attributes associated with that temporary id to be merged with
         the entity already in the database. More documentation on unique identities is available here.

           https://docs.datomic.com/on-prem/schema/identity.html#unique-identities
   - :db.attr/preds - you can ensure the value of an attribute with one or more predicates that you supply.
   - :db/index specifies a boolean value indicating that an index should be generated for this attribute. Defaults to false.")

;; 3. Attir Preds
(comment
  " - :db.attr/preds - you can ensure the value of an attribute with one or more predicates that you supply.")

;; 4. Index
(comment
    " - :db/index specifies a boolean value indicating that an index should be generated for this attribute.
      - Defaults to false.")

;; 5. isComponent
(comment
  " - :db/isComponent specifies a boolean value indicating that an attribute whose type
      is :db.type/ref refers to a subcomponent of the entity to which the attribute is
      applied.
    - When you retract an entity with :db.fn/retractEntity, all subcomponents are also retracted.
    - When you touch an entity, all its subcomponent entities are touched recursively. Defaults to false.")

;; 6. noHistory
(comment
  " noHistory
    - :db/noHistory specifies a boolean value indicating whether past values of an attribute should
      not be retained. Defaults to false.

    - The purpose of :db/noHistory is to conserve storage, not to make semantic guarantees about
      removing information.
    - The effect of :db/noHistory happens in the background, and some amount of history may be visible
      even for attributes with :db/noHistory set to true.")

;; Lab Time: Unique, Index and Attribute Predicates, isComponent, noHistory
