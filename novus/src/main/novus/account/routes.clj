(ns novus.account.routes
  (:require [novus.account.handlers :as account]
            [novus.middleware :as mw]))

(def routes
  ["/account" {:swagger {:tags ["account"]}
               :middleware [[mw/wrap-auth0]]}
   [""
    {:post {:handler account/create-account!
            :responses {201 {:body nil?}}
            :summary "Create account"}
     :put {:handler account/update-account!
           :responses {204 {:body nil?}}
           :summary "Update user role to cook"}
     :delete {:handler account/delete-account!
              :responses {204 {:body nil?}}
              :summary "Delete account"}}]])
