(ns sin-curv
  (:require [neelm.core :refer :all]))

(defn main []
  (let [x (range -10 10 0.1)
        y (map #(Math/sin %) x)
        x (map #(vector % 1.0) x) ;; add bias
        model (fit (regressor {:x x :y y :hidden-nodes 50}))]
    (println (score model x y))))

(comment
  (let [x (range -10 10 0.1)
        y (map #(Math/sin %) x)
        x (map #(vector % 1.0) x)
        n 10]
    (time (dotimes [_ n]
            (fit (regressor {:x x :y y}))))
    (time (dotimes [_ n]
            (fit (regressor {:x x :y y :algorithm :relm}))))))
