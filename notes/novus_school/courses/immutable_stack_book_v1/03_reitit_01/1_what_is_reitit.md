## Routing with Reitit

### What is Reitit?

### Installation

- Adding `metosin/reitit` into our application

```clojure
{metosin/reitit {:mvn/version "0.5.16"}}
```

### Usage

#### Step 1. Creating router via `reitit.ring/router`

This function creates a `[[reitit.core/Router]]` from raw route data (vector) and optionally an options map with support for http-methods and Middleware.

```clojure
(def router (reitit.ring/router
             ["api" {:middleware [wrap-format wrap-oauth2]}
               ["users" {:get get-user
                         :post update-user
                         :delete {:middleware [wrap-delete]
                                  :handler delete-user}}]]))

```

#### Step 2. Create handler via `reitit.ring/handler`

Creates a ring-handler out of a `router`, optional default ring-handler
and options map, with the following keys:
```clojure
(def ring-handler (reitit.ring/handler router))
```
