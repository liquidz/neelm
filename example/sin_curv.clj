(ns sin-curv
  (:require [neelm.core :refer :all]))

(defn dataset []
  (let [x (range -10 10 0.1)
        y (map #(Math/sin %) x)]
    {:x (map #(vector % 1.0) x) ;; add bias
     :y y}))

(defn main []
  (let [{:keys [x y]} (dataset)
        model (regressor {:x x :y y})]
    (println "score:" (score (fit model) x y))
    (println "validation:" (validate model))))
