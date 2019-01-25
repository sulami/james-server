(ns james.core
  (:require [clojure.spec.alpha :as s]
            [james.specs]))

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

(defn- calculate-hash
  "Generates a deterministic hash for a result."
  [result]
  (str (:source result) "-" (:name result) "-" (:subtitle result)))

(defn- attach-hashes
  "Attaches hashes to results."
  [results]
  (map #(assoc %1 :hash (calculate-hash %1)) results))

(defn- sort-results
  "Well, sorts results."
  [results preferences]
  (sort-by :relevance > results))

(defn- attach-positions
  "Attaches positions to ordered results."
  [results]
  (map #(assoc %1 :position %2) results (range)))

(defn prepare-results
  [results preferences]
  (-> results
      attach-hashes
      (sort-results preferences)
      attach-positions))
