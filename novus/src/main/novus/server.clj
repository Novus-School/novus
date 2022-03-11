(ns novus.server
  (:require [novus.router :as router]
            [environ.core :refer [env]]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [datomic.client.api :as d]
            [datomic.dev-local :as dl]
            [novus.samples.solar-system]
            [novus.samples.integrant]))

;; Key benefits of Integrant
;; Plurality
;;  - able to spin multiple services (differnt config)


;; Integrant methods
;; - 1. DB - Service
;; - 2. Auth - Service
;; - 3. Handler
;; - 4. Server

;; === 1. DB
(defmethod ig/init-key :db/datomic
  [_ config]
  (println "\nStarting DB")
  (let [db-name (select-keys config [:db-name])
        client  (d/client (select-keys config [:server-type :system]))
        _       (d/create-database client db-name)
        conn    (d/connect client db-name)]
   (assoc config :conn conn :client client)))

(defmethod ig/halt-key! :db/datomic
  [_ config]
  (dl/release-db (select-keys config [:system :db-name]))
  (println "\nStopping DB"))

(comment
  "3 Database API's"
  (d/delete-database (d/client {:server-type :dev-local
                                :system "dev"})
                     {:db-name "random"})
  (d/list-databases (d/client {:server-type :dev-local
                                :system "dev"})
                    {})
  (d/create-database (d/client {:server-type :dev-local
                                :system "dev"})
                     {:db-name "random"}))

(comment
  (ig/read-string (slurp "src/resources/config.edn")))

;; == 2. Auth0
(defmethod ig/prep-key :auth/auth0
  [_ config]
  (merge config {:client-secret (env :auth0-client-secret)}))

(defmethod ig/init-key :auth/auth0
  [_ auth0]
  (println "\nConfigured auth0")
  auth0)

;; == 3. Handler
(defn app
  [env]
  (router/routes env))

(defmethod ig/init-key :novus/app
  [_ config]
  (println "\nStarted app")
  (app config))

;; == 4. Server
(defmethod ig/prep-key :server/jetty
  [_ config]
  (merge config {:port (or (:port config) (Integer/parseInt (env :port)))}))

(defmethod ig/init-key :server/jetty
  [_ {:keys [handler port]}]
  (println (str "\nServer running on port " port))
  (jetty/run-jetty handler {:port port :join? false}))

;; ==== Halt is natually effectful
;; In the beginning you are converting config into system
;; Once you have the system you need a way to shut it down
;; this is where the halt method comes in
;; it is used to shut down the system
;; it goes in reverse topological order
;; -- meaning when we halt first we close the server, then the handler
;; and finally the database
;; -- but when we start the system, we start by creating database first
;; so you can say database is first to be created, last to be destroyed
(defmethod ig/halt-key! :server/jetty
  [_ jetty] ;; jetty is the value returned by (jetty/run-jetty ..)
  (.stop jetty))

(defn -main
  [config-file]
  (let [config (-> config-file slurp ig/read-string)
        config2 (ig/read-string (slurp "src/resources/configb.edn"))]
    (-> config ig/prep ig/init)))


;; Power of data driven architecture
(comment
  (ig/init (ig/prep (ig/read-string (slurp "src/resources/configb.edn"))))
  (ig/init (ig/prep (ig/read-string (slurp "src/resources/configc.edn")))))
