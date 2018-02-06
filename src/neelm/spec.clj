(ns neelm.spec
  (:require [clojure.spec.alpha :as s]
            [uncomplicate.neanderthal.core :refer :all]))

(s/def ::matrix matrix?)
(s/def ::vector vctr?)
(s/def ::sequence sequential?)

(s/def ::hidden-nodes pos-int?)
(s/def ::x (s/or :matrix ::matrix
                 :sequence ::sequence))
(s/def ::y (s/or :matrix ::matrix
                 :sequence ::sequence))

(s/def ::normalize? boolean?)

(s/def ::activation #{:sigmoid})

(s/def ::type #{:regression :classification})

(s/def ::classes (s/+ any?))

(s/def ::model*
  (s/keys :req-un [::x ::y]
          :opt-un [::normalize?]))

(s/def ::classification-model*
  (s/merge
    ::model*
    (s/keys :opt-un [::classes])))

(s/def ::model
  (s/merge
    ::model*
    (s/keys :req-un [::type ::hidden-nodes])))

(s/def ::classification-model
  (s/merge
    ::classification-model*
    (s/keys :req-un [::type ::hidden-nodes])))
