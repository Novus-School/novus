(ns novus.server
  (:require [novus.router :as router]
            [integrant.core :as ig]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/init-key ::app
  [_ config]
  (println "\nStarted app")
  (app config))
