(ns letter
  (:require [dev-util :as u]
            [neelm.core :refer :all]
            [neelm.util :as util]))

(defn dataset []
  (let [data (->> {:separator #"," :parse-fn identity}
                  (u/read-asset "letter.data"))]
    {:x (->> data (map rest) (map #(map u/parse-double %)) util/normalize)
     :y (map first data)}))

(defn main []
  (let [{:keys [x y]} (dataset)
        model (classifier {:x x :y y :hidden-nodes 200 :lambda (Math/pow 10 4)})]
    (validate model)))
