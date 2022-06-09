(ns novus.server
  (:require [novus.router :as router]
            [environ.core :refer [env]]
            [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]
            [datomic.client.api :as d]
            [datomic.dev-local :as dl]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/init-key ::app
  [_ config]
  (println "\nStarted app")
  (app config))
