(ns james.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sg]
            [james.core :refer :all]
            [james.specs]))

(def example-plugin
  {:name "Example Plugin"
   :match-re #"eg .+"
   :eval-fn (fn [_] (take 3 (sg/generate (s/gen :james/results))))})

(def example-results
  (take 3 (sg/generate (s/gen :james/results))))

(deftest runner
  (testing "filters plugins by the input matching the regex"
    (is (= []
           (run-plugins "foo" [example-plugin])))

    (is (<= (count (run-plugins "eg foo" [example-plugin]))
            3))))
