(ns neelm.validation
  (:require [neelm.operation :as op]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]))

(defn stat [coll]
  (reduce (fn [res k]
            (let [vs (map k coll)]
              (assoc res k {:max (reduce max vs)
                            :min (reduce min vs)
                            :avg (/ (reduce + vs) (:iterations res))})))
          {:iterations (count coll)}
          (-> coll first keys)))

(defn remove-at [n coll]
  (let [v (vec coll)]
    (concat (subvec v 0 n)
            (subvec v (inc n)))))

(defmulti validate-model
  (fn [model & _] (:validation-method model)))

;; k-fold cross validation
(defmethod validate-model :k-fold
  [model opts]
  (let [{:keys [k fit-fn score-fn] :or {k 10}} opts
        {:keys [x y]} model
        num-row (mrows x)
        n (int (Math/floor (/ num-row k)))
        [x y] (op/shuffle x y)
        xs (op/partition n x)
        ys (op/partition n y)]
    (stat
     (for [i (range k)]
       (let [train-x (apply op/concat-rows (remove-at i xs))
             train-y (apply op/concat-rows (remove-at i ys))
             test-x (nth xs i)
             test-y (nth ys i)
             model (fit-fn model train-x train-y)]
         (score-fn model test-x test-y))))))
