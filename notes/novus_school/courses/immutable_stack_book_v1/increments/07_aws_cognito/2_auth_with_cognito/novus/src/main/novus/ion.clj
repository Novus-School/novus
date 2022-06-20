(ns novus.ion
 (:require [integrant.core :as ig]
           [novus.components.datomic-cloud :as datomic-cloud]
           [datomic.ion :as ion]
           [novus.server :as server]))

(def integrant-setup
  {::server/app {:datomic (ig/ref ::datomic-cloud/db)}
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
