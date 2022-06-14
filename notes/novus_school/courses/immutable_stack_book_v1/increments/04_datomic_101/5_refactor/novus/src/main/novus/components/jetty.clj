(ns novus.components.jetty
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]))

;; == 4. Server
(defmethod ig/init-key ::server
  [_ {:keys [handler port] :as _config}]
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
(defmethod ig/halt-key! ::server
  [_ jetty]
  (.stop jetty))
