;; Based on https://github.com/borkdude/fly_io_clojure
(ns build
  (:require [clojure.tools.build.api :as b]))

(def lib 'com.example/app)
(def version "0.0.1")
(def class-dir "target/classes")
(def basis (b/create-basis {:project "deps.edn"}))
(def uber-file (format "target/%s-%s-standalone.jar" (name lib) version))
(def jar-file (format "target/%s-%s.jar" (name lib) version))

(defn clean [_]
  (b/delete {:path "target"}))

(defn jar [_]
  (b/write-pom {:class-dir class-dir
                :lib lib
                :version version
                :basis basis
                :src-dirs ["src"]})
  (b/copy-dir {:src-dirs ["src" "resources"]
               :target-dir class-dir})
  (b/jar {:class-dir class-dir
          :jar-file jar-file}))

(defn uber [_]
  (clean nil)
  (b/copy-dir {:src-dirs ["src/main" "resources"]
               :target-dir class-dir})
  ;; NOTE: Can comment out compilation to skip AOT => be much faster
  ;;       (but then we must start via `clojure.main -m <ns>`)
      ;#_ ; let's skip AOT for now...
  (b/compile-clj {:basis basis
                  :src-dirs ["src/main"]
                  :class-dir class-dir
                  :ns-compile '[com.example.server.main]})
  (b/uber {:class-dir class-dir
           :uber-file uber-file
           :basis basis
           :main 'com.example.server.main})
  (println "Uberjar written to" uber-file))
