(ns neelm.alg.elm
  "Basic Extreme Learning Machine Algorithm"
  (:require [neelm.operation :as op]
            [neelm.activation :as act]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            [uncomplicate.neanderthal.vect-math :as n.v]))

(defmulti forward
  {:arglists '([activation-function-keyword a b x])}
  (fn [kw & _] kw))

(defmethod forward :default
  [kw a b x]
  (let [x' (dge (mrows x) (mrows a))]
    (mm! 1.0 x (trans a) x')
    (op/plus! x' b)
    (act/activate! kw x')
    x'))

(defn fit [model]
  (let [{:keys [x y hidden-nodes activation]} model
        n-cols (ncols x)
        a (op/random-samples hidden-nodes n-cols)
        b (op/random-samples hidden-nodes)
        h (forward activation a b x)
        beta (mm (op/pinv h) y)]
    (merge model {:a a :b b :beta beta})))

(defn predict [model x]
  (let [{:keys [a b beta activation]} model
        h (forward activation a b x)]
    (mm h beta)))
