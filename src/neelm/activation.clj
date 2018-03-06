(ns neelm.activation
  (:require [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            [uncomplicate.neanderthal.vect-math :as n.v]))

(defmulti activate! (fn [kw _] kw))
(defmulti deactivate! (fn [kw _] kw))

;; g(x) = 1 / (1 + e^{-x})
(defmethod activate! :sigmoid
  [_ x]
  (scal! -1 x)
  (n.v/exp! x)
  (alter! x (fn ^double [^long i ^long j ^double v] (/ 1 (inc v)))))

;; g(x) = (e^{x} - e^{-x}) / (e^{x} + e^{-x})
;;      = (e^{2x} - 1) / (e^{2x} + 1)
(defmethod activate! :tanh
  [_ x]
  (scal! 2 x)
  (n.v/exp! x)
  (alter! x (fn ^double [^long i ^long j ^double v]
              (/ (- v 1) (+ v 1)))))

(defmethod deactivate! :sigmoid
  [_ y]
  (alter! y (fn ^double [^long i ^long j ^double v] (/ (- 1 v) v)))
  (n.v/log! y)
  (scal! -1 y))

;; ln((1 + x) / (1 - x)) / 2
(defmethod deactivate! :tanh
  [_ x]
  (alter! x (fn ^double [^long i ^long j ^double v] (/ (+ 1 v) (- 1 v))))
  (n.v/log! x)
  (scal! 1/2 x))

(defn activate [kw x]
  (let [y (copy x)]
    (activate! kw y)
    y))

(defn deactivate [kw y]
  (let [x (copy y)]
    (deactivate! kw x)
    x))
