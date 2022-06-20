(ns novus.auth.routes
  (:require [novus.middleware :refer [token-auth-mw]]))


;;
(defn say-hello-response [{{:keys [username]} :identity}]
    {:status 200
     :body {:message (str "Hello, " username)}})

(def authed-routes
  ["/authed"
   ["/say-hello" {:name ::say-hello
                  :get {:middleware [token-auth-mw]
                        :handler say-hello-response}}]])
