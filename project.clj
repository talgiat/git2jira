(defproject git2jira "0.1.0-SNAPSHOT"
  :description "fetches status of branches from jira"
  :url "http://example.com/FIXME"
  :license {:name "Eclipse Public License"
            :url "http://www.eclipse.org/legal/epl-v10.html"}
  :dependencies [[org.clojure/clojure "1.5.1"]
                 [org.clojure/data.json "0.2.4"]
                 [doric "0.8.0"]
                 [org.clojure/tools.cli "0.3.0"]]
  :main git2jira.core)