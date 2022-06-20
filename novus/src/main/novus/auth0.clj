(ns novus.auth0
  (:require [clj-http.client :as http]
            [muuntaja.core :as m]))

(defn get-management-token
  [auth0]
  (->> {:throw-exceptions false
        :content-type :json
        :cookie-policy :standard
        :body (m/encode "application/json"
                {:client_id "jfHBbz2W2JVsLU9pylOM4QcZLa6fhaCx"
                 :client_secret (:client-secret auth0)
                 :audience "https://leanuidev.us.auth0.com/api/v2/"
                 :grant_type "client_credentials"})}
    (http/post "https://leanuidev.us.auth0.com/oauth/token")
    (m/decode-response-body)
    :access_token))


(comment
    (get-management-token {:client-secret "LvDQVdJJ7ksFbCQIy692O2Y6dBAlBqRsq3O3_p53iD9WRGkamHQ6ZOzOZL9BYiOp"}))


(defn get-role-id
  [token]
  (->> {:headers {"Authorization" (str "Bearer " token)}
        :throw-exceptions false
        :content-type :json
        :cookie-policy :standard}
    (http/get "https://leanuidev.us.auth0.com/api/v2/roles")
    (m/decode-response-body)
    (filter (fn [role] (= (:name role) "manage-recipes")))
    (first)
    :id))
