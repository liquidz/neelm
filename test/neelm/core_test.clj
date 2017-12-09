(ns neelm.core-test
  (:require [clojure.test :as t]
            [neelm.core :as sut]
            [uncomplicate.neanderthal.core :refer :all]
            ))
#_(:require [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.linalg :as n.l]
            [uncomplicate.neanderthal.native :refer :all]
            [uncomplicate.neanderthal.vect-math :as n.v])

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

#_(t/deftest sigmoid-test
  (t/is false)
  )
