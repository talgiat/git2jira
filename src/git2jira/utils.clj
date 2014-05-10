(ns git2jira.utils)

(defn coll2string [coll]
  (apply str (interpose "," coll)))