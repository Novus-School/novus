(ns novus.student.routes
  (:require [novus.student.handlers :as student]
            [novus.middleware :as mw]))

(def routes
  ["/students" {:swagger {:tags ["Student"]}
                :middleware [[mw/wrap-auth0]]}
   [""
    {:get {:handler student/browse
           :responses {201 {:body nil?}}
           :summary "Fetch list of students"}}]])
