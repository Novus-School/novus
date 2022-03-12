(ns novus.samples.epl
  (:require [datomic.client.api :as d]
            [clojure.data.json :as json]))

;; Step 0 - Test data
(def fantasy (json/read-str (slurp "src/resources/fantasy.json")
                            :key-fn keyword))




;; query: tell me about Liverpool
(comment
  (keys (first (filter #(= "Liverpool" (:name %)) (vals (-> fantasy :teams :byId))))))


;; query:
;; Step 1 Define the configuration
(def db-name {:db-name "pl-fantasy"})
(def client-config {:server-type :dev-local
                    :system "dev"})


;; Step 2 - create a client - notice that the function is idempotent
(def client (d/client client-config))

;; Step 3 - create a database
;; always returns true
(def db (d/create-database client db-name))

;; Step 4 - create a connection
(def conn (d/connect client db-name))
;; note: if you try to create conn before creating the database you get an error

;; Step 5 - define schema
(def schema
  [{:db/ident :team/name
    :db/doc "Name of the team"
    :db/valueType :db.type/string
    :db/unique :db.unique/identity
    :db/cardinality :db.cardinality/one}
   {:db/ident :team/id
    :db/doc "ID of the team"
    :db/valueType :db.type/long
    :db/cardinality :db.cardinality/one}])

;; Step 6 transact schema
(comment
 (d/transact conn {:tx-data schema}))

;; Step 7 - Add mock teams
(def mock-teams  (map (fn [{:keys [name id]}] {:team/name name
                                               :team/id id})
                      (vals (-> fantasy :teams :byId))))

(comment
  (d/transact conn {:tx-data mock-teams}))

;; Step 8 - Ask Questions
(comment
  "Question 1 - give me the list of all the team names"
  (d/q '[:find (pull ?team [*])
         :where [?team :team/name ?team-name]]
       (d/db conn)))
(comment
  "Question 2 - tell me everthing about Liverpool"
  (ffirst (d/q '[:find (pull ?team [*])
                 :where [?team :team/name "Liverpool"]]
               (d/db conn)))
  ; "Question 2b - tell me everthing about Liverpool - map form"
  ; (ffirst (d/q {:find (pull ?team [*])
  ;               :where [?team :team/name "Liverpool"]}
  ;              (d/db conn)))
  ;; directly pull top 10 teams highest team ID
  ;; learned from: https://github.com/cognitect-labs/day-of-datomic-cloud/blob/master/tutorial/aggregates.repl
  (d/index-pull (d/db conn) {:index :avet
                             :selector '[*]
                             :start [:team/id]
                             :reverse true
                             :limit 10}))
  ;; Task: find the team with lowest ID

(comment
  (map (fn [[k v]] {:key v}) (:keys (d/history (d/db conn))))
  ;; https://clojuredocs.org/clojure.core/sorted-map
  (into (sorted-map) (:keys (d/history (d/db conn)))))
