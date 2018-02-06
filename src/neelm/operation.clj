(ns neelm.operation
  "Matrix Operation"
  (:require [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.linalg :as n.l]
            [uncomplicate.neanderthal.native :refer :all])
  (:import uncomplicate.neanderthal.internal.api.RealMatrix
           uncomplicate.neanderthal.internal.api.RealVector))

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

;; https://software.intel.com/en-us/articles/implement-pseudoinverse-of-a-matrix-by-intel-mkl
(defn pinv [mat & {:keys [alpha beta] :or {alpha 1.0 beta 0.0}}]
  (let [[m n] (shape mat)
        k (min m n)
        {:keys [sigma u vt]} (n.l/svd mat true true)
        inva (dge n m)]
    (dotimes [i k]
      (let [si (entry sigma i i)
            ss (double (if (> si 1.0e-9) (/ 1.0 si) si))]
        (scal! ss (col u i))))
    (mm! alpha (trans vt) (trans u) beta inva)
    inva))

(defn to-matrix [x]
  (if (matrix? x)
    x
    (let [[m n] (shape x)]
      (dge m n (flatten x) {:layout :row}))))

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

(defn identity-matrix
  ([n] (identity-matrix n 1.0))
  ([n val]
   (let [mat (dge n n)]
     (dotimes [i n]
       (entry! mat i i val))
     mat)))
