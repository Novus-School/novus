(ns novus.account-tests
  (:require [clojure.test :refer :all]
            [novus.test-system :as ts]
            [datomic.client.api :as d]
            [integrant.repl.state :as state]))

(def db (-> state/system :db/datomic))
(def conn (:conn db))

(defn account-fixture
  [f]
  (ts/create-auth0-test-user
    {:connection "Username-Password-Authentication"
     :email "account-tests@novus.app"
     :password "s#m3R4nd0m-pass"})
  (reset! ts/token (ts/get-test-token "account-tests@novus.app"))
  (f)
  (d/transact conn {:tx-data [[:db/retractEntity [:account/account-id "account-tests@novus.app"]]]})
  (reset! ts/token nil))

(use-fixtures :once account-fixture)

(deftest account-tests

  (testing "Create user account"
    (let [{:keys [status]} (ts/test-endpoint :post "/v1/account" {:auth true})]
      (is (= status 201))))

  (testing "Update user role"
    (let [{:keys [status]} (ts/test-endpoint :put "/v1/account" {:auth true})]
      (is (= status 204))))

  (testing "Delete user account"
    (let [{:keys [status]} (ts/test-endpoint :delete "/v1/account" {:auth true})]
      (is (= status 204)))))
