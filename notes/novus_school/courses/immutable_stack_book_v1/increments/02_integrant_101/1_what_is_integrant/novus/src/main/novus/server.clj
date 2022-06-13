(ns novus.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [integrant.core :as ig]))

;; Step 1: Define Integrant Config
(def config
  {:adapter/jetty {:port 8080, :handler (ig/ref :handler/greet)}
   :handler/greet {:name "Immutable Stack"}})

;; Step 2: Define Initialization and Halting multimethods
(defmethod ig/init-key :adapter/jetty [_ {:keys [handler port]}]
  (jetty/run-jetty handler {:join? false
                            :port port}))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defmethod ig/init-key :handler/greet [_ {:keys [name]}]
  (defn handler [request]
    (response/response (str "Hello, " name))))

;; Step 3: Start/Stop the Integrant system
(comment
  (def system (ig/init config))
  (ig/halt! system))
