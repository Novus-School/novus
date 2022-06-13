# Integrant

## Introduction

Integrant is a Clojure (and ClojureScript) micro-framework for building applications with data-driven architecture. It can be thought of as an alternative to Component or Mount, and was inspired by Arachne and through work on Duct.

## Rationale

- Integrant was built as a reaction to fix some perceived weaknesses with Component.

- In Component, systems are created programmatically. Constructor functions are used to build records, which are then assembled into systems.

- In Integrant, systems are created from a configuration data structure, typically loaded from an edn resource. The architecture of the application is defined through data, rather than code.

- In Component, only records or maps may have dependencies. Anything else you might want to have dependencies, like a function, needs to be wrapped in a record.

- In Integrant, anything can be dependent on anything else. The dependencies are resolved from the configuration before it's initialized into a system.


## Installation

To use the latest release, add the following to your `deps.edn` in the `deps` section

```clojure
{:paths ["src/main"]
 :deps    {org.clojure/clojure {:mvn/version "1.10.3"}
           ring/ring           {:mvn/version "1.9.4"}
           ;; Add this
           integrant/integrant {:mvn/version "0.8.0"}}
 :aliases {:dev {:extra-paths ["src/dev"]
                 :extra-deps {nrepl/nrepl {:mvn/version "0.6.0"}}
                 :main-opts ["-m" "nrepl.cmdline"]}}}

```

## Usage

### Step 1: Configuration

Integrant starts with a configuration map. Each top-level key in the map represents a configuration that can be "initialized" into a concrete implementation. Configurations can reference other keys via the ref (or refset) function.

Lets require it in

```clojure
(:require '[integrant.core :as ig])

(def config
  {:adapter/jetty {:port 8080, :handler (ig/ref :handler/greet)}
   :handler/greet {:name "Novus"}})
```

Alternatively, you can specify your configuration as pure edn:

```clojure
{:adapter/jetty {:port 8080, :handler #ig/ref :handler/greet}
 :handler/greet {:name "Novus"}}
```

And load it with Integrant's version of `read-string`:

```clojure
(def config
  (ig/read-string (slurp "config.edn")))
```

### Step 2: Initialization and Halting


#### init-key

Once you have a configuration, Integrant needs to be told how to
implement it. The `init-key` multimethod takes two arguments, a key
and its corresponding value, and tells Integrant how to initialize it:

```clojure
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

(defmethod ig/init-key :handler/greet [_ {:keys [name]}]
  (defn handler [request]
    (response/response (str "Hello, " name))))
```

Keys are initialized recursively, with the values in the map being
replaced by the return value from `init-key`.

In the configuration we defined before, `:handler/greet` will be
initialized first, and its value replaced with a handler function.
When `:adapter/jetty` references `:handler/greet`, it will receive the
initialized handler function, rather than the raw configuration.

#### halt-key

The `halt-key!` multimethod tells Integrant how to stop and clean up
after a key. Like `init-key`, it takes two arguments, a key and its
corresponding initialized value.

```clojure
(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))
```

Note that we don't need to define a `halt-key!` for `:handler/greet`.

### Step 3: Start/Stop the system

Once the multimethods have been defined, we can use the `init` and
`halt!` functions to handle entire configurations. The `init` function
will start keys in dependency order, and resolve references as it
goes:

```clojure
(def system
  (ig/init config))
```

When a system needs to be shut down, `halt!` is used:

```clojure
(ig/halt! system)
```

Like Component, `halt!` shuts down the system in reverse dependency
order. Unlike Component, `halt!` is entirely side-effectful. The
return value should be ignored, and the system structure discarded.

It's also important that `halt-key!` is **idempotent**. We should be
able to run it multiple times on the same key without issue.


By the end of this tutorial, `server.clj` should look like this

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
    (response/response (str "Hello, " name))))

;; Step 3: Start/Stop the Integrant system
(comment
  (def system (ig/init config))
  (ig/halt! system))


```

## Next Chapter: Reloaded workflow

See [Integrant-REPL](https://github.com/weavejester/integrant-repl) to
use Integrant systems at the REPL, in line with Stuart Sierra's [reloaded
workflow](http://thinkrelevance.com/blog/2013/06/04/clojure-workflow-reloaded).

## Further Documentation

* [API docs](https://weavejester.github.io/integrant/integrant.core.html)
