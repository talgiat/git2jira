(ns git2jira.git2jira
  (:require [clojure.java.shell :refer [sh]]
            [clojure.pprint :refer [pprint print-table]]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [doric.core :refer [table]]
            [git2jira.utils :refer :all]))

(defn get-issues [ids fields credentials]
  (let [base-url "http://jira.amers.ime.reuters.com/rest/api/2/search?fields=$fields$&jql=issuekey%20in%20($ids$)"
        fields (coll2string (map name fields))
        ids (coll2string ids)
        url (str/replace (str/replace base-url #"\$ids\$" ids) #"\$fields\$" fields)
        result (sh "curl" "-u" credentials "-X" "GET" "-H" "Accept: application/json" url)]
    (:issues (json/read-str (:out result) :key-fn keyword))))

(defn build-issues-view-model [issues fields-formatters]
  (let [formatters (apply juxt (map #(fn[fields] 
                                       [(first %) ((last %) fields)])
                                    fields-formatters))]
    (map #(into {} (concat [[:key (.toLowerCase (:key %))]] (-> % :fields formatters))) issues)))

(defn get-git-branches [command dir]
  (:out (apply sh (concat (str/split command #"\s") [:dir dir]))))

(defn branches-info [command dir credentials project-key fields-formatters]
  (let [key-pattern (str "(?i)" project-key "-[0-9]+")
        key-regex (re-pattern key-pattern)]
    (when-let [issues-ids (seq (re-seq key-regex (get-git-branches command dir)))]
      (let [issues (get-issues issues-ids (keys fields-formatters) credentials)
            view-model (build-issues-view-model issues fields-formatters)]
        (println (table (sort-by :key view-model)))))))
