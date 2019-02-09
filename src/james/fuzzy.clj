(ns james.fuzzy)

(def anything
  "Non-greedy match-anything regex."
  ".*?")

(def anything-greedy ".*")

(defn fuzzy-find [query pool]
  (if (empty? query)
    (list)
    (let [regex (->> query
                     (interpose anything)
                     (apply str)
                     re-pattern)
          by-match-length (comp count (partial re-find regex))
          by-leadup (->> query
                         first
                         (str anything-greedy)
                         re-pattern
                         (partial re-find)
                         (comp count))]
      (->> pool
           (filter (partial re-find regex))
           (sort-by (juxt by-match-length by-leadup))))))
