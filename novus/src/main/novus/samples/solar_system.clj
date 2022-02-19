(ns novus.samples.solar-system
  (:require [datomic.client.api :as d]))

;; Step 1: Create a client for the Datomic System) using the d/client function
(def client (d/client {:server-type :dev-local
                       :system "datomic-samples"}))
;; Aside
(def dbs (d/list-databases client {}))



;; Step 2: Connect to a database. Connect function returns a database connection
;; This will be used to communicate with database when making transactions and queries
(def conn (d/connect client {:db-name "solar-system"}))

;; Aside: Play around
(comment
  (d/q '[:find ?doc
         :where [?d :db/doc ?doc]]
    (d/db conn))

  (d/q '[:find [(pull ?d [*]) ...]
         :where [?d :db/ident]]
    (d/db conn)))
;; Looks like nothing exists yet lets add some data
;; https://github.com/cognitect-labs/day-of-datomic-cloud/blob/master/datasets/solar-system.repl
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
