(ns novus.student.handlers
  (:require [novus.auth0 :as auth0]
            [clj-http.client :as http]
            [muuntaja.core :as m]
            [datomic.client.api :as d]
            [ring.util.response :as rr]))
            ; [datomic.client.api :as d]))

(defonce req-atom (atom nil))
(comment
  @req-atom)


(defn browse
  [{{{:keys [conn]} :datomic} :env
    :as req}]
  (reset! req-atom req)
  #_(rr/response {:students (d/q '[:find (pull ?student [*])
                                   :where
                                   [?student :student/id]]
                               (d/db conn))})
  (rr/response {:students [[{:db/id 87960930222227,
                             :student/id #uuid "0515a5fa-f177-44f0-8144-d6bdcc403564",
                             :student/first-name "Lynn",
                             :student/last-name "Margulis"}]
                           [{:db/id 87960930222228,
                             :student/id #uuid "1c1bae77-13fa-4cd1-b595-6c86fdd55946",
                             :student/first-name "Galileo",
                             :student/last-name "Galilei"}]]}))

(comment
  (browse @req-atom))
