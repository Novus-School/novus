(ns novus.server
  (:require [integrant.core :as ig]
            [novus.router :as router]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/init-key ::app
  [_ config]
  (println "\nStarted app")
  (app config))

;;
