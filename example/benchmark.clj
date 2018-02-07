(ns benchmark
  (:require [dev-util :as u]
            [criterium.core :as cc]
            [sin-curv :as ex.sin-curv]
            [housing :as ex.housing]
            [letter :as ex.letter]
            [neelm.core :refer :all]
            [neelm.util :as util]
            [clojure.string :as str]))

(defn sin-curv-fn [alg]
  (let [m (assoc (ex.sin-curv/dataset) :algorithm alg)]
    (fn [] (-> m regressor fit))))

(defn housing-fn [alg]
  (let [m (assoc (ex.housing/dataset)
                 :hidden-nodes 200 :algorithm alg)]
    (fn [] (-> m regressor fit))))

(defn letter-fn [alg]
  (let [m (assoc (ex.letter/dataset)
                 :hidden-nodes 500 :algorithm alg)]
    (fn [] (-> m classifier fit))))


(comment
  (let [{:keys [x y test-x test-y]} (ex.letter/dataset)
        hidden-nodes-list (range 200 600 50)
        num-try 10]
    (doseq [hidden-nodes hidden-nodes-list]
      (let [c (classifier {:x x :y y :hidden-nodes hidden-nodes :algorithm :elm})
            mean-time (/ (reduce + (map (fn [_] (u/benchmark (fit c))) (range num-try)))
                         num-try)
            accuracy (:accuracy (score (fit c) test-x test-y))]
        (println
         (str/join "," ["elm" hidden-nodes "-" mean-time accuracy]))))

    (doseq [hidden-nodes hidden-nodes-list
            lambda (map #(Math/pow 10 %) (range 3 8))]
      (let [c (classifier {:x x :y y :hidden-nodes hidden-nodes :algorithm :relm :lambda lambda})
            mean-time (/ (reduce + (map (fn [_] (u/benchmark (fit c))) (range num-try)))
                         num-try)
            accuracy (:accuracy (score (fit c) test-x test-y))]
        (println
         (str/join "," ["relm" hidden-nodes lambda mean-time accuracy]))))))




#_(let [{:keys [x y]} (ex.letter/dataset)
        elm (letter-fn :elm)
        relm (letter-fn :relm)
        num-try 3
        ]
    (time (dotimes [_ num-try] (elm)))
    (time (dotimes [_ num-try] (relm)))
    )
