(ns neelm.operation
  "Matrix Operation"
  (:refer-clojure :exclude [shuffle partition])
  (:require [neelm.operation :as op]
            [uncomplicate.commons.core :refer [info]]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.linalg :as n.l]
            [uncomplicate.neanderthal.native :refer :all])
  (:import [uncomplicate.neanderthal.internal.api RealMatrix RealVector]))

(defn- randoms []
  (let [r (java.util.Random. (System/currentTimeMillis))]
    (repeatedly (fn [] (.nextGaussian r)))))

(defn random-samples
  ([n] (dv (take n (randoms))))
  ([n m] (dge n m (take (* n m) (randoms)))))

(defmulti shape {:arglists '([x])} class)

(defmethod shape :default
  [ls]
  (let [m (count ls)]
    (if (-> ls first sequential?)
      [m (-> ls first count)]
      [m 1])))

(defmethod shape RealMatrix
  [mat]
  [(mrows mat) (ncols mat)])

(defmulti plus! (fn [a b] [(class a) (class b)]))
(defmethod plus! [RealMatrix RealMatrix]
  [m1 m2] (axpy! m2 m1))
(defmethod plus! [RealVector RealVector]
  [v1 v2] (axpy! v2 v1))
(defmethod plus! [RealMatrix RealVector]
  [m1 v1] (doseq [r (rows m1)]
            (axpy! v1 r)))

(defmulti divide! (fn [a b] [(class a) (class b)]))
(defmethod divide! [RealMatrix RealMatrix]
  [m1 m2]
  (alter! m1 (fn ^double [^long i ^long j ^double x]
               (/ x  (entry m2 i j)))))

(defmethod divide! [RealMatrix RealVector]
  [m1 v1]
  (alter! m1 (fn ^double [^long i ^long j ^double x]
               (/ x  (entry v1 j)))))

(defn plus [a b]
  (let [a' (copy a)]
    (plus! a' b)
    a'))

(defn minus [a b]
  (plus a (ax -1 b)))

(defn divide [a b]
  (let [a' (copy a)]
    (divide! a' b)
    a'))

(defmulti ensure-column-layout (comp :layout info))
(defmethod ensure-column-layout :column
  [mat] mat)
(defmethod ensure-column-layout :row
  [mat]
  (let [[m n] (shape mat)
        res (dge m n)]
    (copy! mat res)
    res))

;; https://software.intel.com/en-us/articles/implement-pseudoinverse-of-a-matrix-by-intel-mkl
(defn pinv [mat]
  {:pre [(= :column (:layout (info mat)))]}
  (let [[m n] (shape mat)
        k (min m n)
        {:keys [sigma u vt]} (n.l/svd mat true true)
        inva (dge n m)]
    (dotimes [i k]
      (let [si (entry sigma i i)
            ss (double (if (> si 1.0e-9) (/ 1.0 si) si))]
        (scal! ss (col u i))))
    (mm! 1.0 (trans vt) (trans u) inva)
    inva))

(defn ensure-matrix [x]
  (if (matrix? x)
    x
    (let [[m n] (shape x)]
      (if (= n 1)
        (dge m n x)
        (dge m n (flatten (apply map list x)))))))

(defn to-seq [mat]
  (for [r (rows mat)]
    (seq r)))

(defn nums->matrix
  ([nums] (nums->matrix nums (inc (reduce max nums))))
  ([nums num-of-class]
   (let [num-rows (count nums)
         res (dge num-rows num-of-class)]
     (dotimes [i num-rows]
       (entry! res i (nth nums i) 1.0))
     res)))

(defn matrix->nums
  [mat]
  (map imax (rows mat)))

(defn inv [mat]
  (-> mat n.l/trf n.l/tri))

(defn diagonal-matrix
  ([n] (diagonal-matrix n 1.0))
  ([n val]
   (let [mat (dge n n)]
     (dotimes [i n]
       (entry! mat i i val))
     mat)))

(defn shuffle
  [& matrixes]
  (let [m (-> matrixes first mrows)
        indexes (-> m range clojure.core/shuffle)]
    (for [mat matrixes
          :let [res (dge (mrows mat) (ncols mat))]]
      (do (dotimes [i m]
            (copy! (row mat (nth indexes i))
                   (row res i)))
          res))))

(defn partition
  ([n mat] (partition n mat false))
  ([n mat copy?]
   (let [[num-row num-col] (op/shape mat)]
     (for [grp (clojure.core/partition n (range num-row))]
       (cond-> (submatrix mat (first grp) 0
                          n num-col)
         copy? copy)))))

(defn concat-cols
  "each matrixes must have same mrows"
  [& matrixes]
  (let [num-row (-> matrixes first mrows)
        num-cols (map ncols matrixes)
        res (dge num-row (reduce + num-cols)) ]
    (loop [[mat & rest-matrixes] matrixes
           [num-col & rest-num-cols] num-cols
           col-start 0]
      (when mat
        (copy! mat (submatrix res 0 col-start num-row num-col))
        (recur rest-matrixes rest-num-cols (+ col-start num-col))))
    res))

(defn concat-rows
  "each matrixes must have same ncols"
  [& matrixes]
  (let [num-rows (map mrows matrixes)
        num-col (-> matrixes first ncols)
        res (dge (reduce + num-rows) num-col) ]
    (loop [[mat & rest-matrixes] matrixes
           [num-row & rest-num-rows] num-rows
           row-start 0]
      (when mat
        (copy! mat (submatrix res row-start 0 num-row num-col))
        (recur rest-matrixes rest-num-rows (+ row-start num-row))))
    res))

(defn safe-trans
  "transpose matrix without changing matrix layout"
  [mat]
  (ensure-column-layout (trans mat)))

(defn- vmax [v] (entry v (imax v)))
(defn- vmin [v] (entry v (imin v)))

(defn normalize
  ([x] (normalize x 0.0 1.0))
  ([x ymin ymax]
   (let [[num-row num-col] (shape x)
         n (* num-row num-col)
         xs (flatten (seq x))
         xmax (reduce max xs)
         xmin (reduce min xs)
         x' (copy x)]
     (alter! x' (fn ^double [^long i ^long j ^double xi]
                  (+ (* (/ (- xi xmin)
                           (- xmax xmin))
                        (- ymax ymin))
                     ymin)))
     x')))
