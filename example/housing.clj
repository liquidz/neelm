(ns housing
  (:require [clojure.java.io :as io]
            [neelm.core :refer :all]
            [clojure.string :as str]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            ))

(defn- read-resource [resource-name]
  (->> resource-name io/resource slurp
       str/split-lines
       (map #(str/split (str/trim %) #"\s+"))
       (map #(map (fn [s] (Double/parseDouble s)) %))))

(defn dataset []
  (let [data (read-resource "housing.data")
        num-rows (count data)
        num-cols (-> data first count dec) ]
    {:x (dge num-rows num-cols (mapcat drop-last data))
     :y (dge num-rows 1 (map last data))}))

(defn main []
  (let [{:keys [x y]} (dataset)
        model (fit x y {:n-hidden 500})]
    (score model x y)))
