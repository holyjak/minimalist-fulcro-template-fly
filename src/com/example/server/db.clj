(ns com.example.server.db
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [com.example.server.config :as config]
            [next.jdbc :as jdbc]))

(def ds (jdbc/get-datasource config/db-url))

(defn get-connection []
  (jdbc/get-connection ds))

(defn init-db []
  (let  [conn (jdbc/get-connection ds)
         stmts (-> (slurp (io/resource "db-init.sql"))
                   (str/split #"\n----\n"))]
    (assert (>= (count stmts) 2) (str "Expected min 2 migr. stmts, got " (count stmts)))
    ;; Set up the migration support
    (run! #(jdbc/execute-one! conn [%]) (take 2 stmts))
    ;; Run normal migrations
    (->> (drop 2 stmts)
         (map-indexed (fn [i stmt]
                        (println "Maybe executing db-init statement" i "(+ 2)")
                        (jdbc/execute-one! conn [(format "do $do$ begin perform idempotent('%d', $$\n%s\n$$); end $do$;"
                                               i
                                               stmt)])))
         doall)))