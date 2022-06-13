So far all our application is doing is displying this piece of string to the UI

```
Hello Immutable Stack
```

Technically

This is great that it's working, but Real World is complex and usually has complex and various resource requirements, this means having multiple routes, where each route serve a specific resource. For example soon, we will have routes for students, courses etc

Reitit is a general purpose routing library that can be used to provide routing on top of Ring as well as for client-side routing, as we’ll see later on. It provides a way to associate handler functions with a URL and an HTTP method.

Let’s take a closer look at the functionality it provides.

## Step 1: Installation
First, add it as a dependency.

```clj
{:paths ["src/main"]
 :deps    {org.clojure/clojure {:mvn/version "1.10.3"}
           ring/ring           {:mvn/version "1.9.4"}
           ;; Dependency Management
           integrant/integrant {:mvn/version "0.8.0"}
           ;; Routing
           metosin/reitit {:mvn/version "0.5.15"}}
 :aliases {:dev {:extra-paths ["src/dev"]
                 :extra-deps {nrepl/nrepl {:mvn/version "0.6.0"}
                              integrant/repl {:mvn/version "0.3.2"}}
                 :main-opts ["-m" "nrepl.cmdline"]}}}


```

With the dependency in place, let’s update the namespace to reference `reitit.ring` and add a route for the / URI.

## Step 2: Create `router.clj`

```clj
(ns novus.router
 (:require
   [reitit.ring :as reitit]
   [muuntaja.middleware :as muuntaja]
   [ring.adapter.jetty :as jetty]
   [ring.util.response :as response]
   [ring.middleware.reload :refer [wrap-reload]]))

(defn json-handler [req]
  (response/response (str "Hello Immutable Stack")))

(defn routes
  [env]
  (reitit/ring-handler
    (reitit/router
     [["/" {:get json-handler}]])))



```

- We’re using our `json-handler` to generate responses, and it’s being called when the / route is called with the HTTP GET method

- As you can see, Reitit uses plain Clojure data structures to declare routes. Each route is declared using a vector where the first item is a string representing the route path. The path is followed by a map describing the supported operations.

In this case, the map indicates that the route responds to the :get operation and the request will be handled by the json-handler function.


Now that our `routes` function defined, its time to use it.
Next, we are going to modify `server.clj`

```clj
(ns novus.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]
            [integrant.core :as ig]
            [novus.router :as router]))


;; Step 2: Define Initialization and Halting multimethods
(defmethod ig/init-key :adapter/jetty [_ {:keys [handler port]}]
  (jetty/run-jetty handler {:join? false
                            :port port}))

(defmethod ig/halt-key! :adapter/jetty [_ server]
  (.stop server))

(defmethod ig/init-key :handler/greet [_ config]
  (router/routes config))
```

As you can see, we have `:handler/greet` method. We have replaced
the handler function with `(router/routes config)`

We can test that our routing works correctly by restarting the application using REPL and using browser to send a request to it:

Currently, the route only responds to GET requests; if we try to send it a POST request, then we won’t get a response:

As you might have guessed, all we have to do to handle the POST method is to add the :post key in the route map:

```clj
(defn routes
  [env]
  (reitit/ring-handler
    (reitit/router
     [["/" {:get json-handler
            :post json-handler}]])))

```

### Dynamic Paths

Reitit also supports dynamic paths with embedded parameters. These parameters are specified using a :.

Let’s add another route /courses/:id to our handler that accepts an :id and displays its value on the page.

```
(defn routes
  [env]
  (reitit/ring-handler
    (reitit/router
     [["/" {:get json-handler
            :post json-handler}]
      ["/courses/:id"
       {:get (fn [{{:keys [id]} :path-params}]
               (response/response (str "Course ID:" id)))}]])))

```

Reloading the app and going to this link `http://localhost:8080/courses/learning-reframe` is going to print `Course ID:learning-reframe` on the UI


### Middlewares

Another useful Reitit feature is the ability to selectively apply middleware for specific routes.
