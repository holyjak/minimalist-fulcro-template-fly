(ns com.example.client.ui
  (:require 
    [com.example.client.mutations :as mut]
    [com.fulcrologic.fulcro.algorithms.merge :as merge]
    [com.fulcrologic.fulcro.algorithms.tempid :as tempid]
    [com.fulcrologic.fulcro.algorithms.data-targeting :as targeting]
    [com.fulcrologic.fulcro.algorithms.normalized-state :as norm]
    [com.fulcrologic.fulcro.components :as comp :refer [defsc transact!]]
    [com.fulcrologic.fulcro.raw.components :as rc]
    [com.fulcrologic.fulcro.data-fetch :as df]    
    [com.fulcrologic.fulcro.dom :as dom :refer [button div form h1 h2 h3 input label li ol p ul]]))



(defsc Root [this props]
  {:query [[df/marker-table :load-progress] :new-thing :i-count]}
  (div
   (p "Hello from the ui/Root component!")
   (div {:style {:border "1px dashed", :margin "1em", :padding "1em"}}
        (p "Invoke a load! that fetches a count from the DB:")
        (when-let [m (get props [df/marker-table :load-progress])]
          (dom/p "Progress marker: " (str m)))
        (button {:onClick #(df/load! this :i-count (rc/nc '[:count]) 
                                     {:marker :load-progress})} 
                "I count!")
        (when (:i-count props)
          (p (str "Count is: " (-> props :i-count :count)))))))
        