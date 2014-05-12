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
    :default "git branch -r"]
   ["-d" "--dir directory" "git project directory"
    :validate [#(not-nil? %) "project directory is mandatory"]]
   ["-c" "--credentials jira-credentials" "jira credentials for the project"
    :validate [#(not-nil? %) "git credentials are mandatory"]]
   ["-k" "--key project-key" "jira project key prefix"
    :default "mex" ]
   ["-u" "--api-url jira-api-url" "jira api url"
    :validate [#(not-nil? %) "jira api url is mandatory"]]
   ["-h" "--help"]])

(defn -main [& args]
  (let [opts (parse-opts args cli-options)
        {:keys [git-command dir credentials key api-url help]} (:options opts)]
    (println opts)
    (if help
      (println (:summary opts))
      (git2jira/branches-info git-command dir credentials key fields-formatters api-url))
    (System/exit 0)))