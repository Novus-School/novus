## Goal: Better REPL Driven Development

Currently our `deps.edn` looks like this

```
{:paths ["src/main"]
 :deps    {org.clojure/clojure {:mvn/version "1.10.3"}
           ring/ring           {:mvn/version "1.9.4"}}
 :aliases {:server {:extra-paths ["src/resources" "src"]
                    :main-opts ["-m" "novus.server"]}
           :dev {:extra-paths ["src/dev"]}
           :repl {:jvm-opts ["-Dclojure.server.repl={:port 7777 :accept clojure.core.server/repl}"]}}}

```

and if we run `clj -A:repl:dev`, it starts nrepl at port 7777. This is code smell since 7777 is hard coded. Can we improve our solution? Yes we can.

```
{:paths ["src/main"]
 :deps    {org.clojure/clojure {:mvn/version "1.10.3"}
           ring/ring           {:mvn/version "1.9.4"}}
 :aliases {:dev {:extra-paths ["src/dev"]}
           :repl {:extra-deps {nrepl/nrepl {:mvn/version "0.6.0"}}
                  :main-opts ["-m" "nrepl.cmdline"]}}}
```

We have have replaced `:jvm-opts` with `:extra-deps` and `:main-opts`. Now we call this alias using the `-M` command, like so:
```
> clj -M:repl:dev

2022-03-06 18:28:05.113:INFO::main: Logging initialized @1046ms to org.eclipse.jetty.util.log.StdErrLog
nREPL server started on port 53323 on host localhost - nrepl://localhost:53323
```

As you can see the command `clj -M:nrepl` ran the `-main` function from `nrepl.cmdline` namespace. We need to pass `:dev` alias as well, other wise REPL wont be aware of the `user` class path and invoking `user` namespace will throw error.

This can be further improved by coming nrepl into dev. So `clj -M:nrepl:dev` can be shorted to `clj -M:dev` by changing the config like so

```clj
{:paths ["src/main"]
 :deps    {org.clojure/clojure {:mvn/version "1.10.3"}
           ring/ring           {:mvn/version "1.9.4"}}
 :aliases {:server {:extra-paths ["src/resources" "src"]
                    :main-opts ["-m" "novus.server"]}
           :dev {:extra-paths ["src/dev"]
                 :extra-deps {nrepl/nrepl {:mvn/version "0.6.0"}}
                 :main-opts ["-m" "nrepl.cmdline"]}}}

```

Now stop the REPL and try typing this `clj -M:dev`

It should start a nREPL server on random port

## Aside: Difference between clj -A, -M and -X commands

### clj -A
Use -A to run tests or start socket nREPL using :jvm-opts.

This example is from #grok
Running `clj -A:dev:socket` on the terminal will start a socket repl server on port 50505 and load all the files from `src/dev`

### clj -M
Use clj -M to run any :main-opts.
Example:
I am running `clj -M:nrepl:dev` which ran two aliases
- :dev - which added "src/dev" to extra paths
- :nrepl - which invoked the -main function from "nrepl.cmdline" namespace  - starting nREPL on port 50815

### clj -X
Use -X to run a specific function. You can pass key/value pairs via the cli , in addition to the :exec-args in deps.edn.
In our example running "clj -X:repl '{:port 9999}'" invokes clojure.core.server/start-server function, starting a repl server on port 9999


Note: for atom users with chlorine plugin
If you use atom text editor and use chlorine for connecting to socket nREPL, then head to Settings -> Packages. In Packages, search for "Chlorine", click on the Settings button and make sure that "Auto detect nREPL port when connecting to socket" is ticked.

If it is not ticked, then tick and reopen atom. Now if you run nrepl (clj -M:nrepl:dev -p 30303) and try connecting to socket repl (command + option + y), then the port 30303 should be populated


## Bonus: Dynamic Development

Currently if we start the server, there is no way to programitacally stop it. Lets change that

```clj
(ns novus.server
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]))


(defn handler [request]
  (response/response "Hello Immutable Stack"))

(defn -main []
  (jetty/run-jetty handler {:port 3000 :join? false}))

(comment
  ;; Save the server in server var
  (def server (-main))
  (identity server)
  ;; Call the stop method to stop the server
  (.stop server))


```

Now we can programmatically start and stop the server, all without having to restart the server. We will definitely improve our

Now that you must have realized that programming in Clojure is unlike you've ever experienced before. You have already learned so much. Great work! Before we finish of the lesson lets also remove the `server` alias since we wont be using it anymore b/c we will use REPL to start during development time.

When it comes to deployment, we will create a separate alias called `:prod`, more on that later. After deleting `:server` alias, your `deps.edn` should look like this

```clj
{:paths ["src/main"]
 :deps    {org.clojure/clojure {:mvn/version "1.10.3"}
           ring/ring           {:mvn/version "1.9.4"}}
 :aliases {:dev {:extra-paths ["src/dev"]
                 :extra-deps {nrepl/nrepl {:mvn/version "0.6.0"}}
                 :main-opts ["-m" "nrepl.cmdline"]}}}

```


## Summary
In this tutorial we learned the following:

1. We improved our nrepl command script by making it more flexible. We switch from using
2. We learned the difference between clj -A, clj -M and clj -X commands
3. We learned that -A is mainly used for starting REPL or running tests
4. We learned that -M allows you to run any :main-opts
5. We learned that -X allows you to run a specific function and pass key/value pairs (in string) from the cli, potentially overriding :exec-args in deps.edn
6. We learned how to programmatically start and stop the server
7. We Learned that developing programs in Clojure is a interactive and joyful experience


Next: Chapter 3: Integrant 101
