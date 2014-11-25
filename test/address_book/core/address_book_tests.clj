(ns address-book.core.address-book-tests
  (:use midje.sweet)
  (:require [clojure.test :refer :all]
            [ring.mock.request :as mock]
            [address-book.core.handler :refer :all]
            [address-book.core.models.database :refer [database db]]
            [address-book.core.models.query-defs :as query]))

(def test-db (database :test))

(facts "Example GET and POST tests"
       (with-state-changes [(before :facts (query/create-contacts-table-if-not-exists! {} {:connection db}))
                            (after :facts (query/drop-contacts-table! {} {:connection db}))]

         (fact "Test GET"
               (with-redefs [test-db db]
                 (query/insert-contact<! {:name "JT" :phone "(321)" :email "JT@JT.com"} {:connecion test-db})
                 (query/insert-contact<! {:name "Utah" :phone "(432)" :email "J@Buckeyes.com"} {:connecion test-db})
                 (let [response (app (mock/request :get "/"))]
                   (:status response) => 200
                   (:body response) => (contains "<div class=\"column-1\">JT</div>")
                   (:body response) => (contains "<div class=\"column-1\">Utah</div>"))))

         (fact "Test POST"
               (with-redefs [test-db db]
                 (count (query/all-contacts {} {:connection test-db})) => 0
                 (let [response (app (mock/request :post "/post" {:name "Some Guy" :phone "(321)" :email "a@a.com"}))]
                   (:status response) => 302
                   (count (query/all-contacts {} {:connecion test-db})) => 1)))))
