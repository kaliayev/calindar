(ns calindar.handler
  (:require [compojure.core :refer :all]
            [compojure.route :as route]
            [cheshire.core :as json]
            [hiccup.page :refer [html5 include-css]]
            [clj-time.core :as t]
            [clj-time.format :as ft]
            [clj-time.local :as lt]
            [calindar.format :as f]
            [calindar.db.common :as dbc]
            [calindar.db.migration :as dbm]
            [ring.adapter.jetty :as ring]
            [ring.middleware.json :refer [wrap-json-params]]
            [ring.middleware.defaults :refer [wrap-defaults site-defaults]]))

(defn date-map
  [jodate]
  (letfn [(pad-int [n] (if (< n 10) (str "0" n) n))]
    {:day (pad-int (t/day jodate))
     :month (pad-int (t/month jodate))
     :year (t/year jodate)}))

(defn get-todos-day
  [req]
  (let [user-agent (get-in req [:headers "user-agent"])
        informal-date (or (:day (:params req)) "today")
        jodate (cond (= informal-date "today") (lt/local-now)
                   (= informal-date "tomorrow") (t/plus (lt/local-now) (t/days 1))
                   :else (ft/parse-local-date informal-date))
        date (date-map jodate)]
    (->> (dbc/get-day-events date)
         (f/format-by-ua user-agent date))))

(defroutes app-routes
  (GET "/"  req (dbc/all))
  (GET "/todos" req (get-todos-day req))
  (POST "/add" req (dbc/add-todo (:params req)))
  (route/not-found "Not Found"))

(def app
  (wrap-json-params (wrap-defaults app-routes (assoc-in site-defaults [:security :anti-forgery] false))))

(defn start [port]
  (ring/run-jetty app {:port port
                       :join? false}))

(defn -main []
  (dbm/migrate)
  (let [port (Integer. (or (System/getenv "PORT") "7040"))]
    (start port)))
