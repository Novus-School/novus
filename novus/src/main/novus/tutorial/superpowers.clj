(ns novus.tutorial.superpowers
  (:require [datomic.client.api :as d]))

;;
;; ===================== Superpower: Authorization =====================
;; Step 1 Create: two data base: student + course
;;
(comment
  "3 Database API's"
  (d/list-databases (d/client {:server-type :dev-local
                                :system "dev"})
                    {})
  (d/create-database (d/client {:server-type :dev-local
                                :system "dev"})
                     {:db-name "bank"})
  (d/delete-database (d/client {:server-type :dev-local
                                :system "dev"})
                    {:db-name "bank"}))

;; Step 2: Create basic schema
(def purchase-schema
  [{:db/ident :purchase/id
    :db/doc "The purchase ID"
    :db/unique :db.unique/identity
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one}

   {:db/ident :purchase/account
    :db/doc "Purchase Account"
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}])

(def account-schema
  [{:db/ident :account/id
    :db/doc "The Account ID"
    :db/unique :db.unique/identity
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one}

   {:db/ident :account/customer
    :db/doc "Customer who own's this account"
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}])

(def customer-schema
  [{:db/ident :customer/id
    :db/doc "Customer ID"
    :db/unique :db.unique/identity
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one}

   {:db/ident :customer/name
    :db/doc "Customer name"
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}])

;;
;; Create client
(def client (d/client {:server-type :dev-local
                       :system "dev"}))


;; Create connections
(def bank-conn    (d/connect client {:db-name "bank"}))



;; Transact schemas
(comment
  (d/transact  bank-conn {:tx-data purchase-schema})
  (d/transact  bank-conn {:tx-data account-schema})
  (d/transact  bank-conn {:tx-data customer-schema})
  (d/tx-range  bank-conn {:start 6}))

(comment
  (java.util.UUID/randomUUID))

;; Transact fake data
(def cid #uuid "0fb7ea94-44af-46fa-98ca-0ddb5eb23123")
(def sample-customer {:customer/id cid
                      :customer/name "Jon Snow"})

(def aid #uuid "c579a9f6-f6c3-4718-8ecf-c3003021ee9a")
(def sample-account {:account/id aid
                     :account/customer [:customer/id cid]})

(def pid #uuid "24a96e20-f526-4f7f-ba38-4f684caa5607")
(def sample-purchase {:purchase/id  pid
                      :purchase/account [:account/id aid]})

(comment
  (d/transact bank-conn {:tx-data [sample-customer]})
  (d/transact bank-conn {:tx-data [sample-account]})
  (d/transact bank-conn {:tx-data [sample-purchase]}))

(comment
  (d/q '[:find ?cus
         :in $ ?cid ?pid
         :where
         [?pur :purchase/id ?pid]
         [?pur :purchase/account ?acc]
         [?acc :account/customer ?cus]
         [?cus :customer/id ?cid]]
      (d/db bank-conn)
      (:customer/id sample-customer)
      (:purchase/id sample-purchase)))

;; owns v1
(defn ownsv1? [cid pid db]
  (d/q '[:find ?cus
         :in $ ?customer-id ?purchase-id
         :where
         [?pur :purchase/id ?purchase-id]
         [?pur :purchase/account ?acc]
         [?acc :account/customer ?cus]
         [?cus :customer/id ?customer-id]]
    db cid pid))

(comment
  (ownsv1?
    (:customer/id sample-customer)
    (:purchase/id sample-purchase)
    (d/db bank-conn)))

;; recursive rule (logical OR)
(def owner-rules
  '[[(owner? ?cus-id ?e)
     [?e :customer/id ?cus-id]]
    [(owner? ?cus-id ?e)
     [?e ?ref-attr ?r]
     (owner? ?cus-id ?r)]])


(defn owns? [cid pid db]
  (d/q '{:find [?pur]
         :in [$ ?cus-id ?pur %]
         :where
         [(owner? ?cus-id ?pur)]}
    db cid [:purchase/id pid] owner-rules))

;;
(defonce errors (atom nil))

(comment
  @errors)
(comment
  (try
    (owns?
      (:customer/id sample-customer)
      (:purchase/id sample-purchase)
      (d/db bank-conn))
    (catch Exception ex
      (reset! errors (ex-data ex)))))
(comment
  @errors)
(comment
  (owns?
    (:customer/id sample-customer)
    (:purchase/id sample-purchase)
    (d/db bank-conn)))


