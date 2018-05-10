(ns neelm.operation-test
  (:require [clojure.test :as t]
            [neelm.operation :as sut]
            [uncomplicate.commons.core :refer [info]]
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

(t/deftest minus-test
  (t/testing "matrix minus matrix"
    (t/is (= (sut/minus (dge 2 3 (range 1 7))
                        (dge 2 3 (range 6)))
             (dge 2 3 (repeat 1)))))
  (t/testing "vector minus vector"
    (t/is (= (sut/minus (dv 4 5 6) (dv 1 2 3))
             (dv 3 3 3))))
  (t/testing "matrix minus vector"
    (t/is (= (sut/minus (dge 2 3 (range 6))
                        (dv 10 20 30))
             (dge 2 3 [-10 -9 -18 -17 -26 -25])))))

(t/deftest divide-test
  (t/testing "matrix divide matrix"
    (t/is (= (sut/divide (dge 2 3 [2 4 3 6 5 10])
                         (dge 2 3 [2 4 3 6 5 10]))
             (dge 2 3 (repeat 1)))))

  (t/testing "matrix divide vector"
    (t/is (= (sut/divide (dge 2 3 [2 4 3 6 5 10])
                         (dv [2 3 5]))
             (dge 2 3 [1 2 1 2 1 2])))))

(t/deftest ensure-matrix-test
  (t/testing "2 dim sequence"
    (let [mat (sut/ensure-matrix [[1 2 3] [4 5 6]])]
      (t/is (matrix? mat))
      (t/is (= [2 3] (sut/shape mat)))
      (t/is (= :column (:layout (info mat))))))

  (t/testing "1 dim sequence"
    (let [mat (sut/ensure-matrix [1 2 3])]
      (t/is (matrix? mat))
      (t/is (= [3 1] (sut/shape mat)))
      (t/is (= :column (:layout (info mat))))))

  (t/testing "matrix"
    (let [mat (sut/ensure-matrix (dge 2 3 (range 6)))]
      (t/is (matrix? mat))
      (t/is (= [2 3] (sut/shape mat)))
      (t/is (= :column (:layout (info mat)))))))

(t/deftest ensure-column-layout-test
  (t/testing "column layout"
    (t/are [x] (= :column (:layout (info (sut/ensure-column-layout x))))
      (dge 3 2 (range 6))
      (trans (dge 2 3 (range 6))))))

(t/deftest pinv-test
  (t/testing "column layout"
    (let [mat (sut/ensure-matrix [[1 0 -1] [3 -2 3] [4 1 1]])]
      (t/is (= (map #(Math/round %) (flatten (seq (mm mat (sut/pinv mat)))))
               [1 0 0 0 1 0 0 0 1]))))

  (t/testing "row layout"
    (t/is (thrown? AssertionError (sut/pinv (dge 3 2 (range 6) {:layout :row}))))))

(t/deftest inv-test
  (let [mat (sut/ensure-matrix [[1 0 -1] [3 -2 3] [4 1 1]])]
    (t/is (= (mm mat (sut/inv mat))
             (sut/diagonal-matrix 3)))))

(t/deftest diagonal-matrix-test
  (t/is (= (dge 3 3 [1 0 0 0 1 0 0 0 1])
           (sut/diagonal-matrix 3)))
  (t/is (= (dge 3 3 [2 0 0 0 2 0 0 0 2])
           (sut/diagonal-matrix 3 2))))

(t/deftest shuffle!-test
  ;; FIXME
  (let [m (dge 5 2 (range 10))]
    (t/is (not= m (sut/shuffle m)))))

(t/deftest partition-test
  (t/is (= (list (dge 3 2 [0 1 2 6 7 8])
                 (dge 3 2 [3 4 5 9 10 11]))
           (sut/partition 3 (dge 6 2 (range 12)))))
  (t/is (= (list (dge 3 2 [0 1 2 7 8 9])
                 (dge 3 2 [3 4 5 10 11 12]))
           (sut/partition 3 (dge 7 2 (range 14))))))

(t/deftest concat-cols-test
  (t/is (= (sut/ensure-matrix [[1 2 2 3]
                               [1 2 2 3]])
           (sut/concat-cols (dge 2 1 (repeat 1))
                            (dge 2 2 (repeat 2))
                            (dge 2 1 (repeat 3))))))

(t/deftest concat-rows-test
  (t/is (= (sut/ensure-matrix [[1 1]
                               [2 2]
                               [2 2]
                               [3 3]])
           (sut/concat-rows (dge 1 2 (repeat 1))
                            (dge 2 2 (repeat 2))
                            (dge 1 2 (repeat 3))))))
