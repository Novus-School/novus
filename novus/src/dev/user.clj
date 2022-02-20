(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [novus.server]
            [datomic.client.api :as d]
            [clojure.edn :as edn]))

(ig-repl/set-prep!
  (fn [] (-> "src/dev/resources/config.edn" slurp ig/read-string)))

(def start-dev ig-repl/go)
(def stop-dev ig-repl/halt)
(def restart-dev ig-repl/reset)
(def reset-all ig-repl/reset-all)

(def app (-> state/system :novus/app))
(def db (-> state/system :db/datomic))

(comment
  (start-dev)
  (stop-dev))


;; Transact novus schema
(def schema (-> "src/resources/schema.edn" slurp edn/read-string))
(comment
  (d/transact (:conn db) {:tx-data schema}))
