# Datomic Cloud Deployment

## Objective: Deploy the application to aws using ion

## Prerequisites

Before you begin developing an ion application, you will need:

- [x] a Datomic system using a split stack
- [x] the Clojure CLI
- [x] the ion-dev tools
- [x] git

## Steps

1. `add` config/dev.edn
2. Add Datomic Components
3. Add Other Components
4. Routing (define one route - /students)
5. Install ion + client-cloud
6. Configure ion entry points
7. deploy

Step 1: `add` config/dev.edn

```clj
{:novus.components.jetty/server {:handler #ig/ref :novus.server/app
                                 :port 3000}
 :novus.server/app {:datomic #ig/ref :novus.components.datomic-dev-local/db
                    :auth0 #ig/ref :novus.components.auth0/auth}
 :novus.components.auth0/auth {:client-secret "auth0-client-secret"}
 :novus.components.datomic-dev-local/db {:server-type :dev-local
                                         :system "dev"
                                         :db-name "novus"
                                         :storage-dir :mem}}

```

Step 2: Add Datomic Components

- three in total
  i. datomic - loads schema, data sets etc

```clj
(ns novus.components.datomic
  (:require [datomic.client.api :as d]
            [clojure.edn :as edn]
            [clojure.java.io :as io]))

(defn ident-has-attr?
  [db ident attr]
  (contains? (d/pull db {:eid ident :selector '[*]}) attr))

(defn load-dataset
  [conn]
  (let [db (d/db conn)
        tx #(d/transact conn {:tx-data %})]
    (when-not (ident-has-attr? db :student/id :db/ident)
      (tx (-> (io/resource "novus/schema.edn") slurp edn/read-string)))))

```

      ii. datomic-dev-local - local db component

```clj
(ns novus.components.datomic-dev-local
  (:require [novus.components.datomic :as datomic]
            [datomic.client.api :as d]
            [datomic.dev-local :as dl]
            [integrant.core :as ig]))

(defmethod ig/init-key ::db
  [_ config]
  (println "\nStarted DB")
  (let [db-name (select-keys config [:db-name])
        client (d/client (select-keys config [:server-type :system]))
        _ (d/create-database client db-name)
        conn (d/connect client db-name)]
    (datomic/load-dataset conn)
    (assoc config :conn conn)))

(defmethod ig/halt-key! ::db
  [_ config]
  (println "\nStopping DB")
  (dl/release-db (select-keys config [:system :db-name])))

```

      iii. datomic-cloud    - prod db component

```clj
(ns novus.components.datomic-cloud
 (:require [datomic.client.api :as d]
           [novus.components.datomic :as datomic]
           [integrant.core :as ig]))

(defmethod ig/init-key ::db
  [_ config]
  (println "\nStarted DB")
  (let [db-name (select-keys config [:db-name])
        client (d/client (select-keys config [:server-type :system :region :endpoint]))
        list-databases (d/list-databases client {})]
    (when-not (some #{(:db-name config)} list-databases)    ;; is this required?
      (d/create-database client db-name))                   ;; or will calling create-db with the same name just return true?
    (let [conn (d/connect client db-name)]
      (datomic/load-dataset conn)
      (assoc config :conn conn))))


```

Step 3: Add other components (external integration, http server etc)
i. components/jetty - jetty adapter

```clj
(ns novus.components.jetty
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty])
  (:import [org.eclipse.jetty.server Server]))

;;
;; 2.2 :adapter/jetty
(defmethod ig/init-key ::server [_ {:keys [handler port]}]
  (jetty/run-jetty handler {:port port
                            :join? false}))

(defmethod ig/halt-key! ::server [_ ^Server server]
  (.stop server))

```

      ii. components/auth0 - auth0

```clj
(ns novus.components.auth0
  (:require [integrant.core :as ig]))

(defmethod ig/init-key ::auth
  [_ config]
  (println "\nConfigured auth0")
  config)


```

Step 4: Added routing (created student service)

1. Add reitit dependency
2. define student service
   - handlers (return mock for now)

