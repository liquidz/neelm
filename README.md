# neelm

[Extreme Learning Machine](http://www.ntu.edu.sg/home/egbhuang/) implementation powered by [Neanderthal](https://github.com/uncomplicate/neanderthal).

## Usage

* TODO : clojars

### Supported algorithms

* Basic ELM
* [Regularized ELM](https://www.hindawi.com/journals/mpe/2015/129021/)
* [Multiple hidden layers ELM](https://www.hindawi.com/journals/mpe/2017/4670187/)

### Regression

sin curve example
```
(require '[neelm.core :refer :all])

(def x (range -10 10 0.1))
(def y (map #(Math/sin %) x))

(def model
  ;; Add bias to x
  (let [x (map #(vector % 1.0) x)]
    (fit (regressor {:x x :y y}))))

(println (predict model x))
(println (score model x y))
```

### Classification

download dataset to `./assets`
```
make
```
iris example ([full source](./examples/iris.clj))
```
(require '[neelm.core :refer :all]')

;; (dataset) is defined at examples/iris.clj
(def ds (dataset))
(def x (:x ds))
(def y (:y ds))

(def model
  (fit (classifier {:x x :y y})))

(println (predict model x))
(println (score model x y))
```

### More examples

See [examples](./examples/) directory.

## License

Copyright © 2018 Masashi Iizuka

Distributed under the Eclipse Public License either version 1.0 or (at your option) any later version.
