(ns novus.tutorial.tutorial
;;
;   Copyright (c) Cognitect, Inc. All rights reserved.
;   The use and distribution terms for this software are covered by the
;   Eclipse Public License 1.0 (http://opensource.org/licenses/eclipse-1.0.php)
;   which can be found in the file epl-v10.html at the root of this distribution.
;   By using this software in any fashion, you are agreeing to be bound by
;   the terms of this license.
;   You must not remove this notice, or any other, from this software.

 (:require [datomic.client.api :as d])
 (:import (java.util UUID)))

;;
(def client-cfg
  {:server-type :dev-local ;; :cloud
   :region "us-east-1"
   :system "dev"
   :endpoint "http://entry.bubbagumpshrimp.us-east-1.datomic.net:8182/"
   :proxy-port 8182})

(def client (d/client client-cfg))

(comment
  (d/list-databases client {})
  (d/create-database client {:db-name "inventory-tutorial"}))
(def conn (d/connect client {:db-name "inventory-tutorial"}))

(comment
  (d/transact
    conn
    {:tx-data [{:db/ident :red}
               {:db/ident :green}
               {:db/ident :blue}
               {:db/ident :yellow}]}))

(defn make-idents
  [x]
  (mapv #(hash-map :db/ident %) x))

(def sizes [:small :medium :large :xlarge])
(make-idents sizes)

(def colors [:red :green :blue :yellow])
(def types [:shirt :pants :dress :hat])

(comment
  (d/transact conn {:tx-data (make-idents colors)})
  (d/transact conn {:tx-data (make-idents sizes)})
  (d/transact conn {:tx-data (make-idents types)}))

(def schema-1
  [{:db/ident :inv/sku
    :db/valueType :db.type/string
    :db/unique :db.unique/identity
    :db/cardinality :db.cardinality/one}
   {:db/ident :inv/color
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident :inv/size
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident :inv/type
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}])

(comment
 (d/transact conn {:tx-data schema-1}))

(def sample-data
  (->> (for [color colors size sizes type types]
         {:inv/color color
          :inv/size size
          :inv/type type})
       (map-indexed
         (fn [idx map]
           (assoc map :inv/sku (str "SKU-" idx))))))

(comment
 (d/transact conn {:tx-data sample-data}))

(def db (d/db conn))

[:inv/sku "SKU-42"]

;; pull
(comment
  (d/pull
    db
    [{:inv/color [:db/ident]}
     {:inv/size [:db/ident]}
     {:inv/type [:db/ident]}]
    [:inv/sku "SKU-42"]))

;; repeat refs
(comment
 (d/q
   '[:find ?e ?sku
     :where
     [?e :inv/sku "SKU-42"]
     [?e :inv/color ?color]
     [?e2 :inv/color ?color]
     [?e2 :inv/sku ?sku]]
   db))

(def order-schema
  [{:db/ident :order/items
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/many
    :db/isComponent true}
   {:db/ident :item/id
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident :item/count
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}])

(comment
 (d/transact conn {:tx-data order-schema}))

(def add-order
  {:order/items
   [{:item/id [:inv/sku "SKU-25"]
     :item/count 10}
    {:item/id [:inv/sku "SKU-26"]
     :item/count 20}]})

(comment
 (d/transact conn {:tx-data [add-order]}))

(def db (d/db conn))
(comment
 (d/q
   '[:find ?sku
     :in $ ?inv
     :where
     [?item :item/id ?inv]
     [?order :order/items ?item]
     [?order :order/items ?other-item]
     [?other-item :item/id ?other-inv]
     [?other-inv :inv/sku ?sku]]
   db [:inv/sku "SKU-25"]))

(def rules
  '[[(ordered-together ?inv ?other-inv)
     [?item :item/id ?inv]
     [?order :order/items ?item]
     [?order :order/items ?other-item]
     [?other-item :item/id ?other-inv]]])

(comment
  (d/q
    '[:find ?sku
      :in $ % ?inv
      :where
      (ordered-together ?inv ?other-inv)
      [?other-inv :inv/sku ?sku]]
    db rules [:inv/sku "SKU-25"]))

;; time
(def inventory-counts
  [{:db/ident :inv/count
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}])

(comment
 (d/transact conn {:tx-data inventory-counts}))

(def inventory-update
  [[:db/add [:inv/sku "SKU-21"] :inv/count 7]
   [:db/add [:inv/sku "SKU-22"] :inv/count 7]
   [:db/add [:inv/sku "SKU-42"] :inv/count 100]])

(comment)
;;
(comment
  (d/pull
    db
    '[*]
    [:inv/sku "SKU-22"])
  (d/pull
    (d/db conn)
    '[*]
    [:inv/sku "SKU-22"]))
(comment
 (d/transact conn {:tx-data inventory-update}))

;; never had any 22s
(comment
  (d/transact
    conn
    {:tx-data [[:db/retract [:inv/sku "SKU-22"] :inv/count 7]
               [:db/add "datomic.tx" :db/doc "remove incorrect assertion"]]})

;; correct count for 42s
  (d/transact
    conn
    {:tx-data [[:db/add [:inv/sku "SKU-42"] :inv/count 1000]
               [:db/add "datomic.tx" :db/doc "correct data entry error"]]}))


(def db (d/db conn))
(d/q
  {:query '[:find ?sku ?count
            :where
            [?inv :inv/sku ?sku]
            [?inv :inv/count ?count]]
   :args [db]})
;=> [["SKU-42" 1000] ["SKU-21" 7]]


(comment
  (d/q
    '[:find (max 3 ?tx)
      :where
      [?tx :db/txInstant]]
    db))
;=> [[[13194139533334 13194139533333 13194139533332]]]

;; as-of query
(comment
  (d/tx-range conn {}))
(comment
 (def db-before (d/as-of db 13194139533334)))

;;

(comment
  (d/q
    '[:find ?sku ?count
      :where
      [?inv :inv/sku ?sku]
      [?inv :inv/count ?count]]
    db-before)
  (d/q
    '[:find ?sku ?count
      :where
      [?inv :inv/sku ?sku]
      [?inv :inv/count ?count]]
    (d/as-of db 13194139533334)))

;[["SKU-42" 100] ["SKU-42" 1000] ["SKU-21" 7] ["SKU-22" 7]]

;; history query
(comment
  (require '[clojure.pprint :as pp])
  (def db-hist (d/history db))
  (->> (d/q
         '[:find ?tx ?sku ?val ?op
           :where
           [?inv :inv/count ?val ?tx ?op]
           [?inv :inv/sku ?sku]]
         db-hist)
       (sort-by first)
       (pp/pprint))

  (d/delete-database client {:db-name "inventory-tutorial"}))
