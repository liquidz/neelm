(ns neelm.activation
  (:require [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.vect-math :as n.v]))

(defmulti activate! (fn [kw _] kw))
(defmulti deactivate! (fn [kw _] kw))

;; g(x) = 1 / (1 + e^{-x})
(defmethod activate! :sigmoid
  [_ x]
  (scal! -1 x)
  (n.v/exp! x)
  (alter! x (fn ^double [^long i ^long j ^double v] (/ 1 (inc v)))))

;; g(x) = (1 - e^{-x}) / (1 + e^{-x})
(defmethod activate! :hyperbolic-tangent
  [_ x]
  (scal! -1 x)
  (n.v/exp! x)
  (alter! x (fn ^double [^long i ^long j ^double v] (/ (- 1 v) (+ 1 v)))))

(defmethod deactivate! :sigmoid
  [_ y]
  (alter! y (fn ^double [^long i ^long j ^double v] (/ (- 1 v) v)))
  (n.v/log! y)
  (scal! -1 y))

(defmethod deactivate! :hyperbolic-tangent
  [_ x]
  (alter! x (fn ^double [^long i ^long j ^double v] (/ (+ 1 v) (- 1 v))))
  (n.v/log! x))

(defn activate [kw x]
  (let [y (copy x)]
    (activate! kw y)
    y))

(defn deactivate [kw y]
  (let [x (copy y)]
    (deactivate! kw x)

    (when (some #(Double/isNaN %) (flatten (seq x)))
      (println "deactivate NaN !!" y)
      )

    x))
