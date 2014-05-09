(ns git2jira.core
  (:require [clojure.java.shell :refer [sh]]
            [clojure.pprint :refer [pprint print-table]]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [clojure.tools.cli :refer [parse-opts]]
            [doric.core :refer [table]])
  (:gen-class))

(defn- get-issues [ids fields credentials]
  (let [base-url "http://jira.amers.ime.reuters.com/rest/api/2/search?fields=$fields$&jql=issuekey%20in%20($ids$)"
        fields (apply str (interpose "," (map name fields)))
        ids (apply str (interpose "," ids))
        url (str/replace (str/replace base-url #"\$ids\$" ids) #"\$fields\$" fields)
        result (sh "curl" "-u" credentials "-X" "GET" "-H" "Accept: application/json" url)]
    (:issues (json/read-str (:out result) :key-fn keyword))))

(defn- get-git-branches [command dir]
  (:out (apply sh (concat (str/split command #"\s") [:dir dir]))))

(defn- get-issues-info [ids credentials]
  (let [fields {:summary #(vector :summary (:summary %))
                :status #(vector :status (get-in % [:status :name])) 
                :fixVersions #(vector :fixVersions (first (map :name (:fixVersions %))))}
        fetchers (apply juxt (vals fields))
        issues (get-issues ids (keys fields) credentials)]
    (map #(into {} (concat [[:key (.toLowerCase (:key %))]] (-> % :fields fetchers))) issues)))

(defn get-info [command dir credentials project-key]
  (let [key-pattren (str "(?i)" project-key "-[0-9]+")
        key-regex (re-pattern key-pattren)]
    (when-let [branches (seq (re-seq key-regex (get-git-branches command dir)))]
      (println (table (get-issues-info branches credentials))))))

(def cli-options
  [["-gc" "--git-command command" "git command to list branches"
    :default "git branch"]
   ["-d" "--dir directory" "git project directory"]
   ["-c" "--credentials jira-credentials" "jira credentials for the project"]
   ["-k" "--key project-key" "jira project key prefix"
    :default "mex"]
   ["-h" "--help"]])

(defn -main [& args]
  (let [opts (parse-opts args cli-options)]
    (get-info (get-in opts [:options :git-command]) 
              (get-in opts [:options :dir])
              (get-in opts [:options :credentials])
              (get-in opts [:options :key]))
    (System/exit 0)))