(ns novus.router
 (:require
   [reitit.ring :as ring]
   [ring.adapter.jetty :as jetty]
   [ring.util.response :as response]
   ;; Routes
   [novus.student.routes :as student]
   ;; Middlewares
   [muuntaja.core :as m]
   [reitit.ring.middleware.muuntaja :as muuntaja]))


(defn routes
  [env]
  (ring/ring-handler
    (ring/router
     [["/v1"
       student/routes
       ["/courses/:id"
         {:get (fn [{{:keys [id]} :path-params}]
                 (response/response (str "Course ID:" id)))}]]]
     {:data {:muuntaja m/instance
             :middleware [muuntaja/format-middleware]}})))
