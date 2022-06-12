# Integrating Integrant

Step 1: Add dependency

```clj
{:paths
 ["src/main" "src/resources"]
 :deps
 {org.clojure/clojure {:mvn/version "1.10.3"}
  ...
  integrant/integrant {:mvn/version "0.8.0"}}


 :aliases
 {:dev {:extra-paths ["src/dev"]
        :extra-deps {integrant/repl {:mvn/version "0.3.2"}}}
  ;; Allow novus to accept external REPL clients via a local connection to port 7777.
  :repl {:jvm-opts ["-Dclojure.server.repl={:port 7777 :accept clojure.core.server/repl}"]}}}
```

- We have added two dependencies:
  - integrant
  - integrant/repl

Step 2: Require the dependency in `server.clj`

```clj
(ns novus.server
 (:require [integrant.core :as ig]
           [ring.adapter.jetty :as jetty]))

```

Step 3: Define Integrant Config Map

```clj
(ns novus.server
 (:require [integrant.core :as ig]
           [ring.adapter.jetty :as jetty]))

(def config-map {:adapter/jetty {:port 8080 :handler (ig/ref :handler/greet)}
                 :handler/greet {:name "Novus"}})
```

Step 4: Define integrant multimethods

We are going to define two multimethods: :adapter/jetty and :handler/greet

```clj
(ns novus.server
 (:require [integrant.core :as ig]
           [ring.adapter.jetty :as jetty]
 (:import [org.eclipse.jetty.server Server])))

(def config-map {:adapter/jetty (ig/ref :handler/greet)
                 :handler/greet {:name "Novus"}})

(defmethod ig/init-key :handler/greet [_ opts]
  (fn [_]
     {:message (str "Hello "  (:name opts))}))


(defmethod ig/init-key :adapter/jetty [_ {:keys [handler port]}]
  (jetty/run-jetty handler {:port port
                            :join? false} ))

(defmethod ig/halt-key! :adapter/jetty [_ ^Server server]
  (.stop server))

```

Lets test our implementation

```clj
(comment
 (ig/init (ig/prep config-map)))
```

Invoking the following will start a server on port 8080

Step 5: Enable reloaded workflow

Modify `user.clj`

```clj
(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [novus.server :refer [config.map]]))

;; Step 1: Prepepare config
(ig-repl/set-prep!
  (fn []
     config))

;; Step 2: Reloaded workflow
(def start-dev ig-repl/go)
(def stop-dev ig-repl/halt)
(def restart-dev ig-repl/reset)
(def reset-all ig-repl/reset-all)


(comment
  (start-dev)
  (stop-dev)
  (restart-dev)
  (reset-all))

```
