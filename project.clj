(defproject git2jira "0.2.0-SNAPSHOT"
  :description "fetches status of branches from jira"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.6.0"]
                 [org.clojure/data.json "0.2.4"]
                 [org.clojure/test.check "0.7.0"]
                 [org.clojure/tools.cli "0.3.0"]
                 [com.gfredericks/test.chuck "0.1.16"]
                 [doric "0.8.0"]]
  :main git2jira.core)