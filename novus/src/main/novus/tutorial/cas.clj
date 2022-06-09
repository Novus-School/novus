;   Copyright (c) Cognitect, Inc. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

(ns novus.tutorial.cas
  (:require
     [clojure.core.async :refer (<!!)]
     [cognitect.anomalies :as anom]
     [datomic.client.api :as d]
     [datomic.client.api.async :as d-async]))

;; edit to match your system
(def client-cfg
  {:server-type :dev-local ;; :cloud
   :region "us-east-1"
   :system "dev"
   :endpoint "http://entry.bubbagumpshrimp.us-east-1.datomic.net:8182/"
   :proxy-port 8182})

(def client (d/client client-cfg))

(def schema [{:db/ident :account/number
              :db/cardinality :db.cardinality/one
              :db/valueType :db.type/string
              :db/unique :db.unique/identity}
             {:db/ident :account/balance
              :db/cardinality :db.cardinality/one
              :db/valueType :db.type/double}])

(comment
  (d/delete-database client {:d-name "accounts-1"})
  (d/create-database client {:db-name "accounts-1"}))
(def conn (d/connect client {:db-name "accounts-1"}))

(comment
  (d/transact conn {:tx-data schema}))

(def initial-data [{:account/number "123"
                    :account/balance 100.00}])
(comment
  (d/transact conn {:tx-data initial-data}))

;; cas from 100 to 120
(def cas [:db/cas
          [:account/number "123"]
          :account/balance
          100.0
          120.0])

;; first cas succeeds
(comment
 (d/transact conn {:tx-data [cas]}))

;; repeat will fail, balance no longer 100.0 ; throws conflict error
(comment
  (d/transact conn {:tx-data [cas]})
  (ex-data (:unrepl.repl$i9hjMxfOQ2IzbCA5TVia2QQEJNg/ex (ex-data *e)))
  {:cognitect.anomalies/category :cognitect.anomalies/conflict,
   :cognitect.anomalies/message "Compare failed: 100.0 120.0",
   :datomic/cancelled true,
   :e [:account/number "123"],
   :a :account/balance,
   :v-old 100,
   :v 120,
   :db/error :db.error/cas-failed})
