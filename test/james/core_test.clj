(ns james.core-test
  (:require [clojure.test :refer :all]
            [clojure.spec.alpha :as s]
            [clojure.spec.gen.alpha :as sg]
            [james.core :refer :all :as sut]
            [james.specs]))

(def example-plugin
  {:name "Example Plugin"
   :match-re #"eg .+"
   :eval-fn (fn [_] (take 3 (sg/generate (s/gen :james/results))))})

(def example-results
  (->> #(sg/generate (s/gen :james/result))
       (repeatedly 3)
       (map #(assoc % :title "x"))))

(deftest runner-test
  (testing "doesn't run plugins if their re doesn't match"
    (is (= []
           (run-plugins "foo" [example-plugin]))))

  (testing "runs plugins if their re matches"
    (is (<= (count (run-plugins "eg foo" [example-plugin]))
            3))))

(deftest preparer-test
  (testing "attaches positions"
    (is (= (range 3)
           (map :position (prepare-results example-results "x" {})))))

  (testing "attaches hashes"
    (is (->> (prepare-results example-results "x" {})
             (map #(contains? % :hash))
             (every? true?))))

  (testing "generates unique hashes"
    (let [hashes (->> (prepare-results example-results "" {})
                      (map :hash))]
      (is (= (sort hashes)
             (-> hashes set list* sort)))))

  (testing "considers past choices"
    (let [results (list {:title "a"}
                        {:title "b"}
                        {:title "c"})
          preferences {:past-choices
                       {"query" (calculate-hash (second results))}}]
      (is (= "b"
             (-> results
                 (prepare-results "query" preferences)
                 first
                 :title))))))
