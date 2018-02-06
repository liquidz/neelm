(ns neelm.alg.elm
  (:require [neelm.operation :as op]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            [uncomplicate.neanderthal.vect-math :as n.v]))

(defn sigmoid! [x]
  (scal! -1 x)
  (n.v/exp! x)
  (alter! x (fn ^double [^long i ^long j ^double x] (/ 1 (inc x)))))

(defmulti forward
  {:arglists '([activation-function-keyword a b x])}
  (fn [kw & _] kw))

(defmethod forward :sigmoid
  [_ a b x]
  (let [x' (dge (mrows x) (mrows a))]
    (mm! 1.0 x (trans a) x')
    (op/plus! x' b)
    (sigmoid! x')
    x'))

(defn fit [model]
  (let [{:keys [x y hidden-nodes activation]} model
        n-cols (ncols x)
        a (op/random-samples hidden-nodes n-cols)
        b (op/random-samples hidden-nodes)
        h (forward activation a b x)
        beta (mm (op/pinv h) y)]
    {:a a :b b :beta beta}))

(defn predict [model x]
  (let [{:keys [a b beta activation]} model
        h (forward activation a b x)]
    (mm h beta)))
