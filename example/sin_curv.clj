(ns sin-curv
  (:require [neelm.core :refer :all]))

(defn dataset []
  (let [x (range -10 10 0.1)
        y (map #(Math/sin %) x)]
    {:x (map #(vector % 1.0) x) ;; add bias
     :y y}))

(defn main []
  (let [{:keys [x y]} (dataset)
        model (-> {:x x :y y} regressor fit)]
    (println (score model x y))))
