(ns james.core
  (:require [clojure.spec.alpha :as s]
            [james.specs]
            [james.fuzzy :refer [fuzzy-find]]))

(defn- filter-plugins
  "Filters out plugins that match the current input."
  [input plugins]
  (filter #(-> %
               :match-re
               (re-matches input)
               some?)
          plugins))

(defn run-plugin
  "Runs a plugin with the input and verify spec validity."
  [input plugin]
  {:pre [(s/valid? :james/plugin plugin)]
   :post [(s/valid? :james/results %)]}
  ((:eval-fn plugin) input))

(defn run-plugins
  "Filters applicable plugins and run them with input."
  [input plugins]
  (->> plugins
       (filter-plugins input)
       (mapcat (partial run-plugin input))))

(defn calculate-hash
  "Generates a deterministic hash for a result."
  [result]
  (str (:source result) "-" (:title result) "-" (:subtitle result)))

(defn- attach-hashes
  "Attaches hashes to results."
  [results]
  (map #(assoc %1 :hash (calculate-hash %1)) results))

(defn- sort-results
  "Well, sorts results.
  If there is a previous choice for this query it will be the top result."
  [results query preferences]
  (let [preferred (get-in preferences [:past-choices query])
        top-choice (some #(when (= (:hash %) preferred) %) results)
        sort-fn (partial fuzzy-find :title query)]
    (if top-choice
      (conj (sort-fn (remove (partial = top-choice) results))
            (assoc-in top-choice [:relevance] 1))
      (sort-fn results))))

(defn- attach-positions
  "Attaches positions to ordered results."
  [results]
  (map #(assoc %1 :position %2) results (range)))

(defn prepare-results
  [results query preferences]
  (-> results
      attach-hashes
      (sort-results query preferences)
      attach-positions))
