(ns neelm.util
  (:require [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]))

(defn nums->matrix
  ([nums] (nums->matrix nums (inc (reduce max nums))))
  ([nums num-of-class]
   (let [num-rows (count nums)
         res (dge num-rows num-of-class)]
     (dotimes [i num-rows]
       (entry! res i (nth nums i) 1.0))
     res)))

(defn matrix->nums
  [mat]
  (map imax (rows mat)))

(defn msplit-at [n mat]
  (let [num-rows (mrows mat)
        num-cols (ncols mat)]
    (list
      (submatrix mat 0 0 n num-cols)
      (submatrix mat n 0 (- num-rows n) num-cols))))

(defn random-samples [x y & [opt]]
  (let [ratio (:ratio opt 2/3)
        num-rows (mrows x)
        num-cols (ncols x)
        mmm (int (* num-rows ratio))
        row-indexes (take mmm (shuffle (range num-rows)))
        x' (dge mmm num-cols)
        y' (dge mmm (ncols y)) ]
    (doseq [i (range mmm)]
      (copy! (row x (nth row-indexes i)) (row x' i)))
    (doseq [i (range mmm)]
      (copy! (row y (nth row-indexes i)) (row y' i)))
    [x' y']))
