(ns james.plugins.file-finder
  (:require [clojure.java.io :as io]))

(def search-dirs
  ["/Users/robinschroer/build/spielwiese/"])

(defn file-finder-action
  [result]
  nil)

(defn search-dir [query dir]
  (->> dir
       io/file
       file-seq
       rest))

(defn format-result [result]
  {:title (.getName result)
   :subtitle (.getParent result)
   :action nil
   :plugin nil
   :relevance 1}) ;; TODO actual values

(defn file-finder-eval
  [input]
  (->> search-dirs
       (mapcat (partial search-dir input))
       (map format-result)))

(def file-finder-plugin
  {:name "File finder"
   :match-re #".*"
   :eval-fn file-finder-eval})
