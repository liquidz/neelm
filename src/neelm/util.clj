(ns neelm.util
  (:require [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            [neelm.operation :as op]))

(defn- vmax [v]
  (entry v (imax v)))

(defn- vmin [v]
  (entry v (imin v)))

(defn- normalize* [x from-val to-val]
  (let [cs (cols x)
        n (-> cs first dim)
        cols-max (dv (map vmax cs))
        cols-min (dv (map vmin cs))
        std (op/divide (op/minus x cols-min)
                       (op/minus cols-max cols-min))]
    (op/plus (scal (- to-val from-val) std)
             (dv (repeat (ncols x) from-val)))))

(defn normalize [x & [opt]]
  (let [from (:from opt 0.0)
        to (:to opt 1.0)
        mrows (count x)
        ncols (-> x first count)
        mat (dge mrows ncols (flatten x) {:layout :row})]
    (seq (normalize* mat from to))))
