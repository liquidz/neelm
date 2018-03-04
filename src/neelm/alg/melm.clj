(ns neelm.alg.melm
  (:require [neelm.activation :as act]
            [neelm.alg.elm :as elm]
            [neelm.alg.relm :as relm]
            [neelm.operation :as op]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]))

(def default-argument
  (merge relm/default-argument
         {:hidden-layers 3}))

(defn- split-cols-at [n mat]
  (let [[num-rows num-cols] (op/shape mat)]
    [(copy (submatrix mat 0 0 num-rows n))
     (copy (submatrix mat 0 n num-rows (- num-cols n)))]))

(defn fit [model]
  (let [model (merge default-argument model)
        {:keys [x y hidden-nodes hidden-layers activation]} model
        w (op/random-samples hidden-nodes (ncols x))
        b (op/random-samples hidden-nodes)
        h (elm/forward activation w b x)]
    ;; FIXME
    (merge model
           (loop [i 1
                  h h
                  beta (relm/regularized-beta h model)
                  result [{:w w :b b}]]
             (if (< i hidden-layers)
               (let [h1 (mm y (op/pinv beta))
                     h1 (op/normalize h1 -0.9 0.9)
                     he (op/safe-trans (op/concat-cols (dge (mrows h) 1 (repeat 1)) h))
                     t1 (trans (act/deactivate activation h1))
                     t2 (op/pinv he)
                     whe (mm t1 t2)
                     [b1 w1] (split-cols-at 1 whe)
                     b1 (dv (flatten (seq b1))) ;; vectorize
                     h2 (elm/forward activation w1 b1 h)]
                 (recur (inc i) h2 (relm/regularized-beta h2 model) (conj result {:w w1 :b b1})))
               {:layer-params result :beta beta})))))

(defn predict [model x]
  (let [{:keys [beta activation layer-params]} model
        h (reduce (fn [input {:keys [w b]}]
                    (elm/forward activation w b input))
                  x
                  layer-params)]
    (mm h beta)))
