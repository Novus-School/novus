Step 4: Added routing (created student service)


4.2 Define student service

- handlers (return mock for now)

```clj
(ns novus.student.handlers
  (:require [ring.util.response :as rr]))


(defn browse
  [{{{:keys [conn]} :datomic} :env
    :as req}]
  (rr/response {:students [[{:db/id 87960930222227,
                             :student/id #uuid "0515a5fa-f177-44f0-8144-d6bdcc403564",
                             :student/first-name "Lynn",
                             :student/last-name "Margulis"}]
                           [{:db/id 87960930222228,
                             :student/id #uuid "1c1bae77-13fa-4cd1-b595-6c86fdd55946",
                             :student/first-name "Galileo",
                             :student/last-name "Galilei"}]]}))

```

    - routes

```clj
(ns novus.student.routes
  (:require [novus.student.handlers :as student]
            [novus.middleware :as mw]))

(def routes
  ["/students" {:swagger {:tags ["Student v2.0"]}
                :middleware [[mw/wrap-auth0]]}
   [""
    {:get {:handler student/browse
           :responses {201 {:body nil?}}
           :summary "Fetch list of students"}}]])

```

4.3 create router `router.clj`

```clj
(ns novus.router
  (:require [clojure.string :as string]
            [novus.student.routes :as student]
            [novus.middleware :as mw]
            [muuntaja.core :as m]
            [reitit.coercion.spec :as coercion-spec]
            [reitit.dev.pretty :as pretty]
            [reitit.ring :as ring]
            [reitit.ring.coercion :as coercion]
            [reitit.ring.middleware.dev :as dev]
            [reitit.ring.middleware.exception :as exception]
            [reitit.ring.middleware.muuntaja :as muuntaja]
            [reitit.ring.spec :as rs]
            [reitit.swagger :as swagger]
            [reitit.swagger-ui :as swagger-ui]))

(def swagger-docs
  ["/swagger.json"
   {:get
    {:no-doc true
     :swagger {:basePath "/"
               :info {:title "Novus API Reference"
                      :description "The Novus API is organized around REST. Returns JSON, Transit (msgpack, json), or EDN  encoded responses."
                      :version "1.0.0"}}
     :handler (swagger/create-swagger-handler)}}])

(defn router-config
  [env]
  {:validate rs/validate
   :reitit.middleware/transform dev/print-request-diffs
   :exception pretty/exception
   :data {:env env
          :coercion coercion-spec/coercion
          :muuntaja m/instance
          :middleware [swagger/swagger-feature
                       muuntaja/format-middleware
                       ;exception/exception-middleware
                       coercion/coerce-request-middleware
                       coercion/coerce-response-middleware
                       mw/wrap-env]}})

;; ring-handler - Creates a ring-handler (function) out of a router
(comment
  (clojure.repl/doc ring/ring-handler))

(defn routes
  [env]
  (ring/ring-handler
    (ring/router
      [swagger-docs
       ["/v1"
        student/routes]]
      (router-config env))
    (ring/routes
      (swagger-ui/create-swagger-ui-handler {:path "/"}))))

```

4. Modify `server.clj`

```clj
(ns novus.server
  (:require [novus.router :as router]
            [integrant.core :as ig]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/init-key ::app
  [_ config]
  (println "\nStarted app")
  (app config))

```
