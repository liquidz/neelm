(ns neelm.util
  "Utilities for dataset"
  (:require [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            [neelm.operation :as op]))

(defn normalize
  "Normalize inputed sequence

  * Defaults
    * opt `{:from 0.0 :to 1.0}`
  "
  [x & [opt]]
  (let [from (:from opt 0.0)
        to (:to opt 1.0)
        mrows (count x)
        ncols (-> x first count)
        mat (op/ensure-matrix x)]
    (apply map list (op/normalize mat from to))))
