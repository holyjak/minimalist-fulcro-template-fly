(ns com.example.server.pathom
  "The Pathom parser that is our backend.

   Add your resolvers and 'server-side' mutations here."
  (:require [com.example.server.db :as db]
            [com.example.server.resolvers :as resolvers]
            [com.wsscode.pathom3.connect.indexes :as pci]
            [com.wsscode.pathom3.connect.runner :as pcr]
            [com.wsscode.pathom3.interface.async.eql :as p.a.eql]
            [edn-query-language.core :as eql]
            ;; reload so changes in resolvers are picked up:
            :reload))

(def my-resolvers-and-mutations resolvers/resolvers)

(def default-env
  (-> {:com.wsscode.pathom3.error/lenient-mode? true}
      #_(p.plugin/register pbip/mutation-resolve-params) ; needed or not?
      (pci/register my-resolvers-and-mutations)))

;; Fulcro requires that we query for ::pcr/attribute-errors if we want our code
;; to see it but Pathom complains about it (see https://github.com/wilkerlucio/pathom3/issues/156)
;; (I could also make a Pathom plugin to do this but this is easier for me)
(defn- omit-error-attribute [eql]
  (-> (eql/query->ast eql)
      (update :children (partial remove #(= {:type :prop, :key ::pcr/attribute-errors} (select-keys % [:type :key]))))
      (eql/ast->query)))

(def parse (fn parser [env eql] (p.a.eql/process (merge default-env env) (omit-error-attribute eql))))

(defn make-pathom-env 
  "Create an initial pathom `env`, lifting an query params to the top level
   for easy access by any nested resolver"
  [ring-request edn-transaction]
  (let [children (-> edn-transaction eql/query->ast :children)
        query-params (reduce
                       (fn collect-params [acc {:keys [type params]}]
                         (cond-> acc
                           (and (not= :call type) (seq params))  ; skip mutations
                           (merge params)))
                       {}
                       children)]
    {:ring/request ring-request
     :query-params query-params
     :conn (db/get-connection)}))
