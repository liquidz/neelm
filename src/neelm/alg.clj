(ns neelm.alg
  "Interface to ELM algorithms"
  (:require [neelm.alg.elm :as elm]
            [neelm.alg.melm :as melm]
            [neelm.alg.relm :as relm]))

(defmulti fit
  {:arglists '([model])}
  (fn [model] (:algorithm model)))

(defmethod fit :default
  [model]
  (elm/fit model))

(defmethod fit :relm
  [model]
  (relm/fit model))

(defmethod fit :melm
  [model]
  (melm/fit model))

(defmulti predict
  {:arglists '([model x])}
  (fn [model _] (:algorithm model)))

(defmethod predict :default
  [model x]
  (elm/predict model x))

(defmethod predict :melm
  [model x]
  (melm/predict model x))
