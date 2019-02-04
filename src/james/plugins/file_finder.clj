(ns james.plugins.file-finder)

(defn file-finder-action
  [result]
  nil)

(defn file-finder-eval
  [input]
  (repeat 3 {:title input
             :subtitle ""
             :action nil
             :plugin nil
             :relevance 0.2})) ;; Fake

(def file-finder-plugin
  {:name "File finder"
   :match-re #".*"
   :eval-fn file-finder-eval})
