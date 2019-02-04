(ns james.core
  (:require [clojure.spec.alpha :as s]
            [james.specs])
  (:import [jline.console ConsoleReader]))

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
  "Well, sorts results."
  [results query preferences]
  (let [preferred (get preferences [:past-choices query])
        top-choice (some #(= (:hash %) preferred) results)]
    (if preferred
      (conj (sort-by :relevance > (remove (partial = top-choice) results))
            (assoc-in top-choice [:relevance] 1))
      (sort-by :relevance > results))))

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

(defn- print-prompt
  "Prints the prompt."
  [input]
  (print (str "\033[2K\r> " input))
  (flush))

(defn- handle-input
  "Handles the special inputs, dispatches the runner.
  Returns nil if we should exit."
  [input keyint]
  (case keyint
    ;; Backspace
    127 (apply str (butlast input))
    ;; Return
    13 nil
    ;; Default
    (str input (char keyint))))

(defn -main []
  (loop [input ""]
    (print-prompt input)
    (let [cr (ConsoleReader.)
          keyint (.readCharacter cr)
          new-input (handle-input input keyint)]
      (if (nil? new-input)
        (print "\033[2K\r")
        (recur new-input)))))
