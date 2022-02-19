(ns novus.recipe.handlers
  (:require [novus.recipe.db :as recipe-db]
            [novus.responses :as responses]
            [ring.util.response :as rr])
  (:import (java.util UUID)))

(defn browse
  [{:keys [env claims] :as _request}]
  (let [account-id (:sub claims)]))
    ;; FIXME: recipe-db/find-all-recipes


(defn create!
  [{:keys [env claims parameters] :as _request}]
  (let [recipe-id (UUID/randomUUID)
        account-id (:sub claims)
        recipe (:body parameters)]))
    ;; FIXME: recipe-db/transact-recipe


(defn fetch
  [{:keys [env parameters] :as _request}]
  (let [recipe-id (-> parameters :path :recipe-id)
        ;; FIXME: recipe-db/find-recipe-by-id
        recipe '(recipe-db/find-recipe-by-id)]

    (if recipe
      (rr/response recipe)
      (rr/not-found {:type "recipe-not-found"
                     :message "Recipe not found"
                     :data (str "recipe-id " recipe-id)}))))

(defn update!
  [{:keys [env claims parameters] :as _request}]
  (let [recipe-id (-> parameters :path :recipe-id)
        account-id (:sub claims)
        recipe (:body parameters)]))
    ;; FIXME: recipe-db/transact-recipe


(defn delete!
  [{:keys [env parameters] :as _request}]
  (let [recipe-id (-> parameters :path)]))
    ;; FIXME: recipe-db/retract-recipe

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
