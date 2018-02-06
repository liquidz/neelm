(ns neelm.elm-test
  (:require [clojure.test :as t]
            [neelm.elm :as sut]
            [neelm.test-helper :as h]
            [uncomplicate.neanderthal.native :refer :all]))

(t/deftest sigmoid-test
  (let [m (dge 1 3 [-10.0 0.0 10.0])]
    (sut/sigmoid! m)
    (t/is (= [0.0 0.5 1.0]
             (->> m h/to-seq first
                  (map #(double (/ (Math/round (* 10 %)) 10))))))))

;; (t/deftest forward-test
;;   (let [x (dge 2 3 (range 0 1 0.1))
;;         hidden-nodes 4
;;         a (dge hidden-nodes 3 (range 0 1 0.01))
;;         b (dv (take hidden-nodes (range 0 1 0.01)))]
;;     ))
