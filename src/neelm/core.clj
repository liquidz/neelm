(ns neelm.core
  "Extreme Learning Machine implementation powered by Neanderthal"
  (:require [neelm.alg :as alg]
            [neelm.operation :as op]
            neelm.score.classification
            neelm.score.regression
            neelm.validation
            neelm.serialize))

(def default-argument {:algorithm :relm
                       :hidden-nodes 100
                       :activation :sigmoid
                       :validation-method :k-fold})

(defn regressor
  "Generate model for regression"
  [{:keys [x y] :as arg-map}]
  (let [x (op/ensure-matrix x)
        y (op/ensure-matrix y)]
    (merge default-argument
           {:type :regression }
           (assoc arg-map :x x :y y))))

(defn classifier
  "Generate model for classification"
  [{:keys [x y classes] :as arg-map}]
  (let [x (op/ensure-matrix x)
        classes (vec (or classes (distinct y)))
        n-class (count classes)
        class-map (zipmap classes (range n-class))
        y (map #(get class-map %) y)
        y (op/nums->matrix y n-class)]
    (merge default-argument
           {:type :classification}
           (assoc arg-map :x x :y y :classes classes))))

(defn fit
  ([model x y] (fit (assoc model :x x :y y)))
  ([model] (alg/fit model)))

(defmulti predict
  {:arglists '([model x])}
  (fn [model x] (:type model)))

(defmethod predict :default
  [model x]
  (let [x (op/ensure-matrix x)]
    (alg/predict model x)))

(defmethod predict :classification
  [{:keys [classes] :as model} x]
  (->> (op/ensure-matrix x)
       (alg/predict model)
       op/matrix->nums
       (map #(nth classes %))))

(defmulti score
  {:arglists '([model x y])}
  (fn [model x y] (:type model)))

(defmethod score :regression
  [model x y]
  (let [y' (predict model x)]
    {:coefficient-of-determination
     (neelm.score.regression/coefficient-of-determination y y')}))

(defmethod score :classification
  [model x y]
  (let [y' (predict model x)]
    {:accuracy (neelm.score.classification/accuracy y y')
     :micro-f (neelm.score.classification/f-score y y' (:classes model))}))

(defmulti validate
  (fn [model & _] (:type model)))

(defmethod validate :default
  [model & [opts]]
  (neelm.validation/validate-model
   model (assoc opts :fit-fn fit :score-fn score)))

(defmethod validate :classification
  [model & [opts]]
  (letfn [(score-fn [model x y]
            (let [y (map #(nth (:classes model) %) (op/matrix->nums y))]
              (score model x y)))]
    (neelm.validation/validate-model
     model (assoc opts :fit-fn fit :score-fn score-fn))))

(defn save-model [model file-path]
  (neelm.serialize/save model file-path))

(defn load-model [file-path]
  (neelm.serialize/load file-path))
