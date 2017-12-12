(ns neelm.core-test
  (:require [clojure.test :as t]
            [neelm.core :as sut]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            [fudje.sweet :as fj]))

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

(t/deftest normalize-test
  (let [mat (dge 2 3 (range 6))
       res (sut/normalize mat)
       expected (dge 2 3 [0.0 1.0 0.0 1.0 0.0 1.0]) ]
    (t/is (= expected res))))

(t/deftest regression-test
  (t/testing "vector"
    (t/is
      (compatible
        (sut/regression {:x [1 2 3] :y [4 5 6]})
        (fj/contains
          {:n-hidden (:n-hidden sut/default-argument)
           :x (fj/checker #(and (matrix? %)
                                (= [3 1] (sut/shape %))))
           :y (fj/checker #(and (matrix? %)
                                (= [3 1] (sut/shape %))))}))))

  (t/testing "matrix"
    (t/is
      (compatible
        (sut/regression {:x [[1 2 3] [4 5 6]] :y [7 8] :n-hidden 10})
        (fj/contains
          {:n-hidden 10
           :x (fj/checker #(= [2 3] (sut/shape %)))
           :y (fj/checker #(= [2 1] (sut/shape %)))}))))

  (t/testing "add-bias"
    (t/is
      (compatible
        (sut/regression {:x [1 2 3] :y [4 5 6] :add-bias? true})
        (fj/contains
          {:x (fj/checker #(= [3 2] (sut/shape %)))})))))

#_(t/deftest sigmoid-test
  (t/is false)
  )
