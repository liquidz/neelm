(ns neelm.score.regression
  (:require [neelm.operation :as op]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            [uncomplicate.neanderthal.vect-math :as n.v]))

(defn coefficient-of-determination
  "Coefficient of determination R^2 of the prediction
  y  - expected
  y' - predicted"
  [y y']
  (let [y (op/ensure-matrix y)
        y-v (col y 0)
        y'-v (col y' 0)
        num-rows (mrows y)
        y-mean (/ (sum y-v) num-rows)
        y-mean-v (dv (repeat num-rows y-mean))
        u (sum (n.v/pow (axpy -1 y'-v y-v) 2))
        v (sum (n.v/pow (axpy -1 y-mean-v y-v) 2))]
    (- 1.0 (/ u v))))
