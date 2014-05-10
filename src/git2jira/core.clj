(ns git2jira.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [git2jira.git2jira :as git2jira]
            [git2jira.utils :refer :all])
  (:gen-class))

(def fields-formatters { :summary #(:summary %)
                         :status #(get-in % [:status :name]) 
                         :fixVersions #(coll2string (map :name (:fixVersions %)))})

(def cli-options
  [["-gc" "--git-command command" "git command to list branches"
    :default "git branch"]
   ["-d" "--dir directory" "git project directory"]
   ["-c" "--credentials jira-credentials" "jira credentials for the project"]
   ["-k" "--key project-key" "jira project key prefix"
    :default "mex"]
   ["-h" "--help"]])

(defn -main [& args]
  (let [opts (:options (parse-opts args cli-options))
        {:keys [git-command dir credentials key]} opts]
    (git2jira/branches-info git-command dir credentials key fields-formatters)
    (System/exit 0)))