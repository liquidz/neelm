(ns benchmark
  (:require [dev-util :as u]
            [incanter.charts :refer [add-lines xy-plot] :as chart]
            [incanter.core :refer [view save]]
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

#_(defn plot [x y predicted-y]
  (let [x' (to-seq x)
        y' (to-seq y)
        predicted-y' (to-seq predicted-y)
        c (xy-plot x' y')]
    (add-lines c x' predicted-y')
    (save c "result.png")))

(defn compare-elm-and-relm-with-letter-dataset []
  (let [{:keys [x y test-x test-y]} (ex.letter/dataset)
        algorithms [:elm :relm]
        hidden-nodes-list (range 200 600 50)
        relm-lambda (Math/pow 10 4)
        num-try 10

        hidden-nodes-list (range 50 100 10)
        num-try 1]
    (for [hidden-nodes hidden-nodes-list
          algorithm algorithms]
      (let [c (classifier {:x x :y y :hidden-nodes hidden-nodes :algorithm algorithm :lambda relm-lambda})
            mean-time (/ (reduce + (map (fn [_] (u/benchmark (fit c))) (range num-try)))
                         num-try)
            accuracy (:accuracy (score (fit c) test-x test-y))]
        {:algorithm algorithm :hidden-nodes hidden-nodes :mean-time mean-time :accuracy accuracy}))))

#_(def res (doall (compare-elm-and-relm-with-letter-dataset)))

#_(let [{:keys [elm relm]} (group-by :algorithm res)]
  (-> (chart/bar-chart (map :hidden-nodes elm)
                       (map :mean-time elm))
      (chart/add-b)
      view
      )
  #_(-> (chart/xy-plot (map :hidden-nodes elm)
               (map :accuracy elm))
      (chart/add-lines (map :hidden-nodes relm)
                 (map :accuracy relm))
      (chart/add-lines (map :hidden-nodes elm)
                       (map :mean-time elm))
      (chart/add-lines (map :hidden-nodes relm)
                       (map :mean-time relm))
      view))


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
