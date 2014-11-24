(ns address-book.core.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]
            [address-book.core.routes.address-book-routes :refer [address-book-routes]]))

(defn init []
  (println "Address book application is starting"))

(defn example-post [request]
  (let [post-value (get-in request [:params :example-post])]
    (str "You posted: " post-value)))

(defroutes app-routes
  (route/not-found "Not Found"))

(def app
  (-> (routes address-book-routes app-routes)
      (wrap-defaults (assoc-in site-defaults [:security :anti-forgery] false))))