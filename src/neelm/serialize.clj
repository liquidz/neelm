(ns neelm.serialize
  (:refer-clojure :exclude [load])
  (:require [clojure.edn :as edn]
            [clojure.java.io :as io]
            [neelm.operation :as op]
            [taoensso.nippy :as nippy]
            [uncomplicate.neanderthal.core :refer :all]
            [uncomplicate.neanderthal.native :refer :all])
  (:import [uncomplicate.neanderthal.internal.api RealMatrix RealVector]))

(defn- matrix->map [mat]
  (let [[m n] (op/shape mat)
        data (flatten (map seq (cols mat)))]
    {:num-rows m :num-cols n :data data}))

(defn- map->matrix [{:keys [num-rows num-cols data]}]
  (dge num-rows num-cols data))

(nippy/extend-freeze
 RealMatrix :neanderthal/real-matrix
 [x data-output]
 ;; TODO: encoded string too long
 (->> x
      matrix->map
      pr-str
      (.writeUTF data-output)))

(nippy/extend-thaw
 :neanderthal/real-matrix
 [data-input]
 (-> (.readUTF data-input)
     edn/read-string
     map->matrix))

(nippy/extend-freeze
  RealVector :neanderthal/real-vector
  [x data-output]
  (->> (seq x)
       pr-str
       (.writeUTF data-output)))

(nippy/extend-thaw
 :neanderthal/real-vector
 [data-input]
 (-> (.readUTF data-input)
     edn/read-string
     dv))

(defn save [model file-path]
  (-> file-path
      io/file
      (nippy/freeze-to-file model)))

(defn load [file-path]
  (-> file-path
      io/file
      nippy/thaw-from-file))
