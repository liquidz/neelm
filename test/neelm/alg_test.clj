(ns neelm.alg-test
  (:require [clojure.test :as t]
            [neelm.alg :as sut]
            [neelm.core :as core]
            [neelm.operation :as op]
            [neelm.test-helper :as h]
            [uncomplicate.neanderthal.core :refer :all]))

(defmacro test-regression
  {:style/indent 2}
  [[model-sym] opts & body]
  `(t/testing "regression test"
     (let [{x# :x :as ds#} h/regression-dataset
           ~model-sym (-> (merge h/default-args ds# ~opts)
                          core/regressor
                          sut/fit)]
       (t/is (= [h/hidden-nodes 1] (op/shape (:beta ~model-sym))))
       (t/is (= [(count x#) 1] (op/shape (sut/predict ~model-sym (:x ~model-sym)))))
       (t/is (> (:coefficient-of-determination
                 (core/score ~model-sym (:x ~model-sym) (:y ~model-sym)))
                0.5))
       ~@body)))

(defmacro test-classification
  {:style/indent 2}
  [[model-sym] opts & body]
  `(t/testing "classification test"
     (let [{x# :x num-classes# :num-classes :as ds#} h/classification-dataset
           ~model-sym (-> (merge h/default-args ds# ~opts)
                          core/classifier
                          sut/fit)]
       (t/is (= [h/hidden-nodes num-classes#] (op/shape (:beta ~model-sym))))
       (t/is (= [(count x#) num-classes#] (op/shape (sut/predict ~model-sym (:x ~model-sym)))))
       (t/is (> (:accuracy
                 (core/score ~model-sym (:x ~model-sym) (:y ds#)))
                0.5))
       ~@body)))

(t/deftest basic-elm-test
  (test-regression [_] {:algorithm :elm})
  (test-classification [_] {:algorithm :elm}))

(t/deftest regularized-elm-test
  (let [opt {:algorithm :relm :lambda (Math/pow 10 4)}]
    (test-regression [_] opt)
    (test-classification [_] opt)))

(t/deftest regularized-melm-test
  (let [opt {:algorithm :melm :lambda (Math/pow 10 4)
             :activation :hyperbolic-tangent :hidden-layers 3}]
    (test-regression [model] opt
      (t/is (= 3 (count (:layer-params model)))))
    (test-classification [model] opt
      (t/is (= 3 (count (:layer-params model)))))))
