(ns novus.router
 (:require
   [reitit.ring :as reitit]
   [muuntaja.middleware :as muuntaja]
   [ring.adapter.jetty :as jetty]
   [ring.util.response :as response]
   [ring.middleware.reload :refer [wrap-reload]]))

(defn json-handler [req]
  (response/response (str "Hello Immutable Stack")))

(defn routes
  [env]
  (reitit/ring-handler
    (reitit/router
     [["/" {:get json-handler
            :post json-handler}]
      ["/courses/:id"
       {:get (fn [{{:keys [id]} :path-params}]
               (response/response (str "Course ID:" id)))}]])))
