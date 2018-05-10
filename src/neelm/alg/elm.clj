(ns neelm.alg.elm
  "Basic Extreme Learning Machine Algorithm"
  (:require [neelm.operation :as op]
            [neelm.activation :as act]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            [uncomplicate.neanderthal.vect-math :as n.v]))

(defmulti forward
  {:arglists '([activation-function-keyword weight bias x])}
  (fn [kw & _] kw))

(defmethod forward :default
  [kw weight bias x]
  (let [x' (dge (mrows x) (mrows weight))]
    (mm! 1.0 x (trans weight) x')
    (op/plus! x' bias)
    (act/activate! kw x')
    x'))

(defn fit [model]
  (let [{:keys [x y hidden-nodes activation]} model
        n-cols (ncols x)
        weight (op/random-samples hidden-nodes n-cols)
        bias (op/random-samples hidden-nodes)
        h (forward activation weight bias x)
        beta (mm (op/pinv h) y)]
    (merge model {:weight weight :bias bias :beta beta})))

(defn predict [model x]
  (let [{:keys [weight bias beta activation]} model
        h (forward activation weight bias x)]
    (mm h beta)))
