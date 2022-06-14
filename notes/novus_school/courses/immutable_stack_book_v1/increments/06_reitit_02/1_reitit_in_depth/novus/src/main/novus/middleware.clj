(ns novus.middleware
  (:require [ring.util.response :as rr]))

(def wrap-env
 {:name ::env
  :description "Middleware for injecting env into request"
  ;; runs once - imporant for performance reasons
  :compile (fn [{:keys [env]} route-options]
             (fn [handler]
               (fn [request]
                 (handler (assoc request :env env)))))})
