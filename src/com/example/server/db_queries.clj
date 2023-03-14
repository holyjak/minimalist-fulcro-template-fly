(ns com.example.server.db-queries 
  (:require [next.jdbc :as jdbc]))

(defn my-test-count [conn]
  (jdbc/execute-one! conn ["select count(1) from my_test"]))