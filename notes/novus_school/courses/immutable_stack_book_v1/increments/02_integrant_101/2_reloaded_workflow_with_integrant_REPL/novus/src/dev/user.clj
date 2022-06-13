(ns user
  (:require [integrant.repl :as ig-repl :refer [set-prep! go halt reset]]
            [integrant.core :as ig]
            [novus.server :refer [config]]))

;; Step 1:
(ig-repl/set-prep!
  (fn []
    (let [config (-> "config/dev.edn" slurp ig/read-string)]
      (ig/load-namespaces config)
      config)))

;; Step 2: Start, stop and restart the system
(comment
  (go)   ;; starts the system
  (halt) ;; Halts the system
  (reset)) ;; resets the system + reloads all namespace
