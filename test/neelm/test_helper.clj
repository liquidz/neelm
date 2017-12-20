(ns neelm.test-helper
  (:require [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]))

(defn to-seq [mat]
  (for [r (rows mat)]
    (seq r)))
