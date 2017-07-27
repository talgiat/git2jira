(defproject git2jira "0.2.0-SNAPSHOT"
  :description "fetches status of branches from jira"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.8.0"]
                 [org.clojure/data.json "0.2.6"]
                 [org.clojure/test.check "0.9.0"]
                 [org.clojure/tools.cli "0.3.5"]
                 [doric "0.9.0"]]
  :main git2jira.core)