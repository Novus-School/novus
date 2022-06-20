(ns novus.account.handlers
  (:require [novus.auth0 :as auth0]
            [clj-http.client :as http]
            [muuntaja.core :as m]
            [ring.util.response :as rr]))
            ; [datomic.client.api :as d]))

(defonce req-atom (atom nil))

(comment
  @req-atom)

(defn create-account!
  [{:keys [env claims] :as _request}]
  (reset! req-atom _request)
  (let [{:keys [sub name picture]} claims]))

    ;; FIXME: transact-account


(comment
  (auth0/get-management-token {:client-secret "LvDQVdJJ7ksFbCQIy692O2Y6dBAlBqRsq3O3_p53iD9WRGkamHQ6ZOzOZL9BYiOp"}))

(defn update-account!
  [{:keys [env claims] :as _request}]
  (let [uid (:sub claims)
        client-secret (-> env :auth0)
        token (auth0/get-management-token client-secret)]
    (->> {:headers {"Authorization" (str "Bearer " token)}
          :cookie-policy :standard
          :content-type :json
          :throw-exceptions false
          :body (m/encode "application/json"
                  {:roles [(auth0/get-role-id token)]})}
      (http/post (str "https://learn-reitit-playground.eu.auth0.com/api/v2/users/" uid "/roles")))))


(defn delete-account!
  [{:keys [env claims] :as _request}]
  (let [account-id (:sub claims)
        client-secret (-> env :auth0)
        delete-auth0-account! (http/delete
                                (str "https://learn-reitit-playground.eu.auth0.com/api/v2/users/" account-id)
                                {:headers {"Authorization" (str "Bearer " (auth0/get-management-token client-secret))}})]
    (when (= (:status delete-auth0-account!) 204))))
      ;; FIXME: retract-account
