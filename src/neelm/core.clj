(ns neelm.core
  (:require [clojure.spec.alpha :as s]
            [neelm.alg :as alg]
            [neelm.operation :as op]
            neelm.score.classification
            neelm.score.regression
            neelm.serialize
            [neelm.spec :as spec]))

(def default-argument {:algorithm :elm
                       :hidden-nodes 200
                       :activation :sigmoid})

(defn regressor
  "Generate model for regression"
  [{:keys [x y] :as arg-map}]
  {:pre [(s/valid? ::spec/model* arg-map)]
   :post [(s/valid? ::spec/model %)]}
  (let [x (op/to-matrix x)
        y (op/to-matrix y)]
    (merge default-argument
           {:type :regression }
           (assoc arg-map :x x :y y))))

(defn classifier
  "Generate model for classification"
  [{:keys [x y classes] :as arg-map}]
  {:pre [(s/valid? ::spec/classification-model* arg-map)]
   :post [(s/valid? ::spec/classification-model %)]}
  (let [x (op/to-matrix x)
        classes (vec (or classes (distinct y)))
        n-class (count classes)
        class-map (zipmap classes (range n-class))
        y (map #(get class-map %) y)
        y (op/nums->matrix y n-class)]
    (merge default-argument
           {:type :classification}
           (assoc arg-map :x x :y y :classes classes))))

(defn fit [model]
  (merge model (alg/fit model)))

(defmulti predict
  {:arglists '([model x])}
  (fn [model x] (:type model)))

(defmethod predict :default
  [model x]
  (let [x (op/to-matrix x)]
    (alg/predict model x)))

(defmethod predict :classification
  [{:keys [classes] :as model} x]
  (->> (op/to-matrix x)
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

(defn save-model [model file-path]
  (neelm.serialize/save model file-path))

(defn load-model [file-path]
  (neelm.serialize/load file-path))
