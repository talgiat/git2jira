(ns git2jira.core
  (:require [clojure.tools.cli :refer [parse-opts]]
            [git2jira.git2jira :as git2jira]
            [clojure.string :as str])
  (:gen-class))

(def fields-formatters { :summary #(subs (:summary %) 0 (min (count (:summary %)) 100))
                         :status #(get-in % [:status :name]) 
                         :fixVersions #(str/join "," (map :name (:fixVersions %)))
                         :assignee #(get-in % [:assignee :displayName])})

(def cli-options
  [["-gc" "--git-command <git command>" "git command to list branches"
    :default "git branch -r"]
   ["-d" "--dir <directory>" "git project directory"
    :validate [some? "project directory is mandatory"]]
   ["-c" "--credentials <username:password>" "jira credentials in the format- jira_username:jira_password"
    :validate [some? "git credentials are mandatory"]]
   ["-k" "--key <project-key>" "jira project key prefix, e.g. FOO" ]
   ["-u" "--api-url <url>" "jira api url, e.g. http://your.jira.domain/rest/api/2/search"
    :validate [some? "jira api url is mandatory"]]
   ["-h" "--help" "print these help instructions"]])

(defn get-summary [option] 
  (let [args (vec (take-while string? option))
        [short-arg long-arg desc] args
        default-index (.indexOf option :default)
        args-str (str short-arg ", " long-arg)]
    (cond 
      (> default-index -1) [args-str (str desc ". default: " (nth option (inc default-index)))]
      desc [args-str desc]
      :else [args-str])))

(defn usage [options]
  (let [options-summary (map #(str/join "\n\t" %) (map get-summary options))
        summary (str/join \newline options-summary)]
    (->> ["This is my program. There are many like it, but this one is mine."
          ""
          "Usage: lein run [options]"
          ""
          "Options:"
          summary
          ""]
         (str/join \newline))))

(defn -main [& args]
  (let [opts (parse-opts args cli-options)
        {:keys [git-command dir credentials key api-url help]} (:options opts)]
    (if help
      (println (usage cli-options))
      (git2jira/branches-info git-command dir credentials key fields-formatters api-url))
    (System/exit 0)))