(ns cheffy.samples.solar-system-pro
  (:require [datomic.client.api :as d]
            [datomic.dev-local :as dl]))

;; Step 1: Create a client for the Datomic System) using the d/client function
(def client-config {:server-type :dev-local
                    :system "custom"})

(def client (d/client client-config))

(defonce db-name {:db-name "solar-system-pro"})

;; Step 1.1 Create a new database
(d/create-database client db-name)
;; Aside
(def dbs (d/list-databases client {}))


;; Aside - Delete
(comment
  ; (d/delete-database client  {:db-name "solar-system-pro"})
  (dl/release-db  {:db-name "solar-system-pro"
                   :system "custom"}))
;; Step 2: Connect to a database. Connect function returns a database connection
;; This will be used to communicate with database when making transactions and queries
(def conn (d/connect client db-name))

;; Aside: Play around
(comment
  (d/q '[:find ?doc
         :where [?d :db/doc ?doc]]
    (d/db conn))

  (d/q '[:find [(pull ?d [*]) ...]
         :where [?d :db/ident]]
    (d/db conn)))


;; https://github.com/zperezedgar/SQL-Queries-in-a-solar-system-database/blob/master/queries%20in%20a%20solar%20system%20database.sql
;; Step 3 - Define solar system schema
(def schema
  [{:db/ident :object/name
    :db/doc "Name of a Solar System object."
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/meanRadius
    :db/doc "Mean radius of an object."
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/meanRadiusRel
    :db/doc "Mean radius of an object relative to earth."
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/volume
    :db/doc "Volume of an object"
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/volumeRel
    :db/doc "Volume of an object relative to earth"
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/mass
    :db/doc "Volume of an object"
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/massRel
    :db/doc "Volume of an object relative to earth"
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/density
    :db/doc "Density of an object"
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/surfaceGravity
    :db/doc "Gravity of an object"
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/surfaceGravityRel
    :db/doc "Gravity of an object relative to earth"
    :db/valueType :db.type/double
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/type
    :db/doc "Type of an object"
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/shape
    :db/doc "Type of an object"
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :object/parent
    :db/doc "Parent of an object"
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident :data/source
    :db/doc "Source of the data in a transaction."
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}])

(comment
 (d/transact conn {:tx-data schema}))


;; Step 4 - Transact sample data
(def data
  [{:db/doc "Solar system objects bigger than Pluto."
    :data/source "http://en.wikipedia.org/wiki/List_of_Solar_System_objects_by_size"}
   {:object/name "Sun"
    :object/meanRadius 696000.0}
   {:object/name "Jupiter"
    :object/meanRadius 69911.0}
   {:object/name "Saturn"
    :object/meanRadius 58232.0}
   {:object/name "Uranus"
    :object/meanRadius 25362.0}
   {:object/name "Neptune"
    :object/meanRadius 24622.0}
   {:object/name "Earth"
    :object/meanRadius 6371.0}
   {:object/name "Venus"
    :object/meanRadius 6051.8}
   {:object/name "Mars"
    :object/meanRadius 3390.0}
   {:object/name "Ganymede"
    :object/meanRadius 2631.2}
   {:object/name "Titan"
    :object/meanRadius 2576.0}
   {:object/name "Mercury"
    :object/meanRadius 2439.7}
   {:object/name "Callisto"
    :object/meanRadius 2410.3}
   {:object/name "Io"
    :object/meanRadius 1821.5}
   {:object/name "Moon"
    :object/meanRadius 1737.1}
   {:object/name "Europa"
    :object/meanRadius 1561.0}
   {:object/name "Triton"
    :object/meanRadius 1353.4}
   {:object/name "Eris"
    :object/meanRadius 1163.0}])

(comment
 (d/transact conn {:tx-data data}))

;; Step 5: Ask questions
(comment
 ;; Give me all the list of object that has the attribute :object/name
  (d/q '[:find (pull ?obj [*])
         :where [?obj :object/name ?name]]
    (d/db conn))
 ;; Note: ?name value placeholder can be omitted. So this works just fine
  (d/q '[:find (pull ?obj [*])
         :where [?obj :object/name]]
    (d/db conn)))
;; Give me the mean radius of Mars
(d/q '[:find ?meanRadius
       :where [?obj :object/name "Mars"]
              [?obj :object/meanRadius ?meanRadius]]
  (d/db conn))
