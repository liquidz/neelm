(ns neelm.serialize-test
  (:require [clojure.test :as t]
            [neelm.serialize :as sut]
            [neelm.core :as nc]
            [clojure.java.io :as io]))

(def ^:private test-file-path ".test.model")

(t/deftest save-and-load-test
  (t/testing "regression"
    (let [x (split-at 3 (take 6 (range 0 1 0.1)))
          y [0.01 0.02]
          model (nc/fit (nc/regressor {:x x :y y}))]
      (sut/save model test-file-path)
      (t/is (.exists (io/file test-file-path)))
      (t/is (= model (sut/load test-file-path)))
      (-> test-file-path io/file .delete)))

  (t/testing "classification"
    (let [x (split-at 3 (take 6 (range 0 1 0.1)))
          y ["A" "B"]
          model (nc/fit (nc/classifier {:x x :y y}))]
      (sut/save model test-file-path)
      (t/is (.exists (io/file test-file-path)))
      (t/is (= model (sut/load test-file-path)))
      (-> test-file-path io/file .delete)))

  )
