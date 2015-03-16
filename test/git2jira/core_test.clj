(ns git2jira.core-test
  (:require [git2jira.git2jira :as git2jira]
            [git2jira.core]
            [clojure.test :refer :all]))

(deftest wrong-jira-key-test
  (testing "passing wrong app key should return empty results"
    (is (empty? (git2jira/get-issues-ids-from-branches "git branch -r" "." "wrongkey")))))

(deftest wrong-git-command-test
  (testing "passing wrong app key should return no error"
    (is (some? (:error (git2jira/get-issues-ids-from-branches "grit branch -r" "." "git2jira"))))))

(deftest wrong-credentials-test
  (testing "passing wrong jira api credentials should return error"
    (is (some? (:error (git2jira/branches-info* "git branch -r" 
                                                "." 
                                                "wrong:credentials" 
                                                "mex" 
                                                {} 
                                                "http://jira.amers.ime.reuters.com/rest/api/2/search"))))))