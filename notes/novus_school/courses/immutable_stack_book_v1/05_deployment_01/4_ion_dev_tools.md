# Ion Dev Tools

The Datomic ion-dev tools should be installed via an alias in your user `deps.edn` file,
which is normally located at `$HOME/.clojure/deps.edn`.

To install the tools, add the datomic-cloud maven repo under your :mvn/repos key:

```
"datomic-cloud" {:url "s3://datomic-releases-1fc2183a/maven/releases"}
```


Then add an :ion-dev entry under your :aliases key with the latest version of ion-dev:

```
:ion-dev
{:deps {com.datomic/ion-dev {:mvn/version "1.0.306"}}
 :main-opts ["-m" "datomic.ion.dev"]}
```

By the end your deps.edn should look something like this

```clj
{:mvn/repos {"datomic-cloud"      {:url "s3://datomic-releases-1fc2183a/maven/releases"}
             "cognitect-dev-tools" {:url "https://dev-tools.cognitect.com/maven/releases/"}}
 :aliases
 {:ion-dev
  {:deps {com.datomic/ion-dev {:mvn/version "1.0.306"}}
   :main-opts ["-m" "datomic.ion.dev"]}}}

```

Now that we have added ion-dev as an `:ion-dev` we can run it like so from anywhere

```
clj -A:ion-dev
```

This will throw an error

```
WARNING: Use of :main-opts with -A is deprecated. Use -M instead.
Execution error (ArityException) at clojure.main/main (main.java:40).
Wrong number of args (0) passed to: datomic.ion.dev/-main

Full report at:
/var/folders/m0/gj5rg98x1z10h6h6dk_zrh880000gn/T/clojure-3652147230109631627.edn

```

this is great because now our `ion-dev` is working.

This is the final piece of puzzle. Now we can go and deploy our application
