(ns dev-util
  (:require [clojure.java.io :as io]
            [clojure.string :as str]))

(def parse-double
  #(Double/parseDouble %))

(def default-opts
  {:separator #"\s+"
   :parse-fn parse-double})

(defn read-asset [asset-name & [opts]]
  (let [{:keys [separator parse-fn]} (merge default-opts opts)]
    (->> asset-name (io/file "assets") slurp
         str/split-lines
         (map #(str/split (str/trim %) separator))
         (map #(map parse-fn %)))))

(defmacro benchmark [expr]
  `(->> (with-out-str (time ~expr))
        (re-seq #"\d+\.\d+")
        first
        (Double/parseDouble)))
