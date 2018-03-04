(ns iris
  (:require [dev-util :as u]
            [neelm.core :refer :all]))

(defn- dataset []
  (let [data (->> {:separator #"," :parse-fn identity}
                  (u/read-asset "iris.data"))]
    {:x (->> data
             (map drop-last)
             (map #(map u/parse-double %)))
     :y (map last data)}))

(defn main []
  (let [{:keys [x y]} (dataset)
        model (classifier {:x x :y y})]
    (println "score:" (score (fit model) x y))
    (println "validation:" (validate model))))
