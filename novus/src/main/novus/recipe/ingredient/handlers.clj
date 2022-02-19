(ns novus.recipe.ingredient.handlers
  (:require [novus.recipe.db :as recipe-db]
            [novus.responses :as responses]
            [ring.util.response :as rr])
  (:import (java.util UUID)))


(defn create!
  [{:keys [env parameters] :as _request}]
  (let [recipe-id (-> parameters :path :recipe-id)
        ingredient (:body parameters)
        ingredient-id (str (UUID/randomUUID))]))
    ;; FIXME: recipe-db/transact-ingredient


(defn update!
  [{:keys [env parameters] :as _request}]
  (let [ingredient (:body parameters)
        recipe-id (-> parameters :path :recipe-id)]))
    ;; FIXME: recipe-db/transact-ingredient


(defn delete!
  [{:keys [env parameters] :as _request}]
  (let [ingredient (:body parameters)]))
    ;; FIXME: recipe-db/retract-ingredient
