(ns neelm.score.classification)

(defn- categorize-to-confusion-matrix [y y']
  (reduce (fn [res [a b]]
            (-> res
                (update-in [a b] #(inc (or % 0)))
                (update-in [a :tpfn] #(inc (or % 0)))
                (update-in [:tpfp b] #(inc (or % 0)))))
          {} (map list y y')))

(defn- f-score-for-labels [category labels]
  (for [l labels
        :let [precision (/ (get-in category [l l] 0)
                           (get-in category [:tpfp l] 1))
              recall (/ (get-in category [l l] 0)
                        (get-in category [l :tpfn] 1))
              pre+rec (+ precision recall)]]
    (/ (* 2 precision recall)
       (if (zero? pre+rec) 1 pre+rec))))

(defn f-score [y y' labels]
  (let [category (categorize-to-confusion-matrix y y')
        f-scores (f-score-for-labels category labels)]
    (double (/ (reduce + f-scores)
               (count f-scores)))))

(defn accuracy [y y']
  (double
   (/ (->> (map = y y') (filter true?) count)
      (count y))))
