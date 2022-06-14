(ns novus.router
 (:require
   [reitit.ring :as ring]
   [ring.adapter.jetty :as jetty]
   [ring.util.response :as response]
   [clojure.repl :as repl]
   [reitit.coercion.spec :as spec-coercion]
   ;; Routes
   [novus.student.routes :as student]
   ;; Middlewares
   [muuntaja.core :as m]
   [reitit.ring.middleware.muuntaja :as muuntaja]
   [reitit.ring.middleware.parameters :as parameters]
   [reitit.ring.middleware.exception :as exception]
   [reitit.ring.coercion :as coercion]
   [reitit.ring.middleware.multipart :as multipart]
   ;; Custom Middleware
   [novus.middleware :as novus]
   ;; Swagger Integration
   [reitit.swagger :as swagger]
   [reitit.swagger-ui :as swagger-ui]))


;;
(def swagger-docs
  ["/swagger.json"
   {:get
    {:no-doc true
     :swagger {:basePath "/"
               :info {:title "Novus Ion API Reference"
                      :description "The Novus API is organized around REST. Returns JSON, Transit (msgpack, json), or EDN  encoded responses."
                      :version "1.0.0"}}
     :handler (swagger/create-swagger-handler)}}])


(defn routes
  [env]
  (ring/ring-handler
    (ring/router
     [swagger-docs
      ["/v1"
       student/routes
       ["/courses/:id"
         {:get (fn [{{:keys [id]} :path-params}]
                 (response/response (str "Course ID:" id)))}]]]
     {:data {:env env
             :muuntaja m/instance
             :coercion spec-coercion/coercion
             :middleware [;; 1. query-params & form-params
                          parameters/parameters-middleware
                          ;; 2. content negotiation
                          muuntaja/format-negotiate-middleware
                          ;; 3. encoding response body
                          muuntaja/format-response-middleware
                          ;; 4. exception handling
                          exception/exception-middleware
                          ;; 5. decoding request body
                          muuntaja/format-request-middleware
                          ;; 6. coercing response bodys
                          coercion/coerce-response-middleware
                          ;; 7. coercing request parameters
                          coercion/coerce-request-middleware
                          ;; 8. Multi Part Params
                          multipart/multipart-middleware
                          ;; 9. Custom
                          novus/wrap-env]}})
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/"}))))


(comment
  (repl/doc ring/create-default-handler))
