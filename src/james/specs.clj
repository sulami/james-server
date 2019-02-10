(ns james.specs
  (:require [clojure.spec.alpha :as s]))

(s/def :james.plugin/name string?)
(s/def :james.plugin/match-re
  (s/with-gen (partial instance? java.util.regex.Pattern)
    #(s/gen #{#".*"})))
(s/def :james.plugin/eval-fn
  (s/with-gen fn?
    #(s/gen #{(fn [input] [])})))

(s/def :james/plugin
  (s/keys :req-un [:james.plugin/name
                   :james.plugin/match-re
                   :james.plugin/eval-fn]))

(s/def :james.result/title string?)
(s/def :james.result/subtitle string?)
(s/def :james.result/action
  (s/with-gen (s/nilable fn?)
    #(s/gen nil?)))
(s/def :james.result/hash string?)
(s/def :james.result/position
  (s/and int? #(<= 0 %)))

(s/def :james/result
  (s/keys :req-un [:james.result/title
                   :james.result/subtitle
                   :james.result/action]
          :opt-un [:james.result/hash
                   :james.result/position]))

(s/def :james/results
  (s/coll-of :james/result))
