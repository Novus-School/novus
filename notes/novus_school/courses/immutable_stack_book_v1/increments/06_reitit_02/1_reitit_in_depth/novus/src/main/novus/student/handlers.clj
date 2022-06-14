(ns novus.student.handlers
  (:require [ring.util.response :as rr]
            [datomic.client.api :as d]))

(defn browse
  [{{{:keys [conn]} :datomic} :env
    :as req}]
  (rr/response {:students (d/q '[:find (pull ?student [*])
                                 :where
                                 [?student :student/id]]
                             (d/db conn))}))

(defn fetch
  [{{{:keys [conn]} :datomic} :env
    {{:keys [studentId]} :path} :parameters
    :as req}]
  (rr/response {:student (ffirst (d/q '[:find (pull ?student [*])
                                        :in $ ?sid
                                        :where
                                        [?student :student/id ?sid]]
                                    (d/db conn)
                                    studentId))}))
