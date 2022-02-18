# Learn Datomic Cloud - Notes


Notes on Datomic Cloud. Including Datomic dev-local, Datomic Ions, and AWS deployment.

## Course files

The code in this repo includes two folders - `increments` - code for the start of each video (if you get lost somewhere along the way just copy the content of the video you are starting and continue). `cheffy` this is the start of the project / course. It's the same code as in `increments/06-start`

### Clone

```shell
$ git clone https://github.com/learnuidev/learn-datomic-notes.git

$ cd learn-datomic-notes/cheffy
```

### Run Socket REPL + Dev + Test

Probably you will run your REPL from your editor, and thre is nothing stopping you to run it from the command line:

```shell
clj -A:socket-repl:dev:test
```

This will start socket REPL in port 50505. If you wish to run REPL from the editor, you will need to use this PORT number

Once connected to repl. You can go to `src/dev/user.clj` and start and stop the service programatically
```clj
(ns user
  (:require [integrant.repl :as ig-repl]
            [integrant.core :as ig]
            [integrant.repl.state :as state]
            [cheffy.server]))

(ig-repl/set-prep!
  (fn [] (-> "src/dev/resources/config.edn" slurp ig/read-string)))

(def start-dev ig-repl/go)
(def stop-dev ig-repl/halt)
(def restart-dev ig-repl/reset)
(def reset-all ig-repl/reset-all)

(def app (-> state/system :cheffy/app))
(def db (-> state/system :db/postgres))

(comment
  (start-dev)
  (stop-dev))

```

### Run the app
Probably you will run your REPL from your editor, and thre is nothing stopping you to run it from the command line:

```shell
clj -M:dev src/main/cheffy/server.clj
```
