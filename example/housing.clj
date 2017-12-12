(ns housing
  (:require [clojure.java.io :as io]
            [neelm.core :refer :all]
            [clojure.string :as str]))

(defn- read-resource [resource-name]
  (->> resource-name io/resource slurp
       str/split-lines
       (map #(str/split (str/trim %) #"\s+"))
       (map #(map (fn [s] (Double/parseDouble s)) %))))

(defn dataset []
  (let [data (read-resource "housing.data")]
    {:x (map drop-last data)
     :y (map last data)}))

(defn main []
  (let [{:keys [x y]} (dataset)
        n-hidden 200
        model1 (fit (regression {:x x :y y :n-hidden n-hidden}))
        model2 (fit (regression {:x x :y y :n-hidden n-hidden :add-bias? true}))
        model3 (fit (regression {:x x :y y :n-hidden n-hidden :normalize? true}))]
    (println "model1 score:" (score model1 x y))
    (println "model2 score:" (score model2 x y))
    (println "model3 score:" (score model3 x y))))

