(ns neelm.alg.relm
  "Regularized Extreme Learning Machine Algorithm"
  (:require [neelm.alg.elm :as elm]
            [neelm.operation :as op]
            [neelm.serialize :as n.s]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]))

(def default-argument
  {:lambda (Math/pow 10 4)})

(defn regularized-beta [h model]
  (let [{:keys [y hidden-nodes lambda]} model
        num-samples (mrows (:x model))
        ht (trans h)]
    (if (>= num-samples hidden-nodes)
      ;; \beta = (\frac{I}{C} + H^TH)^{-1}H^TY
      (let [hth (mm ht h)
            ic (op/diagonal-matrix (mrows hth) (/ 1 lambda))]
        (mm (op/inv (op/plus ic hth))
            ht y))
      ;; \beta = H^T(\frac{I}{C} + HH^T)^{-1}Y
      (let [hht (mm h ht)
            ic (op/diagonal-matrix (mrows hht) (/ 1 lambda))]
        (mm ht (op/inv (op/plus ic hht)) y)))))

(defn fit [model]
  (let [model (merge default-argument model)
        {:keys [x y hidden-nodes activation]} model
        a (op/random-samples hidden-nodes (ncols x))
        b (op/random-samples hidden-nodes)
        h (elm/forward activation a b x)
        beta (regularized-beta h model)]
    (merge model {:a a :b b :beta beta})))
