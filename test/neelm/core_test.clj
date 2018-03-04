(ns neelm.core-test
  (:require [clojure.test :as t]
            [fudje.sweet :as fj]
            [neelm.core :as sut]
            [neelm.operation :as op]
            [uncomplicate.neanderthal.core :refer :all]))

(t/deftest regressor-test
  (t/testing "vector"
    (t/is
     (compatible
      (sut/regressor {:x [1 2 3] :y [4 5 6]})
      (fj/contains
       {:type :regression
        :hidden-nodes (:hidden-nodes sut/default-argument)
        :x (fj/checker #(and (matrix? %)
                             (= [3 1] (op/shape %))))
        :y (fj/checker #(and (matrix? %)
                             (= [3 1] (op/shape %))))}))))

  (t/testing "matrix"
    (t/is
     (compatible
      (sut/regressor {:x [[1 2 3] [4 5 6]] :y [7 8] :hidden-nodes 10})
      (fj/contains
       {:type :regression
        :hidden-nodes 10
        :x (fj/checker #(= [2 3] (op/shape %)))
        :y (fj/checker #(= [2 1] (op/shape %)))})))))

(t/deftest classifier-test
  (t/testing "without classes (auto complete)"
    (t/is
     (compatible
      (sut/classifier {:x [1 2 3 4] :y ["a" "b" "c" "b"]})
      (fj/contains
       {:type :classification
        :hidden-nodes (:hidden-nodes sut/default-argument)
        :x (fj/checker #(and (matrix? %)
                             (= [4 1] (op/shape %))))
        :y (fj/checker #(and (matrix? %)
                             (= [4 3] (op/shape %))))
        :classes (fj/just ["a" "b" "c"])}))))

  (t/testing "with classes"
    (t/is
     (compatible
      (sut/classifier {:x [1 2 3] :y ["a" "b" "c"] :classes ["a" "b" "c" "d"]})
      (fj/contains
       {:type :classification
        :y (fj/checker #(and (matrix? %)
                             (= [3 4] (op/shape %))))
        :classes (fj/just ["a" "b" "c" "d"])})))))
