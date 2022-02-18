(ns cheffy.recipe.recipe.handlers
  (:require [cheffy.recipe.db :as recipe-db]
            [cheffy.responses :as responses]
            [ring.util.response :as rr])
  (:import (java.util UUID)))


(defn favorite!
  [{:keys [env claims parameters] :as _request}]
  (let [account-id (:sub claims)
        recipe-id (-> parameters :path :recipe-id)]))
    ;; FIXME: recipe-db/favorite-recipe


(defn unfavorite!
  [{:keys [env claims parameters] :as _request}]
  (let [account-id (:sub claims)
        recipe-id (-> parameters :path :recipe-id)]))
    ;; FIXME: recipe-db/unfavorite-recipe
