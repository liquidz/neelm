(ns letter
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [neelm.core :refer :all]))

(defn- read-asset [asset-name]
  (->> asset-name (io/file "assets") slurp
       str/split-lines
       (map #(str/split (str/trim %) #","))))

(defn- to-double-list [coll]
  (map #(Double/parseDouble %) coll))

(defn dataset []
  (let [data (read-asset "letter.data")]
    {:x (->> data (map rest) (map to-double-list))
     :y (map first data)}))

(defn main []
  (let [{:keys [x y]} (dataset)
        [train-x test-x] (split-at 15000 x)
        [train-y test-y] (split-at 15000 y)
        model (fit (classifier {:x train-x
                                :y train-y
                                :hidden-nodes 500
                                :normalize? true}))]
    (score model test-x test-y)))
