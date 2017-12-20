(ns housing
  (:require [clojure.java.io :as io]
            [clojure.string :as str]
            [neelm.core :refer :all]
            [neelm.util :as util]))

(defn- read-asset [asset-name]
  (->> asset-name (io/file "assets") slurp
       str/split-lines
       (map #(str/split (str/trim %) #"\s+"))
       (map #(map (fn [s] (Double/parseDouble s)) %))))

(defn dataset []
  (let [data (read-asset "housing.data")]
    {:x (map drop-last data)
     :y (map last data)}))

(defn main []
  (let [{:keys [x y]} (dataset)
        x' (util/normalize x)
        hidden-nodes 200
        model1 (fit (regressor {:x x :y y :hidden-nodes hidden-nodes}))
        model2 (fit (regressor {:x x' :y y :hidden-nodes hidden-nodes}))]
    (println "model1 score:" (score model1 x y))
    (println "model2 score:" (score model2 x' y))))
