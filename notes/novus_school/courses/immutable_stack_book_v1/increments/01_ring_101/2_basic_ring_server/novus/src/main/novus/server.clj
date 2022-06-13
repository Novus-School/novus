(ns novus.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]))

#_(defn handler [request]
   {:status 200
    :headers {"Content-Type" "application/json"}
    :body "Hello Immutable Stack"})


(defn handler [request]
  (response/response "Hello Immutable Stack"))

(defn -main []
  (jetty/run-jetty handler {:port 3000 :join? false}))
