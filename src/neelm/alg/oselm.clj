(ns neelm.alg.oselm
  "On-Line Sequential Extreme Learning Machine"
  (:require [neelm.operation :as op]
            [neelm.alg.elm :as elm]
            [neelm.activation :as act]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all]
            [uncomplicate.neanderthal.vect-math :as n.v]

            ;; FIXME
            [neelm.core :as core]
            housing
            ))

(def test-hidden-nodes 20)

(def test-chunks
  (let [ds (housing/dataset)
        n test-hidden-nodes]
    (let [{:keys [x y]} ds]
      (map (fn [x' y']
             {:x (op/ensure-matrix x')
              :y (op/ensure-matrix y')})
           (partition n x)
           (partition n y)))))

(def test-first-chunk
  (let [ds (housing/dataset)]
    {:x (take test-hidden-nodes (:x ds))
     :y (take test-hidden-nodes (:y ds))}))

(def test-second-chunk
  (let [ds (housing/dataset)]
    {:x (->> (:x ds) (drop test-hidden-nodes) (take test-hidden-nodes)
             op/ensure-matrix)
     :y (->> (:y ds) (drop test-hidden-nodes) (take test-hidden-nodes)
             op/ensure-matrix)}))

(def default-argument
  {}
  )

(defn- calc-m0 [h]
  (op/inv (mm (trans h) h)))

(defn- boosting-phase [model]
  (let [{:keys [x y hidden-nodes activation]} model
        n-cols (ncols x)
        weight (op/random-samples hidden-nodes n-cols)
        bias (op/random-samples hidden-nodes)
        h0 (elm/forward activation weight bias x)
        m0 (calc-m0 h0)
        beta0 (mm m0 (trans h0) y)]
    (merge model {:weight weight :bias bias
                  ;;:h h0
                  :m m0
                  :beta beta0})))

(defn- sequential-learning-phase [model]
  (let [{:keys [x y hidden-nodes activation weight bias
                ;;h
                m beta]} model
        ;; h_{k+1}
        h' (elm/forward activation weight bias x)
        ;; M_{k+1}
        m' (op/minus m (op/divide (mm m h' (trans h') m)
                                  (op/plus (dge hidden-nodes hidden-nodes (repeat 1))
                                           (mm (trans h') m h'))))
        ;; ;; beta^{k+1}
        ;; beta^{k+1} = beta^k + (M_{k+1} h_{k+1})(t^T_{k+1} - (h^T_{k+1} beta_k))
        ;; beta' (do
        ;;         ;; beta^k + (M_{k+1} h_{k+1} )
        ;;         )
        beta'(op/plus beta
                 (mm m' h'
                     (op/minus y (mm (trans h') beta))))]
    (merge model {;;:h h'
                  :m m' :beta beta'})))

;; FIXME classification の場合、あとからラベルが追加になることがあるので
;;       どこかでカバーしてあげる必要がある
(defn fit [model]
  (if (every? #(contains? model %) [:m
                                    ;;:h
                                    :beta])
    (sequential-learning-phase model)
    (boosting-phase (merge default-argument model))))


(let [model (core/regressor (assoc (first test-chunks)
                                   :hidden-nodes test-hidden-nodes
                                   :algorithm :elm
                                   ))
      model (reduce (fn [model chunk]
                      (println ">>>>>>>>>>>>>>>")
                      (clojure.pprint/pprint (dissoc model :x :y :weight :bias :type :activation :validation-method))
                      (fit (merge model chunk)))
                    model
                    test-chunks)
      {:keys [xx yy]} (housing/dataset)
      ]
  (println "vvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvvv")

  (clojure.pprint/pprint (dissoc model :x :y :weight :bias :type :activation :validation-method))

  (println ".")
  (println ".")
  (println ".")
  (println ".")

  ;;(core/predict model xx)
  ;;(println (core/score model xx yy))
  )
