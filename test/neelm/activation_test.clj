(ns neelm.activation-test
  (:require [clojure.test :as t]
            [neelm.activation :as sut]
            [uncomplicate.neanderthal.native :refer :all]
            [uncomplicate.neanderthal.vect-math :as n.v]))

(t/deftest activation-and-deactivation-test
  (doseq [k [:sigmoid :tanh]
          x [(dge 3 3 (range 9))
             (dge 3 3 (range -4 5))]]
    (t/testing (name k)
      (t/is (= (seq x)
               (seq (n.v/round (sut/deactivate k (sut/activate k x)))))))))
