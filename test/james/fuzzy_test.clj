(ns james.fuzzy-test
  (:require [james.fuzzy :as sut]
            [clojure.test :refer :all]))

(deftest fuzzy-find-test
  (testing "it returns an empty list for empty queries"
    (is (empty? (sut/fuzzy-find "" [1 2 3]))))

  (testing "it returns an empty list for empty pools"
    (is (empty? (sut/fuzzy-find "query" []))))

  (testing "it returns the query if it is in the pool"
    (is (= '("abc")
           (sut/fuzzy-find "abc" ["abc"]))))

  (testing "it doesn't return the query if it isn't in the pool"
    (is (empty? (sut/fuzzy-find "abc" ["def"]))))

  (testing "it returns results that start with the query"
    (is (= '("abcde" "abcdef")
           (sut/fuzzy-find "abc" ["abdef" "abcde" "abcdef"]))))

  (testing "it returns results that end with the query"
    (is (= '("deabc" "defabc")
           (sut/fuzzy-find "abc" ["defab" "deabc" "defabc"]))))

  (testing "it returns results that contain the query"
    (is (= '("xabcx")
           (sut/fuzzy-find "abc" ["xabcx" "blob"]))))

  (testing "it returns results that contain the query broken up"
    (is (= '("abxcd")
           (sut/fuzzy-find "abcd" ["abxcd"]))))

  (testing "it prefers shorter matches over longer ones"
    (is (= '("abcdef" "axb")
           (sut/fuzzy-find "ab" ["axb" "abcdef"]))))

  (testing "it prefers earlier matches to later ones"
    (is (= '("abcdef" "defabc")
           (sut/fuzzy-find "abc" ["defabc" "abcdef"]))))

  (testing "it prefers shorter matches over early ones"
    (is (= '("xxxabc" "axbc")
           (sut/fuzzy-find "abc" ["axbc" "xxxabc"])))))
