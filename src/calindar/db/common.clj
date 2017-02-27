(ns calindar.db.common
  (:require [clojure.java.jdbc :as sql]
            [clj-uuid :as uuid]
            [clojure.string :as s]
            [clj-time.format :as ft]
            [clj-time.coerce :as ct]))

(def spec (or (System/getenv "DATABASE_URL")
              "postgresql://localhost:5432/todo"))

(def con (sql/get-connection spec))

(defn sql-array
  [v]
  (.createArrayOf con "varchar" (into-array String v)))

(defn pad-num
  [n]
  (if (< n 10) (str "0" n) (str n)))

(defn get-day-events [date]
  (into [] (sql/query spec
                      ["select * from todos where date = ? order by time asc" (format "%s-%s-%s" (:year date) (:month date) (:day date))])))

(defn add-todo
  [params]
  (let [[hour minute] (s/split (:time params) #":")
        time (s/join ":" [(pad-num (Integer/parseInt hour)) minute]) 
        result (sql/insert! spec
                            :todos {:id (uuid/v1)
                                    :name (:name params)
                                    :description (:description params)
                                    :date (:date params)
                                    :time time
                                    :recur (boolean (:recur params))})]
    {:status 200 :body (str (into [] result))}))

(defn all []
  (str (into [] (sql/query spec ["select * from todos order by date asc"]))))
