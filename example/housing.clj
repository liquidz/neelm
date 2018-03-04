(ns housing
  (:require [dev-util :as u]
            [neelm.core :refer :all]
            [neelm.util :as util]))

(defn dataset []
  (let [data (u/read-asset "housing.data")]
    {:x (util/normalize (map drop-last data))
     :y (map last data)}))

(defn main []
  (let [{:keys [x y]} (dataset)
        model (-> {:x x :y y} regressor fit)]
    (println "score:" (score model x y))
    (println "validation:" (validate model))))
