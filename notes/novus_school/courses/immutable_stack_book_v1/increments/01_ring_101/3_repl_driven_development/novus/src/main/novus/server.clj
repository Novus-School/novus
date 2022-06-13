(ns novus.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]))

(defn handler [request]
  (response/response "Hello Immutable Stack"))

(defn -main []
  (jetty/run-jetty handler {:port 3000 :join? false}))

(comment
  (def server (-main))
  (identity server)
  (.stop server))
