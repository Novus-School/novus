(ns novus.student.routes
  (:require [novus.student.handlers :as student]))

(def routes
  ["/students"
   [""
    {:get {:handler student/browse
           :responses {201 {:body nil?}}
           :summary "Fetch list of students"}}]])
