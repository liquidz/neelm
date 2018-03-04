(ns neelm.test-helper
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [uncomplicate.neanderthal.core :refer :all]))

(defn to-seq [mat]
  (for [r (rows mat)]
    (seq r)))

(def hidden-nodes 50)

(def default-args
  {:hidden-nodes hidden-nodes})

(def regression-dataset
  (let [x (range -10 10 0.1)]
    {:x (map #(vector % 1.0) x)
     :y (map #(Math/sin %) x)}))

(def classification-dataset
  (let [data (->> (io/file "assets" "iris.data")
                  slurp
                  str/split-lines
                  (map #(str/split (str/trim %) #",")))
        y (map last data)]
    {:x (map (fn [row] (map #(Double/parseDouble %) (drop-last row))) data)
     :y y
     :num-classes (count (distinct y))}))
