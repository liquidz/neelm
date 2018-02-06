(ns neelm.core-test
  (:require [clojure.test :as t]
            [fudje.sweet :as fj]
            [neelm.core :as sut]
            [neelm.operation :as op]
            [neelm.test-helper :as h]
            [uncomplicate.neanderthal.core :refer :all]))

(t/deftest regressor-test
  (t/testing "vector"
    (t/is
     (compatible
      (sut/regressor {:x [1 2 3] :y [4 5 6]})
      (fj/contains
       {:hidden-nodes (:hidden-nodes sut/default-argument)
        :x (fj/checker #(and (matrix? %)
                             (= [3 1] (op/shape %))))
        :y (fj/checker #(and (matrix? %)
                             (= [3 1] (op/shape %))))}))))

  (t/testing "matrix"
    (t/is
     (compatible
      (sut/regressor {:x [[1 2 3] [4 5 6]] :y [7 8] :hidden-nodes 10})
      (fj/contains
       {:hidden-nodes 10
        :x (fj/checker #(= [2 3] (op/shape %)))
        :y (fj/checker #(= [2 1] (op/shape %)))}))))

  (t/testing "normailze? option"
    (let [reg (sut/regressor {:x [[1 2 3] [4 5 6]] :y [1 2] :normalize? true})]
      (t/is (= [[0.0 0.0 0.0] [1.0 1.0 1.0]]
               (h/to-seq (:x reg)))))))
