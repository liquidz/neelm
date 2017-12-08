(ns sin-curv
  (:require [incanter.charts :refer [add-lines xy-plot]]
            [incanter.core :refer [view]]
            [neelm.core :refer [add-bias fit predict]]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]))

(defn dataset []
  (let [x (range -10 10 0.1)
        y (map #(Math/sin %) x)
        n (count x)]
    [(add-bias (dge n 1 x))
     (dge n 1 y)]))

(defn- to-seq [x]
  (seq (col x 0)))

(defn plot [x y predicted-y]
  (let [x' (to-seq x)
        y' (to-seq y)
        predicted-y' (to-seq predicted-y)
        c (xy-plot x' y')]
    (add-lines c x' predicted-y')
    (view c)))

(defn main []
  (let [[x y] (dataset)
        model (fit x y)
        py (predict model x)]
    (plot x y py)))
