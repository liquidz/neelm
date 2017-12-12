(ns sin-curv
  (:require ;;[incanter.charts :refer [add-lines xy-plot]]
            ;;[incanter.core :refer [view save]]
            [neelm.core :refer :all]))

;;(defn- to-seq [x]
;;  (seq (col x 0)))
;;
;;(defn plot [x y predicted-y]
;;  (let [x' (to-seq x)
;;        y' (to-seq y)
;;        predicted-y' (to-seq predicted-y)
;;        c (xy-plot x' y')]
;;    (add-lines c x' predicted-y')
;;    (save c "result.png")))

(defn main []
  (let [x (range -10 10 0.1)
        y (map #(Math/sin %) x)
        model (fit (regression {:x x :y y :n-hidden 50 :add-bias? true}))]
    (score model x y)))
