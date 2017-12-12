(ns letter
  (:require [clojure.java.io :as io]
            [neelm.core :refer :all]
            [neelm.util :refer :all]
            [clojure.string :as str]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]))

(defn- read-resource [resource-name]
  (->> resource-name io/resource slurp
       str/split-lines
       (map #(str/split (str/trim %) #","))))

(defn- alph->num [s]
  (- (int (first s)) 65))

(def n-class 26)

(defn dataset []
  (let [data (read-resource "letter.data")
        num-rows (count data)
        num-cols (-> data first count dec)
        x (->> data (mapcat rest) (map #(Double/parseDouble %)))
        x (trans (dge num-cols num-rows x))
        y (map (comp alph->num first) data)
        y (nums->matrix y)]
    {:x x :y y}))

(defn main []
  (let [{:keys [x y]} (dataset)
        x (normalize x)
        [train-x test-x] (msplit-at 15000 x)
        [train-y test-y] (msplit-at 15000 y)
        model (fit (classification {:x train-x :y train-y :n-hidden 500}))
        num-rows (mrows test-x)
        y' (predict model test-x)]

    (->> (map = (matrix->nums test-y) (matrix->nums y'))
         (filter true?)
         count
         ((fn [n] (/ n (mrows test-x))))
         double)))
