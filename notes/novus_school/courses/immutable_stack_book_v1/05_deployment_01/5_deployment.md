# Datomic Cloud Deployment

In this lesson you will deploy your first Datomic Cloud application. Lets get started

## Prerequisites

Before you begin developing an ion application, we need to setup/install the following:

- [x] AWS CLI configured
- [x] the Datomic CLI tools installed
- [x] Created a Datomic system using a split stack
- [x] the ion-dev tools installed

## Objective:

Deploy the application to aws using ion


## Steps

1. Add Datomic Cloud Components
2. Install ion + client-cloud
3. Configure ion entry points
4. deploy


### Step 1: Add Datomic Cloud Component


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

### Step 2: Install ion + client-cloud

```
{:paths ["src/main" "src/resources"]
 :deps
 {org.clojure/clojure {:mvn/version "1.10.3"}
  ...
  com.datomic/ion {:mvn/version "1.0.59"}
  com.datomic/client-cloud {:mvn/version "1.0.119"}}
```


### Step 3: Configure ion entry points

Ion applications are arbitrary Clojure code, exposed to consumers via one or more entry points. An entry point is a function with a well-known signature. There are five types of of entry points for different callers, each with a different function signature.

Lambda and HTTP direct are external entry points. They expose AWS Lambdas and web services, respectively.

Internal entry points are callbacks that extend the Datomic Client API with your code. They include transaction functions, query functions, and pull xforms.

In our case we are interested at HTTP Direct Entry Point

HTTP Direct Entry Point

A web entry point is a function that takes the following input map and returns an output map. The input and output maps are a subset of the Clojure Ring Spec.


#### Step 3.1: Configure `resources/datomic/ion-config.edn`

```clj
{:allow []
 :lambdas {}
 ;; caveat: http-direct only works with split stack
 :http-direct {:handler-fn novus.ion/handler}
 :app-name "my-app"}


```

Now that we have defined `ion-config.edn`, lets define the handler function

#### Step 3.2: Create the entry function handler


```clj
(ns novus.ion
 (:require [integrant.core :as ig]
           [novus.components.datomic-cloud :as datomic-cloud]
           [datomic.ion :as ion]
           [novus.server :as server]))

(def integrant-setup
  {::server/app {:datomic (ig/ref ::datomic-cloud/db)}
   ::datomic-cloud/db {:server-type :ion
                       :region "us-east-1"
                       :system "app-prod"
                       :db-name "app-prod"
                       :endpoint "https://3bgnoq0ny3.execute-api.us-east-1.amazonaws.com"}})

(def app
  (delay
    (-> integrant-setup ig/prep ig/init ::server/app)))

(defn handler
  [req]
  (@app req))

```

- Create `novus.ion` namespace
- Define prod config
- use `datomic.ion/get-params` to load SSM parameters
- Define a function called `app`, that returns the request handler
- Define a function called `handler`, which calls the `app` function. This will be our web entry point function


Now that we have configured ion entry points, its time to deploy our application

### Step 4: Deploy Application

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

### How to access the prod endpoint

#### Option 1: You can use `datomic system describe-groups app-prod` command to find the prod url


```
[{"name":"app-primary",
  "type":"compute",
  "endpoints":
  [{"type":"client",
    "api-gateway-endpoint":
    "https://3bgnoq0ny3.execute-api.us-east-1.amazonaws.com",
    "api-gateway-id":"3bgnoq0ny3",
    "api-gateway-name":"datomic-app-prod-client-api"},
   {"type":"http-direct",
    "api-gateway-endpoint":
    "https://your-prod-api.execute-api.us-east-1.amazonaws.com",
    "api-gateway-id":"oqdnm8f3y6",
    "api-gateway-name":"datomic-app-prod-ions"}],
  "cft-version":"973",
  "cloud-version":"9132"}]
```

This command should return a list of compute groups. We are interested in `http-direct`. Copy the `api-gateway-endpoint`
and try it going to `v1/students`. You should see Lynn Margulis and Galileo being returned

```
{"students":[[{"db/id":87960930222227,"student/id":"0515a5fa-f177-44f0-8144-d6bdcc403564","student/first-name":"Lynn","student/last-name":"Margulis"}],[{"db/id":87960930222228,"student/id":"1c1bae77-13fa-4cd1-b595-6c86fdd55946","student/first-name":"Galileo","student/last-name":"Galilei"}]]}

```
