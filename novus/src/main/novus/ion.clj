(ns novus.ion
 (:require [integrant.core :as ig]
           [novus.components.auth0 :as auth0]
           [novus.components.datomic-cloud :as datomic-cloud]
           [datomic.ion :as ion]
           [novus.server :as server]))

(def integrant-setup
  {::server/app {:datomic (ig/ref ::datomic-cloud/db)
                 :auth0 (ig/ref ::auth0/auth)}
   ::auth0/auth {:client-secret (get (ion/get-params {:path "/datomic-shared/prod/novus/"}) "auth0-client-secret")
                 :client-id (get (ion/get-params {:path "/datomic-shared/prod/novus/"}) "auth0-client-id")}
   ::auth0/auth {:client-id "auth0-client-id"
                 :client-secret "auth0-client-secret"}
   ::datomic-cloud/db {:server-type :ion
                       :region "us-east-1"
                       :system "app-prod"
                       :db-name "app-prod"
                       :endpoint "https://3bgnoq0ny3.execute-api.us-east-1.amazonaws.com"}})

(def app
  (delay
    (-> integrant-setup ig/prep ig/init ::server/app)))

(defn handler
  [req]
  (@app req))
