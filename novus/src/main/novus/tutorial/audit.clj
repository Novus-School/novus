(ns novus.tutorial.audit
  (:require [datomic.client.api :as d]))


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

;; Create client
(def client (d/client {:server-type :dev-local
                       :system "dev"}))


;; Create connections
(def conn    (d/connect client {:db-name "bank"}))