```clj
(ns novus.student.handlers
  (:require [novus.auth0 :as auth0]
            [clj-http.client :as http]
            [muuntaja.core :as m]
            [datomic.client.api :as d]
            [ring.util.response :as rr]))
            ; [datomic.client.api :as d]))

(defonce req-atom (atom nil))
(comment
  @req-atom)


(defn browse
  [{{{:keys [conn]} :datomic} :env
    :as req}]
  (reset! req-atom req)
  #_(rr/response {:students (d/q '[:find (pull ?student [*])
                                   :where
                                   [?student :student/id]]
                               (d/db conn))})
  (rr/response {:students [[{:db/id 87960930222227,
                             :student/id #uuid "0515a5fa-f177-44f0-8144-d6bdcc403564",
                             :student/first-name "Lynn",
                             :student/last-name "Margulis"}]
                           [{:db/id 87960930222228,
                             :student/id #uuid "1c1bae77-13fa-4cd1-b595-6c86fdd55946",
                             :student/first-name "Galileo",
                             :student/last-name "Galilei"}]]}))

(comment
  (browse @req-atom))


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

3. create router `router.clj`

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

Step 5: Install ion + client-cloud

`deps.edn`

```clj
{:paths ["src/main" "src/resources"]
 :deps
 {org.clojure/clojure {:mvn/version "1.10.3"}
  ...
  com.datomic/ion {:mvn/version "1.0.59"}
  com.datomic/client-cloud {:mvn/version "1.0.119"}}
```

Step 6: Configure ion entry points

Ion applications are arbitrary Clojure code, exposed to consumers via one or more entry points. An entry point is a function with a well-known signature. There are five types of of entry points for different callers, each with a different function signature.

Lambda and HTTP direct are external entry points. They expose AWS Lambdas and web services, respectively.

Internal entry points are callbacks that extend the Datomic Client API with your code. They include transaction functions, query functions, and pull xforms.

In our case we qare interested at HTTP Direct Entry Point

HTTP Direct Entry Point

A web entry point is a function that takes the following input map and returns an output map. The input and output maps are a subset of the Clojure Ring Spec.

Step 6.1: Create the entry function handler - Create `novus.ion` namespace - Define prod config - use `datomic.ion/get-params` to load SSM parameters - Define a function called `app`, that returns the request handler - Define a function called `handler`, which calls the `app` function. This will be our web entry point function

```clj
(ns novus.ion
 (:require [integrant.core :as ig]
           [novus.components.auth0 :as auth0]
           [novus.components.datomic-cloud :as datomic-cloud]

           [datomic.ion :as ion]
           [novus.server :as server]))

(def integrant-setup
  {::server/app {:datomic (ig/ref ::datomic-cloud/db)
                 :auth0 (ig/ref ::auth0/auth)}
   ::auth0/auth {:client-secret (get (ion/get-params {:path "/datomic-shared/prod/novus/"}) "auth0-client-secret")
                 :client-id (get (ion/get-params {:path "/datomic-shared/prod/novus/"}) "auth0-client-id")}
   ::datomic-cloud/db {:server-type :ion
                       :region "us-east-1"
                       :system "app-prod"
                       :db-name "app-prod"
                       :endpoint "https://3bgnoq0ny3.execute-api.us-east-1.amazonaws.com"}})
                       ; :system "novus-prod"
                       ; :db-name "novus-prod"
                       ; :endpoint "https://9avu4sblfa.execute-api.us-east-1.amazonaws.com"}})

(def app
  (delay
    (-> integrant-setup ig/prep ig/init ::server/app)))

(defn handler
  [req]
  (@app req))
```

Step 6.2: Configure `ion-config.edn`

```clj
{:allow []
 :lambdas {}
 ;; caveat: http-direct only works with split stack
 :http-direct {:handler-fn novus.ion/handler}
 :app-name "my-app"}


```

Now that we have configured ion entry points, its time to deploy our application

### Step 7: Deploy Application

#### Push

You capture the current state of your application code by invoking push. push creates a named CodeDeploy revision in Amazon S3, which you can later deploy to one or more Datomic compute groups. If you are working on committed code with no local deps you will get a repropducible revision named after your commit. For work in progress, you will have to supply a name for your (unreproducible) revision.

push reads your deps.edn and ensures all library dependencies and local code are moved to S3. In addition, it creates a first-class revision in Code Deploy. push is smart about minimizing the transfer of code to S3.

```clj
clj -A:ion-dev '{:op :push}'
```

If you wait a little, you will receive a map that contains the compand to deploy your app

#### Deploy

When you create a primary compute group or query group, you associate it with a Code Deploy application. The :app-name in your ion-config.edn connects your code to a Code Deploy application and determines which compute groups you can deploy to.

Datomic Cloud manages a Code Deploy deployment group for each compute group. The ion deploy command deploys a previously-pushed ion revision to a deployment group.

deploy uses AWS Step Functions to:

Deploy your code and dependencies to the compute group using Code Deploy. Code Deploy works by moving code from S3 to the compute group's EC2 instances and cycling the Datomic process with a newly-extended classpath, in a rolling fashion. This is much faster than cycling EC2 instances. Deploy is smart about minimizing the transfer of code from S3.
[optionally] ensure Lambdas If you've configured Lambdas, deploy ensures an AWS Lambda corresponding to each lambda entry point. These AWS Lambdas are in fact just lightweight proxies that forward invocations to your functions running on the Datomic cluster (i.e. your code is not running in AWS Lambda). In this way your code runs near the data and cache, and without the limitations and complexities of running in the Lambda execution context.

Lets deploy our application using the `:deploy` operation

```
clojure -A:ion-dev '{:op :deploy, :group app-primary, :rev "d4fb5745836e77f991ce368e684f0b8e2e6ad278"}'
```

This operation returns a map that looks like this

```clj
{:execution-arn
 arn:aws:states:us-east-1:118340284428:execution:datomic-app-primary:app-primary-d4fb5745836e77f991ce368e684f0b8e2e6ad278-1655074599874,
 :status-command
 "clojure -A:ion-dev '{:op :deploy-status, :execution-arn arn:aws:states:us-east-1:118340284428:execution:datomic-app-primary:app-primary-d4fb5745836e77f991ce368e684f0b8e2e6ad278-1655074599874}'",
 :doc
 "To check the status of your deployment, issue the :status-command."}
```

#### deploy-status

To check the status of your deployment, we can issue the :status-command.

```

clojure -A:ion-dev '{:op :deploy-status, :execution-arn arn:aws:states:us-east-1:118340284428:execution:datomic-app-primary:app-primary-d4fb5745836e77f991ce368e684f0b8e2e6ad278-1655074599874}'

```

If the deployment succeeds, then you should see a map like this

```clj
{:deploy-status "SUCCEEDED", :code-deploy-status "SUCCEEDED"}
```
