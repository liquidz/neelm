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
    {:x (trans (dge num-cols num-rows (mapcat drop-last data)))
     :y (dge num-rows 1 (map last data))}))

(defn main []
  (let [{:keys [x y]} (dataset)
        n-hidden 200
        x1 x
        x2 (add-bias x1)
        x3 (normalize x1)
        model1 (fit x1 y {:n-hidden n-hidden})
        model2 (fit x2 y {:n-hidden n-hidden})
        model3 (fit x3 y {:n-hidden n-hidden})]
    (println "model1 score:" (score model1 x1 y))
    (println "model2 score:" (score model2 x2 y))
    (println "model3 score:" (score model3 x3 y))))