(comment
  (ownsv1?
    (:customer/id sample-customer)
    (:purchase/id sample-purchase)
    (d/since (d/db bank-conn) 8)))
(comment
  (ownsv1?
    (:customer/id sample-customer)
    (:purchase/id sample-purchase)
    (d/as-of (d/db bank-conn) 0)))

;; ===== Tutorial: Speculative Writes with with
(comment
  (clojure.repl/doc d/with)
  (d/q '[:find ?name
         :in $
         :where [_ :customer/name ?name]]
    (:db-after (d/with (d/with-db bank-conn) {:tx-data [{:customer/id #uuid "771f1ec0-5abc-4a84-b76d-aafd2e065d4c"}]}))
    #_(:db-before (d/with (d/with-db bank-conn) {:tx-data [{:customer/id #uuid "771f1ec0-5abc-4a84-b76d-aafd2e065d4c"
                                                            :customer/name "Jane Doe"}]})))
  (d/q '[:find ?name
         :in $
         :where [_ :customer/name ?name]]
    (:db-after (d/with (d/with-db bank-conn) {:tx-data [[:db/add "-1" :customer/id #uuid "771f1ec0-5abc-4a84-b76d-aafd2e065d4c"]
                                                        [:db/add "-1" :customer/name "Joe Schmoe"]]}))))

;; ===============================================================================================
;; ==== Tutorial: Attribute predicates
;;
;;
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
    :db.attr/preds 'novus.superpowers/user-name?}])

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

;; =====================================================================
;; ===================== Superpower: DB Aggregation =====================
;; Step 1 Create: two data base: student + course
;;
(comment
  "3 Database API's"
  (d/list-databases (d/client {:server-type :dev-local
                                :system "dev"})
                    {})
  (d/create-database (d/client {:server-type :dev-local
                                :system "dev"})
                     {:db-name "student"})
  (d/create-database (d/client {:server-type :dev-local
                                :system "dev"})
                     {:db-name "course"}))

;; Step 2: Create basic schema
(def student-schema
  [{:db/ident :student/id
    :db/doc "The id of the account"
    :db/unique :db.unique/identity
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one}

   {:db/ident :student/first-name
    :db/doc "The first name of the account"
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}

   {:db/ident :student/last-name
    :db/doc "The last name of the account"
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}])

(def course-schema
  [{:db/ident :course/id
    :db/doc "The id of the course"
    :db/unique :db.unique/identity
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one}
   {:db/ident :course/creator
    :db/doc "The owner of the learning path"
    :db/valueType :db.type/ref
    :db/cardinality :db.cardinality/one}
   {:db/ident :course/name
    :db/doc "The name of the course"
    :db/valueType :db.type/string
    :db/cardinality :db.cardinality/one}
   {:db/ident :course/student-id
    :db/doc "Student who is taking this course"
    :db/valueType :db.type/uuid
    :db/cardinality :db.cardinality/one}])

;; Create client
(def client (d/client {:server-type :dev-local
                       :system "dev"}))


;; Create connections
(def student-conn    (d/connect client {:db-name "student"}))
(def course-conn    (d/connect client {:db-name "course"}))

;; Transact schemas
(comment
  (d/transact student-conn {:tx-data student-schema})
  (d/transact course-conn {:tx-data course-schema}))


;; transact fake data
(def sid #uuid "f3e3a9eb-ba08-46a0-97c5-2f383f584943")
(def cid #uuid "01806f28-625a-4a6a-940f-aacbafa5faef")
(comment
  (d/transact
   student-conn
   {:tx-data [{:student/id sid
               :student/first-name "Jon"
               :student/last-name "Snow"}]}))
(comment
  (d/transact
   course-conn
   {:tx-data [{:course/id cid
               :course/name "Intro to Programming"
               :course/student-id sid}]}))


;; Query Across Multiple Database
(comment
  (d/q '[:find ?course-name ?first-name ?last-name
         :keys course/name student/first-name student/last-name
         :in $cdb $sdb ?sid ?cid
         :where
         [$cdb ?course :course/id ?cid]
         [$cdb ?course :course/name ?course-name]
         [$sdb ?student :student/id ?sid]
         [$sdb ?student :student/first-name ?first-name]
         [$sdb ?student :student/last-name ?last-name]
         [$cdb ?course :course/student-id ?sid]]
    ; (d/db student-conn)
    (d/db course-conn)
    (d/db student-conn)
    sid
    cid))
