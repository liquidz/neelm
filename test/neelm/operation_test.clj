(ns neelm.operation-test
  (:require [clojure.test :as t]
            [neelm.operation :as sut]
            [neelm.test-helper :as h]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]))

(t/deftest random-samples-test
  (t/testing "vector"
    (let [x (sut/random-samples 3)]
      (t/is (vctr? x))
      (t/is (= 3 (dim x)))))

  (t/testing "matrix"
    (let [x (sut/random-samples 2 3)]
      (t/is (matrix? x))
      (t/is (= 2 (mrows x)))
      (t/is (= 3 (ncols x))))))

(t/deftest shape-test
  (t/testing "sequence"
    (t/is (= [2 3] (sut/shape [[1 2 3] [4 5 6]]))))
  (t/testing "matrix"
    (t/is (= [2 3] (sut/shape (dge 2 3 (range 6)))))))

(t/deftest plus-test
  (t/testing "matrix plus matrix"
    (t/is (= (sut/plus (dge 2 3 (range 6))
                       (dge 2 3 (range 6)))
             (dge 2 3 (map #(* 2 %) (range 6))))))

  (t/testing "vector plus vector"
    (t/is (= (sut/plus (dv 1 2 3) (dv 4 5 6))
             (dv 5 7 9))))

  (t/testing "matrix plus vector"
    (t/is (= (sut/plus (dge 2 3 (range 6))
                       (dv 10 20 30))
             (dge 2 3 [10 11 22 23 34 35])))))

(t/deftest to-matrix-test
  (t/testing "sequence"
    (let [mat (sut/to-matrix [[1 2 3] [4 5 6]])]
      (t/is (matrix? mat))
      (t/is (= [2 3] (sut/shape mat)))))

  (t/testing "matrix"
    (let [mat (sut/ensure-matrix (dge 2 3 (range 6)))]
      (t/is (matrix? mat))
      (t/is (= [2 3] (sut/shape mat))))))

(t/deftest inv-test
  (let [mat (op/ensure-matrix [[1 0 -1] [3 -2 3] [4 1 1]])]
    (t/is (= (mm mat (sut/inv mat))
             (dge 3 3 [1 0 0 0 1 0 0 0 1] {:layout :row})))))
