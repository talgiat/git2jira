(ns git2jira.git2jira
  (:require [clojure.java.shell :refer [sh]]
            [clojure.pprint :refer [pprint print-table]]
            [clojure.data.json :as json]
            [clojure.string :as str]
            [doric.core :refer [table]]))

(defn- is-error? [result]
  (and (map? result) 
       (:error result)))

(defn- shell-failed? [result]
  (and (:exit result) 
       (not= 0 (:exit result))))

(defn- call-jira-api [credentials url]
  (try 
    (sh "curl" "-u" credentials "-X" "GET" "-H" "Accept: application/json" url)
    (catch Exception e 
      {:error (str "caught exception: " (.getMessage e))})))

(defn get-issues [ids fields credentials api-url]
  (let [base-url (str api-url "?fields=$fields$&jql=issuekey%20in%20($ids$)")
        fields (str/join "," (map name fields))
        ids (str/join "," ids)
        url (str/replace (str/replace base-url #"\$ids\$" ids) #"\$fields\$" fields)
        result (call-jira-api credentials url)]
    (if (shell-failed? result)
      {:error (:err result)}
      (try
        (:issues (json/read-str (:out result) :key-fn keyword))
        (catch Exception e
          {:error (str "Could not parse jira api response as JSON. Response was: " (:out result))})))))

(defn build-issues-view-model [issues fields-formatters]
  (let [formatters (apply juxt (map #(fn[fields] 
                                        [(first %) ((last %) fields)])
                                    fields-formatters))]
    (map #(into {} (concat [[:key (.toLowerCase (:key %))]] (-> % :fields formatters))) 
         issues)))

(defn get-git-branches [command dir]
  (try
    (:out (apply sh (concat (str/split command #"\s") [:dir dir])))
    (catch Exception e
      {:error (str "Trying to run command: '" command "' caught exception: " (.getMessage e))})))

(defn get-issues-ids-from-branches [command dir project-key]
  (let [key-pattern (str "(?i)" project-key "-[0-9]+")
        key-regex (re-pattern key-pattern)
        result (get-git-branches command dir)]
    (if (is-error? result)
      result
      (seq (re-seq key-regex result)))))

(defn branches-info* [command dir credentials project-key fields-formatters api-url]
  (let [issues-ids (get-issues-ids-from-branches command dir project-key)]
    (cond 
      (is-error? issues-ids) issues-ids
      (empty? issues-ids) {:error (str "No git branches whose name starts with the form: " 
                                       project-key 
                                       "-[0-9]+ ,were found for git project: " 
                                       dir)}
      :else (let [unique-issues (->> issues-ids (map #(.toLowerCase %) ,,,) distinct)
                  issues (get-issues unique-issues (keys fields-formatters) credentials api-url)]
              (if (is-error? issues)
                issues
                (build-issues-view-model issues fields-formatters))))))

(defn branches-info [command dir credentials project-key fields-formatters api-url]
  (let [branches-info (branches-info* command dir credentials project-key fields-formatters api-url)]
    (if (is-error? branches-info)
      (println (:error branches-info))
      (println (table (sort-by :key branches-info))))))