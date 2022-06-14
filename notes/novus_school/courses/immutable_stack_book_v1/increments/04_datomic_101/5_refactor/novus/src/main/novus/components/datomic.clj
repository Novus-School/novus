(ns novus.components.datomic
  (:require [datomic.client.api :as d]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn ident-has-attr?
  [db ident attr]
  (contains? (d/pull db {:eid ident :selector '[*]}) attr))

(defn load-dataset
  [conn]
  (let [db (d/db conn)
        tx #(d/transact conn {:tx-data %})]
    (when-not (ident-has-attr? db :student/id :db/ident)
      (tx (-> (io/resource "novus/schema.edn") slurp edn/read-string))
      (tx (-> (io/resource "novus/seed.edn") slurp edn/read-string)))))
