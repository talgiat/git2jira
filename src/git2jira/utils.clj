(ns git2jira.utils)

(def not-nil? (complement nil?))

(defn coll2string [coll]
  (apply str (interpose "," coll)))