(defproject calindar "0.1.0-SNAPSHOT"
  :description "FIXME: write description"
  :url "http://example.com/FIXME"
  :min-lein-version "2.0.0"
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [compojure "1.5.1"]
                 [ring/ring-defaults "0.2.1"]
                 [cheshire "5.7.0"]
                 [hiccup "1.0.4"]
                 [io.aviso/pretty "0.1.33"]
                 [org.clojure/java.jdbc "0.6.1"]
                 [clj-time "0.13.0"]
                 [danlentz/clj-uuid "0.1.7"]
                 [ring/ring-jetty-adapter "1.5.0"]
                 [org.postgresql/postgresql "9.4-1201-jdbc41"]
                 [ring/ring-json "0.4.0"]]
  :plugins [[lein-ring "0.9.7"]]
  :ring {:handler calindar.handler/app
         :init calindar.db.migration/migrate}
  :profiles
  {:dev {:dependencies [[javax.servlet/servlet-api "2.5"]
                        [ring/ring-mock "0.3.0"]]}})
