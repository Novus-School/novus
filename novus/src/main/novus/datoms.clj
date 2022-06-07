(ns novus.datoms
  (:require [datomic.client.api :as d]))

;; Create connections

(def conn (-> {:server-type :dev-local
               :system "dev"}
             (d/client)
             (d/connect {:db-name "bank"})))

(comment
  (clojure.repl/doc d/datoms)
  "-------------------------
  datomic.client.api/datoms
  ([db arg-map])
    Returns an Iterable of datoms from an index as specified by arg-map:

   :index       One of :eavt, :aevt, :avet, or :vaet.
   :components  Optional vector in the same order as the index
                containing one or more values to further narrow the
                result

  Datoms are associative and indexed:

  Key     Index        Value
  --------------------------
  :e      0            entity id
  :a      1            attribute id
  :v      2            value
  :tx     3            transaction id
  :added  4            boolean add/retract

  For a description of Datomic indexes, see
  https://docs.datomic.com/cloud/query/raw-index-access.html.

  See namespace doc for timeout, offset/limit, and error handling.")
(comment
  (count (into #{} (map :e (d/datoms (d/db conn) {:index :eavt}))))
  (d/datoms (d/db conn) {:index :avet})
  (d/datoms (d/db conn) {:index :vaet}))
