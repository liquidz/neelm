(ns neelm.alg
  (:require [neelm.alg.elm :as elm]
            [neelm.alg.relm :as relm]))

(defmulti fit
  (fn [model] (:algorithm model)))

(defmethod fit :default
  [model]
  (elm/fit model))

(defmethod fit :relm
  [model]
  (relm/fit model))

(defmulti predict
  (fn [model _] (:algorithm model)))

(defmethod predict :default
  [model x]
  (elm/predict model x))
