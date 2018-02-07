(ns letter
  (:require [dev-util :as u]
            [neelm.core :refer :all]
            [neelm.util :as util]))

(defn dataset []
  (let [data (->> {:separator #"," :parse-fn identity}
                  (u/read-asset "letter.data"))
        x (->> data (map rest) (map #(map u/parse-double %)) util/normalize)
        y (map first data)
        [train-x test-x] (split-at 15000 x)
        [train-y test-y] (split-at 15000 y)]
    {:x train-x
     :y train-y
     :test-x test-x
     :test-y test-y}))

(defn main []
  (let [{:keys [x y test-x test-y]} (dataset)
        model (fit (classifier {:x x :y y :hidden-nodes 2000 :algorithm :relm :lambda (Math/pow 10 4)}))]
    (println (score model test-x test-y)))
  )

(comment
  (doseq [hidden-nodes [300 400 500 600]
          lambda (map #(Math/pow 10 %) [3 4 5 6])]
    (let [{:keys [x y test-x test-y]} (dataset)
          model (fit (classifier {:x x :y y :hidden-nodes hidden-nodes :algorithm :relm :lambda lambda}))]
      (println hidden-nodes lambda "--" (score model test-x test-y))))
  )
