# Ring

Ring is a Clojure web applications library inspired by Python's WSGI and Ruby's Rack. By abstracting the details of HTTP into a simple, unified API, Ring allows web applications to be constructed of modular components that can be shared among a variety of applications, web servers, and web frameworks.

## Goal:
- Understanding Ring
- Installation
- Create a basic ring server

### LOC 1 - Understanding Ring

## Ring Spec (1.4)

Ring is defined in terms of:
  - *handlers*,
  - *middleware*
  - *adapters*,
  - *requests maps*, and
  - *response maps*, each of which are described below.

### 1. Handlers
- Ring handlers constitute the core logic of the web application.
- Handlers are implemented as Clojure functions.

There are two kind of handlers

#### Sync vs Async Handlers

A synchronous handler takes 1 argument, a request map, and returns a response
map.

An asynchronous handler takes 3 arguments: a request map, a callback function
for sending a response and a callback function for raising an exception. The
response callback takes a response map as its argument. The exception callback
takes an exception as its argument.

A handler function may simultaneously support synchronous and asynchronous
behaviour by accepting both arities.


### 2. Middleware
Ring middleware augments the functionality of handlers by invoking them in the
process of generating responses. Typically middleware will be implemented as a
higher-order function that takes one or more handlers and configuration options
as arguments and returns a new handler with the desired compound behaviour.


### 3. Adapters
Handlers are run via Ring adapters, which are in turn responsible for
implementing the HTTP protocol and abstracting the handlers that they run from
the details of the protocol.

Adapters are implemented as functions of two arguments: a handler and an options
map. The options map provides any needed configuration to the adapter, such as
the port on which to run.

Once initialized, adapters receive HTTP requests, parse them to construct a
request map, and then invoke their handler with this request map as an
argument. Once the handler returns a response map, the adapter uses it to
construct and send an HTTP response to the client.


### 5. Request Map
A request map is a Clojure map containing at least the following keys and
corresponding values:

:server-port
  (Required, Integer)
  The port on which the request is being handled.

:server-name
  (Required, String)
  The resolved server name, or the server IP address.

:remote-addr
  (Required, String)
  The IP address of the client or the last proxy that sent the request.

:uri
  (Required, String)
  The request URI, excluding the query string and the "?" separator.
  Must start with "/".

:query-string
  (Optional, String)
  The query string, if present.

:scheme
  (Required, clojure.lang.Keyword)
  The transport protocol, must be one of :http or :https.

:request-method
  (Required, clojure.lang.Keyword)
  The HTTP request method, must be a lowercase keyword corresponding to a HTTP
  request method, such as :get or :post.

:protocol
  (Required, String)
  The protocol the request was made with, e.g. "HTTP/1.1".

:content-type [DEPRECATED]
  (Optional, String)
  The MIME type of the request body, if known.

:content-length [DEPRECATED]
  (Optional, Integer)
  The number of bytes in the request body, if known.

:character-encoding [DEPRECATED]
  (Optional, String)
  The name of the character encoding used in the request body, if known.

:ssl-client-cert
  (Optional, java.security.cert.X509Certificate)
  The SSL client certificate, if supplied.

:headers
  (Required, clojure.lang.IPersistentMap)
  A Clojure map of downcased header name Strings to corresponding header value
  Strings. When there are multiple headers with the same name, the header
  values are concatenated together, separated by the "," character.

:body
  (Optional, java.io.InputStream)
  An InputStream for the request body, if present.


## 5. Response Map
A response map is a Clojure map containing at least the following keys and
corresponding values:

:status
  (Required, Integer)
  The HTTP status code, must be greater than or equal to 100.

:headers
  (Required, clojure.lang.IPersistentMap)
  A Clojure map of HTTP header names to header values. These values may be
  either Strings, in which case one name/value header will be sent in the
  HTTP response, or a seq of Strings, in which case a name/value header will be
  sent for each such String value.

:body
  (Optional, ring.core.protocols/StreamableResponseBody)
  A representation of the response body, if a response body is appropriate for
  the response's status code. The response body must satisfy the
  StreamableResponseBody protocol located in the ring.core.protocols namespace.

  By default the protocol is satisfied by the following types:

  String:
    Contents are sent to the client as-is.
  ISeq:
    Each element of the seq is sent to the client as a string.
  File:
    Contents at the specified location are sent to the client. The server may
    use an optimized method to send the file if such a method is available.
  InputStream:
    Contents are consumed from the stream and sent to the client. When the
    stream is exhausted, it is .close'd.


### LOC 2 - Installation


### LOC 3 - Create a basic ring server
