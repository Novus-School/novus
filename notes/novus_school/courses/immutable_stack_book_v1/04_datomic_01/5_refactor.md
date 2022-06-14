# Refactor


## Steps

1. `add` config/dev.edn
2. Add Datomic Components
3. Add Other Components


Step 1: `add` config/dev.edn

1. `config/dev.edn`

```clj
{:novus.components.jetty/server {:handler #ig/ref :novus.server/app
                                 :port 6060}
 :novus.server/app {:datomic #ig/ref :novus.components.datomic-dev-local/db}
 :novus.components.datomic-dev-local/db {:server-type :dev-local
                                         :system "dev"
                                         :db-name "novus"
                                         :storage-dir :mem}}

```


Step 2: Add Datomic Components

- two in total
  i. datomic - loads schema, data sets etc
  ii. datomic-dev-local - local db component

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
      (tx (-> (io/resource "novus/schema.edn") slurp edn/read-string))
      (tx (-> (io/resource "novus/seed.edn") slurp edn/read-string)))))


```  

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

3. Add Other Components

i. jetty
```clj
(ns novus.components.jetty
  (:require [integrant.core :as ig]
            [ring.adapter.jetty :as jetty]))

;; == 4. Server
(defmethod ig/init-key ::server
  [_ {:keys [handler port] :as _config}]
  (println (str "\nServer running on port " port))
  (jetty/run-jetty handler {:port port :join? false}))


;; ==== Halt is natually effectful
;; In the beginning you are converting config into system
;; Once you have the system you need a way to shut it down
;; this is where the halt method comes in
;; it is used to shut down the system
;; it goes in reverse topological order
;; -- meaning when we halt first we close the server, then the handler
;; and finally the database
;; -- but when we start the system, we start by creating database first
;; so you can say database is first to be created, last to be destroyed
(defmethod ig/halt-key! ::server
  [_ jetty]
  (.stop jetty))

```

4. Modify `server.clj`

```clj
(ns novus.server
  (:require [integrant.core :as ig]
            [novus.router :as router]))

(defn app
  [env]
  (router/routes env))

(defmethod ig/init-key ::app
  [_ config]
  (println "\nStarted app")
  (app config))

```

With that, we have successfully refactored our application. This is great since this will make the deployment step a smooth process

Now if we restart the our application and go to http://localhost:3000/v1/students, we should get a JSON response like so.

```
{"students":[[{"db/id":87960930222227,"student/id":"0515a5fa-f177-44f0-8144-d6bdcc403564","student/first-name":"Lynn","student/last-name":"Margulis"}],[{"db/id":87960930222228,"student/id":"1c1bae77-13fa-4cd1-b595-6c86fdd55946","student/first-name":"Galileo","student/last-name":"Galilei"}]]}

```
