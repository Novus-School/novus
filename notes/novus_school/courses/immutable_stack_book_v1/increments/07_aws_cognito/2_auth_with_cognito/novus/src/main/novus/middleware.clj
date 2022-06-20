(ns novus.middleware
  (:require [ring.util.response :as rr]
            [clojure.string :as str]
            [muuntaja.core :as m])
  (:import java.util.Base64))

(def wrap-env
 {:name ::env
  :description "Middleware for injecting env into request"
  ;; runs once - imporant for performance reasons
  :compile (fn [{:keys [env]} route-options]
             (fn [handler]
               (fn [request]
                 (handler (assoc request :env env)))))})


(defn decode-jwt [jwt]
  (let [[_ payload _] (str/split jwt #"\.")]
    (when payload
      (String. (.decode (Base64/getDecoder) ^String payload)))))

(def token-auth-mw
  {:name ::token-auth
   :summary "Inject a map containing `:username` and `:email` into the key `:identity` on the request.
             The application uses AWS Cognito for request authorization in front of the application.
             By the time we are at application router we are confident we have a valid token. This simply
             decodes the token and injects the user identity into the request."
   :wrap (fn [handler]
           (fn [request]
             (let [jwt (-> request :headers (get "authorization"))
                   decoded-token (when jwt
                                  (->> jwt decode-jwt (m/decode "application/json")))]
               (handler (assoc request :identity {:username (:cognito:username decoded-token)
                                                  :email (:email decoded-token)})))))})
