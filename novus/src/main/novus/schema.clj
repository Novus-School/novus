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


;; Installing an attribute definition
(comment
  "- Attribute definitions are represented as clojure maps (see Data Structures) that
     can be submitted to a database as part of a transaction.
   - It is idiomatic to use the transaction map data structure, as shown in the following example.
   - It defines an attribute named :person/name of type :db.type/string with :db.cardinality/one
     that is intended to capture a person's name.
")
(def person-name-attr
 {:db/ident :person/name
  :db/valueType :db.type/string
  :db/cardinality :db.cardinality/one
  :db/doc "A person's name"})

(comment
  "- Leading underscores are used for reverse lookup in pull. If your attribute has a
     leading underscore then you will not be able to use reverse lookup with that attribute.

   - Attribute installation is atomic. All the required information for an attribute must be specified in a
     single transaction, after which the attribute will be immediately available for use.")


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

;; ===============================================================================================
;; ==== Tutorial: Attribute predicates

(def client (d/client {:server-type :dev-local
                       :system "dev"}))

(comment
  "3 Database API's"
  (d/list-databases (d/client {:server-type :dev-local
                                :system "dev"})
                    {})
  (d/create-database (d/client {:server-type :dev-local
                                :system "dev"})
                     {:db-name "twitter"})
  (d/delete-database (d/client {:server-type :dev-local
                                :system "dev"})
                    {:db-name "twitter"}))

(comment
  " Rationale
  - You may want to constrain an attribute value by more than just its storage/representation
    type.
  - For example, an email address is not just a string, but a string with a particular format. In
    Datomic, you can assert attribute predicates about an attribute.
  - Attribute predicates are asserted via the :db.attr/preds attribute, and are fully-qualified
    symbols that name a predicate of a value.
  - Predicates return true (and only true) to indicate success. All other values indicate
    failure and are reported back as transaction errors.

  - Inside transactions, Datomic will call all attribute predicates for all attribute values,
    and abort a transaction if any predicate fails.

    For example, the following function validates that a user-name has a particular length:
 ")

;; Step 2: Create basic schema
(defn user-name? [username]
  (<= 6 (count username) 15))

(comment
  "To install the user-name? predicate, add a db.attr/preds value to an attribute, e.g.")
(def user-schema
  [{:db/ident :user/id
    :db/doc "User ID"
    :db/unique :db.unique/identity
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one}

   {:db/ident :user/username
    :db/doc "User Name"
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one
    :db.attr/preds 'novus.schema/user-name?}])

;;
(def twitter  (d/connect client {:db-name "twitter"}))

;; transact schema
(comment
  (d/transact  twitter {:tx-data user-schema}))

;; Transact datas
(comment
  (java.util.UUID/randomUUID))
(comment
   @error-atom)
(comment
  ;; Yay
  (d/transact twitter {:tx-data [{:user/id #uuid "ecb14c30-cf86-41b5-87a4-6cdb61f73807"
                                  :user/username "john.doe"}]}))

;;
(comment
  "A transaction that includes an invalid user-name will result in an incorrect anomaly that includes:
     - the entity id
     - the attribute name
     - the attribute value
     - the name of the failed predicate
     - the predicate return in :db.error/pred-return"

  "For example, the string 'john' is not a valid user-name? and will cause an anomaly like:")

(defonce error-atom (atom nil))
(comment
  @error-atom)
(comment
  (try
   (d/transact twitter {:tx-data [{:user/id #uuid "ecb14c30-cf86-41b5-87a4-6cdb61f73807"
                                   :user/username "john"}]})
   (catch Exception err
     (reset! error-atom (ex-data err)))))


(def error-message
  {:cognitect.anomalies/category :cognitect.anomalies/incorrect,
   :cognitect.anomalies/message [:cognitect.anomalies/message "Entity -9223301668109598081 attribute :user/username value 'john' failed pred novus.superpowers/user-name?"]
   :db.error/pred-return false,
   :db/error :db.error/attr-pred})

(comment
  "
   - Attribute predicates must be on the classpath of a process that is performing a transaction.
   - Attribute predicates can be asserted or retracted at any time, and will be enforced starting
     on the transaction after they are asserted.
   - Asserting or retracting an attribute predicate has no effect on attribute values that already
     exist in the database.
   - Attribute predicates can cancel the transaction directly.")

;; Summary
(comment
  "Summary
   - Attribute predicates are predicate functions allow you to constrain an attribute value
   - Attribute predicates are asserted via the :db.attr/preds attribute, and are fully-qualified
     symbols that name a predicate of a value.
   - Predicates return true (and only true) to indicate success. All other values indicate
     failure and are reported back as transaction errors.
   - Attribute predicates must be on the classpath of a process that is performing a transaction.")
