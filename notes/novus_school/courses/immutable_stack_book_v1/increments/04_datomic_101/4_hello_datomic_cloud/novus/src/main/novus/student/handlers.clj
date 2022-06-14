(ns novus.student.handlers
  (:require [ring.util.response :as rr]))


(defn browse
  [req]
  (rr/response
   {:students [[{:db/id 87960930222227,
                 :student/id #uuid "0515a5fa-f177-44f0-8144-d6bdcc403564",
                 :student/first-name "Lynn",
                 :student/last-name "Margulis"}]
               [{:db/id 87960930222228,
                 :student/id #uuid "1c1bae77-13fa-4cd1-b595-6c86fdd55946",
                 :student/first-name "Galileo",
                 :student/last-name "Galilei"}]]}))
