(ns neelm.core
  (:require [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.linalg :as n.l]
            [uncomplicate.neanderthal.native :refer :all]
            [uncomplicate.neanderthal.vect-math :as n.v]))

(defn- randoms []
  (let [r (java.util.Random. (System/currentTimeMillis))]
    (repeatedly (fn [] (.nextGaussian r)))))

(defn random-samples
  ([n]
   (dv (take n (randoms))))
  ([n m]
   (dge n m (take (* n m) (randoms)))))

(defn- sigmoid [x]
  (let [x' (copy x)]
    (scal! -1 x')
    (n.v/exp! x')
    (alter! x' (fn ^double [^long i ^long j ^double x] (/ 1 (inc x))))
    x'))

(defn mpv!
  ([mat v] (mpv! 1 mat v))
  ([alpha mat v]
   (doseq [r (rows mat)]
     (axpy! alpha v r))))

(defn mpv
  ([mat v] (mpv 1 mat v))
  ([alpha mat v]
   (let [x (copy mat)]
     (mpv! alpha x v)
     x)))

(def shape (juxt mrows ncols))

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

(defn add-bias [mat]
  (let [[m n] (shape mat)
        res (dge m (inc n))]
    (entry! res 1.0)
    (dotimes [i n]
      (copy! (col mat i) (col res i)))
    res))

(defn forward
  "H = sigmoid(a*x+b)"
  [a b x]
  (sigmoid
   (mpv (mm x (trans a))
        b)))

(defn fit [x y & [opt]]
  (let [n-hidden (or (:n-hidden opt) 200)
        n-cols (ncols x)
        a (random-samples n-hidden n-cols)
        b (random-samples n-hidden)
        h (forward a b x)
        beta (mm (pinv h) y) ]
    {:a a :b b :beta beta}))

(defn predict [model x]
  (let [{:keys [a b beta]} model
        h (forward a b x)]
    (mm h beta)))
