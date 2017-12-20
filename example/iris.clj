(ns iris
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [neelm.core :refer :all]))

(defn- read-asset []
  (for [line (str/split-lines (slurp (io/file "assets/iris.data")))
        :let [row (str/split line #",")
              x (drop-last row)]]
    (concat (map #(Double/parseDouble %) x) [(last row)])))

(defn- dataset []
  (let [data (read-asset)]
    {:x (map drop-last data)
     :y (map last data)}))

(defn main []
  (let [{:keys [x y]} (dataset)
        model (fit (classifier {:x x :y y}))]
    (score model x y)))
