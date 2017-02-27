(ns calindar.db.migration
  (:require [clojure.java.jdbc :as sql]
            [calindar.db.common :as dbc]))

(defn migrated? []
  (-> (sql/query dbc/spec
                 [(str "select count(*) from information_schema.tables "
                       "where table_name='todos'")])
      first :count pos?))

(defn migrate []
  (when (not (migrated?))
    (print "Creating database structure...") (flush)
    (sql/db-do-commands dbc/spec
                        (sql/create-table-ddl
                         :todos
                         [[:id :uuid "PRIMARY KEY"]
                          [:name :varchar "NOT NULL"]
                          [:description :text]
                          [:date :text]
                          [:time :text]
                          [:recur :boolean]]))
    (println " done")))
