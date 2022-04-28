## Goal: Create a basic clojure ring server + `deps.edn` project

### Step 1: Create a new `deps.edn` project

First, we are going to create a new project called `novus` by creating a new directory. This is where the application code will live
```
mkdir novus
```

### Step 2: Configure `deps.edn` file

Next let's cd into the directory and add a `deps.edn` file. Inside we will specify the source files path, declare all the dependencies and define custom aliases

```
{:paths ["src/main"]
 :deps    {org.clojure/clojure {:mvn/version "1.10.3"}
           ring/ring           {:mvn/version "1.9.4"}}
 :aliases {:dev {:extra-paths ["src/dev"]}
           :nrepl {:jvm-opts ["-Dclojure.server.repl={:port 7777 :accept clojure.core.server/repl}"]}}}

```
#### `:paths` 
  - this property specifies the location path of our source code i.e inside `src/main` directory. 
  - By default, the clj tool will look for source files in the `src` directory. 
  - You must specify the paths manually to override the default behaviour.

#### `:deps` 

  - this is where we specfiy our dependencies. currenly we have two - clojure and ring.

#### `:aliases`

  - custom aliases. currently we have two: `dev` and `nrepl`
  - `dev` - we have specified extra-path during development. this is where user.clj file will live
  - `nrepl` - starts nrepl (network repl) on port `7777`


Next we will create the following directories
- src
- src/main
- src/dev
```
mkidr src
mkdir src/main
mkdir src/dev
```

### Step 3: Add user.clj

`user.clj` is a very special file. This is where all the magic happens.

Lets add it inside Inside `src/dev` directory.

```
touch src/dev/user.clj
```

Next lets declate the user `namespace`. 

Q: What is a `namespace?`
"Namespaces provide a means to organize our code and the names we use in our code. Specifically, they let us give new unambiguous names to functions or other values. These full names are naturally long because they include context. Thus namespaces also provide a means to unambiguously reference the names of other functions and values but using names that are shorter and easier to type.

A namespace is both a name context and a container for vars. Namespace names are symbols where periods are used to separate namespace parts, such as clojure.string. By convention, namespace names are typically lower-case and use - to separate words, although this is not required." - [link](https://clojure.org/guides/learn/namespaces)


#### Creating a new namespace with `ns` macro

TODO: Read this blog - extreact ideas + points: https://justabloginthepark.com/2017/06/18/clojure-and-the-esoteric-mysteries-of-namespaces/


- The best way to set up a new namespace at the top of a Clojure source file is to use the `ns` macro. 
- By default this will create a new namespace that contains mappings for:
   - the classnames in java.lang plus 
   - clojure.lang.Compiler, and 
   - the functions in clojure.core.

Basic Usage
```clj
(ns name docstring? references*)
```
- `name` is the name of new namespace
- `docstring?` Optional documentation
- `references*`: Zero or more references (more on references later)

Lets create a `user` namespace by simply passing the `name` property

```clj
(ns user)
```
This will create a new namespace called `user`. Meaning our `user` namespace has access to 
1. classnames in `java.lang` and `clojure.lang.Compiler` and most importantly
3. all the functions in `clojure.core` i.e #{map ->> juxt ...}

### Step 4: Start the nREPL (network REPL)

Now that we have our `user` namespace, its time to fire up our superpower - nREPL

Next lets fire up the repl. Enter the following into the console
```
clj -A:dev:nrepl
```

This will start a networked REPL on port `7777`. Now that our network REPL is running on port `7777`, this means that we are ready connect our IDE to the running REPL. This way we can incrementally build application by testing simple functions in the REPL -> usually under a rich comment block. And then later we can run them into `tests`.

Note: I am using chlorine + Atom to connect to REPL. You can use whatever IDE + nREPL. If you code in VSCode or Sublime and want to leverage other solutions, there are plenty of tutorials online to get started on this topic.


### Step 5: Create `root` namespace directory and demo file

Let's create our root namespace directory called `novus` inside `main`.

Next let's add a new file called `ring_basic.clj`. This is where we will write our server logic

```
mkdir src/main/novus
touch src/main/novus/ring_basic.clj
```
Open `ring_basic.clj` file and add the namespace

```clj
(ns novus.ring-basic)

```

Now that we have our namespace, we are ready to update it with code to create a web server and handle HTTP requests.


### Step 6: Define handler function

- Ring uses standard Clojure maps to represent requests and responses.
- The handler is a function that processes incoming requests and generates corresponding responses. A very basic Ring handler might look like this:

```clj
(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str ​"<html><body> your IP is: "
              (:remote-addr request)
              "</body></html>")})
```

As you can see, the handler function accepts a map representing an HTTP request and returns a map representing an HTTP response. Ring takes care of generating the request map from the incoming HTTP request, and converting the map returned by the function into the corresponding HTTP response that will be sent to the client. Let’s open the ring-app.core and add the handler there.

Now we need to start a web server with our handler attached. We can use Ring’s jetty adapter to configure and start an instance of jetty. To do so, We’ll require it in the namespace declaration and then call `run-jetty` function from our -main function. Our namespace should look like this:

```
(ns novus.ring-basic
  (:require [ring.adapter.jetty :as jetty]))

(defn handler [request]
  {:status 200
   :headers {"Content-Type" "text/html"}
   :body (str ​"<html><body> your IP is: "
              (:remote-addr request)
              "</body></html>")})

(defn main []
 (​jetty/run-jetty handler {:port 3000 :join? false}))

```
The `run-jetty` function accepts the handler function we just created, along with a map containing options such as the HTTP port. The :join? key indicates whether the server thread should block. Let’s set it to false so that we’re able to work in the REPL while it’s running.


Now, we can open up the terminal and start our application using the `clj -A:server` command.

```
clj -A:server
```

At this point our server is ready to handle requests, and we can navigate to http://localhost:3000 or use curl to see our app in action. You should see “your IP is: 0:0:0:0:0:0:0:1” since we’re accessing it from localhost and the server is listening on all of the available interfaces.

The handler that we wrote serves an HTML string with the client’s IP address with a response status of 200. Since this is a common operation, the Ring API provides a helper function for generating such responses found in the `ring.util.response` namespace. Let’s reference it and update our handler as follows.

```clj
(ns novus.ring-basic
  (:require [ring.adapter.jetty :as jetty]
            [ring.util.response :as response]))

(defn handler [request]
  (response/response
   (str ​"<html><body> your IP is: "
        (:remote-addr request)
        "</body></html>")))

(defn main []
 (​jetty/run-jetty handler {:port 3000 :join? false}))
```

We should now be able to restart the app in the terminal and see the same page displayed as before. If you want to create a custom response, you’ll have to write a function that would accept a request map and return a response map representing your custom response. Let’s look at the format for the request and response maps.
