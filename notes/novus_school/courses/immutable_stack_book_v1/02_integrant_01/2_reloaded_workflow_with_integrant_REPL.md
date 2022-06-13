# Aside: Reloaded workflow with Inregrant REPL

## 1. Intro
A Clojure library that implements the user functions of Stuart Sierra's reloaded workflow for Integrant.

It's very similar to `reloaded.repl`, except that it works for `Integrant`, rather than `Component`.

## Step 1: Installation

Add the following dependency to your dev profile:

```edn
{:aliases {:dev {:extra-deps {integrant/repl {:mvn/version "0.3.2"}}}
```

With that done, lets go ahead and restart our repl

```
clj -M:dev
```

## Step 2: Usage

Require the `integrant.repl` namespace in your `user.clj` file, and use
the `set-prep!` function to define a zero-argument function that returns
an Integrant configuration.

For example:

```clojure
(ns user
  (:require [integrant.repl :as ig-repl :refer [set-prep! go halt reset]]
            [novus.server :refer [config]]))

;; Step 1:
(set-prep! (constantly config))


;; Step 2: Start, stop and restart the system
(comment
  (go)   ;; starts the system
  (halt) ;; Halts the system
  (reset)) ;; resets the system + reloads all namespace
```

Use `go` to start the system. This will prepare the configuration and turn it into a running system,
which is stored in `integrant.repl.state/system`.


```clojure
user=> (go)
:initiated
```

`go` performs the `(prep)` and `(init)` steps together. Once the
system is running, we can stop it at any time:

```clojure
user=> (halt)
:halted
```

If we want to reload our source files and restart the system, we can
use:

```clojure
user=> (reset)
:reloading (...)
:resumed
```

Behind the scenes, Integrant-REPL uses [tools.namespace][]. You can
set the directories that are monitored for changed files by using the
`refresh-dirs` function:

```clojure
user=> (require '[clojure.tools.namespace.repl :refer [set-refresh-dirs]])
nil
user=> (set-refresh-dirs "src/clj")
("src/clj")
```

[tools.namespace]: https://github.com/clojure/tools.namespace/

----

Now that we have a reloaded workflow, remove `comment` block from `server.clj` and as well as
move the `config` to `config/dev.edn` in the root namespace

Lets first delete the comment block
```clj
(ns novus.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [integrant.core :as ig]))

;; Step 1: Define Integrant Config
(def config
  {:adapter/jetty {:port 8080, :handler (ig/ref :handler/greet)}
   :handler/greet {:name "Immutable Stack"}})

;; Step 2: Define Initialization and Halting multimethods
(defmethod ig/init-key :adapter/jetty [_ {:keys [handler port]}]
  (jetty/run-jetty handler {:join? false
                            :port port}))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defmethod ig/init-key :handler/greet [_ {:keys [name]}]
  (defn handler [request]
    (response/response (str "Hello 3, " name))))

```

Now that the comment block is deleted, lets move the `config` to `config/dev.edn`

Lets create directory in the rootnamespace and a file called `dev.edn` inside
```
mkdir config
cd config
touch dev.edn
```


```clj
{:adapter/jetty {:port 8080, :handler #ig/ref :handler/greet}
 :handler/greet {:name "Immutable Stack"}}
```

Note that since we are moving config from the `server` namespace, into a .edn file, we will have to replace ig/ref call with #ig/ref

Now that config if moved, our server.clj file should look like this

```clj
(ns novus.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [integrant.core :as ig]))

;; Step 2: Define Initialization and Halting multimethods
(defmethod ig/init-key :adapter/jetty [_ {:keys [handler port]}]
  (jetty/run-jetty handler {:join? false
                            :port port}))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defmethod ig/init-key :handler/greet [_ {:keys [name]}]
  (defn handler [request]
    (response/response (str "Hello, " name))))
```

Finally, lets update the `user` namespace

```clj
(ns user
  (:require [integrant.repl :as ig-repl :refer [set-prep! go halt reset]]
            [integrant.core :as ig]
            [novus.server :refer [config]]))

;; Step 1:
(ig-repl/set-prep!
  (fn []
    (let [config (-> "config/dev.edn" slurp ig/read-string)]
      (ig/load-namespaces config)
      config)))

;; Step 2: Start, stop and restart the system
(comment
  (go)   ;; starts the system
  (halt) ;; Halts the system
  (reset)) ;; resets the system + reloads all namespace

```

Notice that we are now reading the config from `config/dev.edn`
