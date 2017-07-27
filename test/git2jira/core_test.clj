(ns git2jira.core-test
  (:require [git2jira.git2jira :as git2jira]
            [git2jira.core]
            [clojure.test :refer :all]))

(defn is-error-code [result error-code]
  (is (= (error-code git2jira/error-codes)
         (get-in result [:error :code]))))

(deftest wrong-jira-key-test
  (testing "passing wrong app key should return empty results"
    (is (empty? (git2jira/get-issues-ids-from-branches "git branch -r" "." "wrongkey")))))

(deftest wrong-git-command-test
  (testing "passing wrong app key should return an error"
    (is-error-code (git2jira/get-issues-ids-from-branches "grit branch -r" "." "git2jira") :failed-running-git-command)))

(deftest wrong-credentials-test
  (testing "passing wrong jira api credentials should return error"
    (is-error-code (git2jira/branches-info* "git branch -r" 
                                            "." 
                                            "wrong:credentials" 
                                            "GTJ" 
                                            {} 
                                            "http://jira.amers.ime.reuters.com/rest/api/2/search")
                   :failed-parsing-json-response)))