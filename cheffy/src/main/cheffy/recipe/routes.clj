(ns cheffy.recipe.routes
  (:require [cheffy.recipe.handlers :as recipe]
            [cheffy.recipe.ingredient.handlers :as ingredient]
            [cheffy.recipe.step.handlers :as step]
            [cheffy.responses :as responses]
            [cheffy.middleware :as mw]))

(def routes
  ["/recipes" {:swagger {:tags ["recipes"]}
               :middleware [[mw/wrap-auth0]]}
   [""
    {:get {:handler recipe/browse
           :responses {200 {:body any?}}
           :summary "List all recipes"}
     :post {:handler recipe/create!
            :middleware [[mw/wrap-manage-recipes]]
            :parameters {:body {:name string? :prep-time number? :img string?}}
            :responses {201 {:body {:recipe-id string?}}}
            :summary "Create recipe"}}]
   ["/:recipe-id"
    [""
     {:get {:handler recipe/fetch
            :parameters {:path {:recipe-id string?}}
            :responses {200 {:body any?}}
            :summary "Retrieve recipe"}
      :put {:handler recipe/update!
            :middleware [[mw/wrap-recipe-owner] [mw/wrap-manage-recipes]]
            :parameters {:path {:recipe-id string?}
                         :body {:name string? :prep-time number? :public boolean? :img string?}}
            :responses {204 {:body nil?}}
            :summary "Update recipe"}
      :delete {:handler recipe/delete!
               :middleware [[mw/wrap-recipe-owner] [mw/wrap-manage-recipes]]
               :parameters {:path {:recipe-id string?}}
               :responses {204 {:body nil?}}
               :summary "Delete recipe"}}]
    ["/steps" {:middleware [[mw/wrap-recipe-owner] [mw/wrap-manage-recipes]]}
     [""
      {:post {:handler step/create!
              :parameters {:path {:recipe-id string?}
                           :body {:description string? :sort number?}}
              :responses {201 {:body {:step-id string?}}}
              :summary "Create step"}
       :put {:handler step/update!
             :parameters {:path {:recipe-id string?}
                          :body {:step-id string? :description string? :sort int?}}
             :responses {204 {:body nil?}}
             :summary "Update step"}
       :delete {:handler step/delete!
                :parameters {:path {:recipe-id string?}
                             :body {:step-id string?}}
                :responses {204 {:body nil?}}
                :summary "Delete step"}}]]
    ["/ingredients" {:middleware [[mw/wrap-recipe-owner] [mw/wrap-manage-recipes]]}
     [""
      {:post {:handler ingredient/create!
              :parameters {:path {:recipe-id string?}
                           :body {:name string? :sort int? :amount int? :measure string?}}
              :responses {201 {:body {:ingredient-id string?}}}
              :summary "Create ingredient"}
       :put {:handler ingredient/update!
             :parameters {:path {:recipe-id string?}
                          :body {:ingredient-id string? :name string? :sort int? :amount int? :measure string?}}
             :responses {204 {:body nil?}}
             :summary "Update ingredient"}
       :delete {:handler ingredient/delete!
                :parameters {:path {:recipe-id string?}
                             :body {:ingredient-id string?}}
                :responses {204 {:body nil?}}
                :summary "Delete ingredient"}}]]
    ["/favorite"
     {:post {:handler recipe/favorite!
             :parameters {:path {:recipe-id string?}}
             :responses {204 {:body nil?}}
             :summary "Favorite recipe"}
      :delete {:handler recipe/unfavorite!
               :parameters {:path {:recipe-id string?}}
               :responses {204 {:body nil?}}
               :summary "Unfavorite recipe"}}]]])
