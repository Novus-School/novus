(ns user
  (:require [integrant.repl :as ig-repl :refer [set-prep! go halt reset]]
            [integrant.core :as ig]
            [clojure.edn :as edn]
            [novus.server]
            [datomic.client.api :as d]))

;; Step 1:

(ig-repl/set-prep!
  (fn []
    (let [config (-> "config/dev.edn" slurp ig/read-string)]
      (ig/load-namespaces config)
      config)))

;; Step 2: Start, stop and restart the system
(comment
  (go)   ;; starts the system
  (halt) ;; Halts the system
  (reset)) ;; resets the system + reloads all namespace


;;
(comment
  "3 Database API's"
  (d/delete-database (d/client {:server-type :dev-local
                                :system "dev"})
                     {:db-name "novus"})
  (d/list-databases (d/client {:server-type :dev-local
                                :system "dev"})
                    {})
  (d/create-database (d/client {:server-type :dev-local
                                :system "dev"})
                     {:db-name "novus"}))


;; Create a client
(def client (d/client {:server-type :dev-local
                       :system "dev"}))

;; DB Name
(def db-name {:db-name "novus"})
(comment
   (d/create-database client db-name))


;; Create Connection
(def conn (d/connect client db-name))

;; 4. Create + transact schema
(def schema
   (-> "src/resources/novus/schema.edn" slurp edn/read-string))
(comment
  (d/transact conn {:tx-data schema}))

;; 5. Transact mock data
(def mock-data
   (-> "src/resources/novus/seed.edn" slurp edn/read-string))
(comment
  (d/transact conn {:tx-data mock-data}))

;; Notice that we are using the same function to add both schema and domain data. pretty cool


;; === Query
(comment
  ;; query
  (d/q '[:find (pull ?student [*])
         :where [?student :student/id]]
        (d/db conn))
  (d/q '[:find (count ?student)
         :where [?student :student/id]]
    (d/db conn)))
