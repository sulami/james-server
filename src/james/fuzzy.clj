(ns james.fuzzy)

(def anything ".*?")
(def anything-greedy ".*")

(defn- query->regex
  "Builds a query for a regex."
  [query]
  (-> query
      (interleave (map (partial format "[^%c]*") query))
      butlast
      (->> (apply str))
      re-pattern))

(defn- fuzzy-match
  "Returns a fuzzy matching function for filtering."
  [query]
  (->> query
       query->regex
       (partial re-find)))

(defn fuzzy-score
  "Returns a function that scores a string based on a query.
  transformer acts like for `fuzzy-find`."
  [transformer query result]
  (let [regex (query->regex query)
        match-length (comp count (partial re-find regex) transformer)
        leadup (->> query
                    first
                    (str anything-greedy)
                    re-pattern
                    (partial re-find)
                    (comp count))]
    [(match-length result)
     (leadup (transformer result))]))

(defn fuzzy-find
  "Filters, orders and scores a pool of choices by a query.
  transformer can be used to transform the pool before filtering, eg. if the
  string to match is wrapped in another data structure."
  ([query pool] (fuzzy-find identity query pool))
  ([transformer query pool]
   (if (empty? query)
     (list)
     (let [regex (query->regex query)
           filter-fn (fuzzy-match query)
           filtered-pool (filter (comp filter-fn transformer) pool)
           score-fn (partial (partial fuzzy-score transformer) query)]
       (->> pool
            (filter (comp filter-fn transformer))
            (sort-by score-fn))))))
