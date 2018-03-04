(ns neelm.spec
  (:require [clojure.spec.alpha :as s]
            [uncomplicate.neanderthal.core :refer :all]))

(s/def ::matrix matrix?)
(s/def ::sequence sequential?)

(s/def ::hidden-nodes pos-int?)
(s/def ::x* (s/or :matrix ::matrix
                  :sequence ::sequence))
(s/def ::y* (s/or :matrix ::matrix
                  :sequence ::sequence))
(s/def ::x ::matrix)
(s/def ::y ::matrix)

(s/def ::type #{:regression :classification})
(s/def ::algorithm #{:elm :relm})
(s/def ::activation #{:sigmoid})

(s/def ::regression-model*
  (s/keys :req-un [::x* ::y*]
          :opt-un [::hidden-nodes ::activation ::algorithm]))

(s/def ::regression-model
  (s/keys :req-un [::x ::y ::type ::hidden-nodes ::activation ::algorithm]))

(s/def ::classes (s/+ any?))

(s/def ::classification-model*
  (s/merge
    ::regression-model*
    (s/keys :opt-un [::classes])))

(s/def ::classification-model
  (s/merge
   ::regression-model
   (s/keys :req-un [::classes])))

(s/def ::model (s/keys :req-un [::x ::y]))

(s/def ::fit-fn fn?)
(s/def ::score-fn fn?)

(s/def ::validate-options
  (s/keys :req-un [::fit-fn ::score-fn]))
