(ns com.example.server.resolvers 
  (:require [com.example.server.db-queries :as db-queries]
            [com.wsscode.pathom3.connect.operation :as pco]))

(pco/defresolver i-count
  [{:keys [conn] :as _env} _]
  {::pco/input  []
   ::pco/output [:i-count]}
  {:i-count (db-queries/my-test-count conn)})


(pco/defmutation create-random-thing [env {:keys [tmpid] :as params}]
  ;; Fake generating a new server-side entity with
  ;; a server-decided actual ID
  ;; NOTE: To match with the Fulcro-sent mutation, we
  ;; need to explicitly name it to use the same symbol
  {::pco/op-name 'com.example.client.mutations/create-random-thing
   ;::pco/params [:tempid]
   ::pco/output [:tempids]}
  (println "SERVER: Simulate creating a new thing with real DB id 123" tmpid)
  {:tempids {tmpid 123}})

(def resolvers [i-count create-random-thing])