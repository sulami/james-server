(ns james.core
  (:require [clojure.spec.alpha :as s]
            [james.specs]))

(defn- filter-plugins
  "Filter out plugins that match the current input."
  [input plugins]
  (filter #(-> %
               :match-re
               (re-matches input)
               some?)
          plugins))

(defn run-plugin
  "Run a plugin with the input and verify spec validity."
  [input plugin]
  {:pre [(s/valid? :james/plugin plugin)]
   :post [(s/valid? :james/results %)]}
  ((:eval-fn plugin) input))

(defn run-plugins
  "Filter applicable plugins and run them with input."
  [input plugins]
  (->> plugins
       (filter-plugins input)
       (mapcat (partial run-plugin input))))

(defn- sort-results
  "Well, sort results."
  [results preferences]
  (sort-by :relevance > results))

(defn- attach-positions
  "Attach positions to ordered results."
  [results]
  (map #(assoc %1 :position %2) results (range)))

(defn prepare-results
  [results preferences]
  (-> results
      (sort-results preferences)
      attach-positions))
