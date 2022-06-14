(ns novus.student.routes
  (:require [novus.student.handlers :as student]))

(def routes
  ["/students"
   [""
    {:get {:handler student/browse
           :responses {200 {:body {:students vector?}}}
           :summary "Fetch list of students"}}]

   ["/:studentId"
    {:get {:handler student/fetch
           :responses {201 {:body {:student map?}}}
           :parameters {:path {:studentId uuid?}}
           :summary "Fetch list of students"}}]])
