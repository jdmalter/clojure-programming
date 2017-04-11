(ns chapter-three-core)

; list
`(a b :name 12.5)

; vector
['a 'b :name 12.5]

; map
{:name "Chas" :age 31}

; set
#{1 2 3}

; another map
{Math/PI "~3.14"
 [:composite "key"] 42
 nil "nothing"}

; set of maps
#{{:first-name "chas" :last-name "emerick"}
  {:first-name "brian" :last-name "carper"}
  {:first-name "christophe" :last-name "grand"}}

; operate on a vector
(def v [1 2 3])
(conj v 4)
(conj v 4 5) ; v is not modified by conj
(seq v)

; same operation works on maps
(def m {:a 5 :b 6})
(conj m [:c 7])
(seq m) ; m is not modified by conj

; and sets
(def s #{1 2 3})
(conj s 10)
(conj s 3 4) ; duplicate elements are not accepted
(seq s) ; s is not modified by conj

; and lists
(def l `(1 2 3))
(conj l 0) ; element appened to the front
(conj l 0 -1) ; in reverse order
(seq l) ; l is not modified by conj

; use into which is built from a seq of conjs
(into v [4 5])
(into m [[:c 7] [:d 8]])
(into #{1 2} [2 3 4 5 3 3 2])
(into [1] {:a 1 :b 2})

; collections can [conj seq count empty =]
; conj guarantees membership into a collection
(conj `(1 2 3) 4)
(into `(1 2 3) [:a :b :c])

; empty creates an empty version of its parameter data structure
(defn swap-pairs
  [sequential]
  (into (empty sequential)
        (interleave
          (take-nth 2 (drop 1 sequential))
          (take-nth 2 sequential))))
(swap-pairs (apply list (range 10)))
(swap-pairs (apply vector (range 10)))
(defn map-map
  [f m]
  (into (empty m)
        (for [[k v] m]
          [k (f v)])))
(map-map inc (hash-map :z 5 :c 6 :a 0))
(map-map inc (sorted-map :z 5 :c 6 :a 0))

; count indicates the number of entries
(count [1 2 3])
(count {:a 1 :b 2 :c 3})
(count #{1 2 3})
(count `(1 2 3))

; = indicates equality of entries
(= [1 2 3] `(1 2 3))
(= {:a 1 :b 2 :c 3} {:b 2 :c 3 :a 1})
(= #{1 2 3} #{2 3 1})
(= `(1 2 3) [1 2 3])

; sequences can [seq first rest next lazy-seq]
; seq produces a sequence over its argument
(seq "Clojure")
(seq {:a 5 :b 6})
(seq (java.util.ArrayList. (range 5)))
(seq (into-array ["Clojure" "Programming"]))
(seq [])
(seq nil)

; some functions call seq on their arguments
(map str "Clojure")
(set "Programming")

; [first rest next] provide sequence operations
(first "Clojure")
(rest "Clojure")
(next "Clojure")

; rest and next only differ on sequences of zero or one value
(rest [1]) ; ()
(next [1]) ; nil
(rest nil) ; ()
(next nil) ; nil

; seqs are immutable
(doseq [x (range 3)]
  (println x))

; sequences are not iterators, hence r and rst are not modified
(let [r (range 3)
      rst (rest r)]
  (prn (map str rst))
  (prn (map #(+ 100 %) r))
  (prn (conj r -1) (conj rst 42)))

; sequences are not lists, hence count requires a full traversal
(let [s (range 1e6)]
  (time (count s))) ; Elapsed time: 67.472508 msecs
(let [s (apply list (range 1e6))]
  (time (count s))) ; Elapsed time: 0.021668 msecs

; create seq with cons or list*
(cons 0 (range 1 5)) ; accepts a head element and tail seq
(cons :a [:b :c :d]) ; disregards tail's concrete type
(cons 0 (cons 1 (cons 2 (cons 3 (cons 4 (cons 5 (range 6 10)))))))
(list* 0 1 2 3 4 5 (range 6 10)) ; list* is equivalent to multiple cons calls

; lazy-seq elements only realized when needed
(lazy-seq [1 2 3])
(defn random-ints
  "Returns a lazy seq of random integers in the range [0, limit)."
  [limit]
  (lazy-seq
    (println "realizing random number")
    (cons (rand-int limit)
          (random-ints limit))))
(def rands (take 10 (random-ints 50)))
(first rands) ; realize the first rand by first call
(nth rands 3) ; all preceding values must be realized
(count rands) ; count forces entire sequence to be realized
(count rands) ; count of rands already realized

; there is a better way to do random-ints
(repeatedly 10 (partial rand-int 50))

; rest is more lazy than next
(def x (next (random-ints 50)))
(def x (rest (random-ints 50)))

; dorun and doall force the realization of the sequence
(dorun (take 5 (random-ints 50)))

; extract a sequence, process it, turn it back into a more appropriate data structure
(apply str (remove (set "aeiouy") 
                   "Clojure Programming"))

; lazy-sequences are persistent, so there might be out-of-memory errors without GC
(split-with neg? (range -5 5)) ; splits a seq into two lazy seq
(let [[t d] (split-with #(< % 12) (range 1e8))]
  [(count t) (count d)]) ; reverseing count t and count d WILL crash computer

; maps can [assoc dissoc get contains?]
(def m {:a 1 :b 2 :c 3})
(get m :b) ; 2
(get m :d) ; nil
(get m :d "notta") ; "notta"
(assoc m :d 4) ; {:c 3 :b 2 :d 4 :a 1}
(dissoc m :b) ; {:c 3 :a 1}

; assoc and dissoc work on multiple entries
(assoc m
       :x 4
       :y 5
       :z 6) ; {:y 5 :z 6 :c 3 :b 2 :x 4 :a 1}
(dissoc m
        :a
        :c) ; {:b 2}

; get and assoc work with vectors too
(def v [1 2 3])
(get v 1) ; 2 ; v associates the index 1 with the value 2
(get v 10) ; nil
(get v 10 "zipple")
(assoc v
       1 4
       0 -12
       2 :p) ; [-12 4 :p]

; assoc can append elements to a vector with the new value's index
(assoc v 3 10)
(assoc v (count v) 10)

; get works with sets
(get #{1 2 3} 2) ; 2
(get #{1 2 3} 4) ; nil
(get #{1 2 3} 4 "zilch") ; "zilch"
(when (get #{1 2 3} 2)
  (println "it contains '2'!")) ; it contains '2'!

; contains checks for a key (and not a value)
(contains? [1 2 3] 0) ; true ; checks for the 0 index
(contains? {:a 5 :b 6} :b) ; true ; checks for the :b key
(contains? {:a 5 :b 6} 42) ; false
(contains? #{1 2 3} 2) ; true

; get and contains work on [vectors maps sets Java-maps Strings Java-arrays]
(get "Jacob" 3) ; o
(contains? (java.util.HashMap.) "not-there") ; false
(get (into-array [1 2 3]) 0) ; 1

; get might not be able to determine whether there is an association
(get {:ethel nil} :lucy)
(get {:ethel nil} :ethel)

; find works like get but it returns the entry if present
(find {:ethel nil} :lucy)
(find {:ethel nil} :ethel)

; find works well with destructuring and conditionals
(if-let [e (find {:a 5 :b 6} :a)]
  (format "found %s => %s" (key e) (val e))
  "not found") ; "found :a => 5"
(if-let [[k v] (find {:a 5 :b 6} :a)]
  (format "found %s => %s" k v)
  "not found") ; "found :a => 5"

; nth is similar to get, but nth is more specific
(nth [:a :b :c] 2) ; :c
(get [:a :b :c] 2) ; :c
(nth [:a :b :c] 3) ; IndexOutOfBoundsException
(get [:a :b :c] 3) ; nil
(nth [:a :b :c] -1) ; IndexOutOfBoundsException
(get [:a :b :c] -1) ; nil

; nth and get are identical when there is a default return value
(nth [:a :b :c] -1 :default)
(get [:a :b :c] -1 :default)

; get gracefully handles things that don't support get
(nth 42 0) ; UnsupportedOperationException
(get 42 0) ; nil

; stacks are supported with [conj pop peek] which do what you'd expect
; use list
(conj '() 1) ; (1)
(conj '(2 1) 3) ; (3 2 1)
(peek '(3 2 1)) ; 3
(pop '(3 2 1)) ; (2 1)
(pop '(1)) ; ()
(pop '()) ; IllegalStateException

; use vector
(conj [] 1) ; [1]
(conj [1 2] 3) ; [3 2 1]
(peek [1 2 3]) ; 3
(pop [1 2 3]) ; [1 2]
(pop [1]) ; []
(pop []) ; IllegalStateException

; sets are maps that associate elements to themselves
(get #{1 2 3} 2) ; 2
(get #{1 2 3} 4) ; nil
(get #{1 2 3} 4 "not found") ; "not found"

; disj removes values from a set
(disj #{1 2 3} 3 1) ; #{2}

; define sorted map
(def sm (sorted-map :z 5 :x 9 :y 0 :b 2 :a 3 :c 4))
sm ; {:a 3 :b 2 :c 4 :x 9 :y 0 :z 5}

; rseq returns reverse sorted in constant time
(rseq sm) ; ([:z 5] [:y 0] [:x 9] [:c 4] [:b 2] [:a 3])

; sorted has far better performance than seq-only operations
(subseq sm <= :c) ; ([:a 3] [:b 2] [:c 4])
(subseq sm > :b <= :y) ; ([:c 4] [:x 9] [:y 0])
(rsubseq sm > :b <= :y) ; ([:y 0] [:x 9] [:c 4])

; compare supports all Clojure scalers and sequential collections (and java.lang.Comparable)
(compare 2 2) ; 0
(compare "ab" "abc") ; -1
(compare ["a" "b" "c"] ["a" "b"]) ; 1
(compare ["a" 2] ["a" 2 0]) ; -1

; convert predicate to comparator
((comparator <) 1 4) ; -1
((comparator <) 4 1) ; 1
((comparator <) 4 4) ; 0

; (comp - compare) negates the results of (compare)
(sorted-map-by compare :z 5 :x 9 :y 0 :b 2 :a 3 :c 4) ; {:a 3, :b 2, :c 4, :x 9, :y 0, :z 5}
(sorted-map-by (comp - compare) :z 5 :x 9 :y 0 :b 2 :a 3 :c 4) ; {:z 5, :y 0, :x 9, :c 4, :b 2, :a 3}

; define some function
(defn magnitude
  [x]
  (-> x Math/log10 Math/floor))
(magnitude 100)
(magnitude 100000)

; define some comparison function
(defn compare-magnitude
  [a b]
  (neg? (- (magnitude a) (magnitude b))))
((comparator compare-magnitude) 10 10000) ; -1
((comparator compare-magnitude) 100 10) ; 1
((comparator compare-magnitude) 10 75) ; 0

; create sorted set with comparator
(sorted-set-by compare-magnitude 10 1000 500) ; #{10 500 1000}
(conj *1 600) ; 600 is not added to set because its magnitude is the same as 500's magnitude
(disj *1 750) ; #{10 1000} 500 is disjoined from set because its magnitude is the same as 500's magnitude
(contains? *1 1239) ; 1239 has same order of magnitude as 1000

; subseq and rsubseq can implement linear interpolation
(defn interpolate
  "Takes a collection of points (as [x y] tuples), returning a function
   which is a linear interpolation between those points."
  [points]
  (let [results (into (sorted-map) (map vec points))]
    (fn [x]
      (let [[xa ya] (first (rsubseq results <= x))
            [xb yb] (first (subseq results > x))]
        (if (and xa xb)
          (/ (+ (* ya (- xb x)) (* yb (- x xa)))
             (- xb xa))
          (or ya yb))))))
(def f (interpolate [[0 0] [10 10] [15 5]]))(map f [2 10 12])

; collections are functions
(get [:a :b :c] 2) ; c
([:a :b :c] 2) ; c

(get {:a 5 :b 6} :b) ; 6
({:a 5 :b 6} :b) ; 6

(get {:a 5 :b 6} :c 7) ; 7
({:a 5 :b 6} :c 7) ; 7

(get #{1 2 3} 3) ; 3
(#{1 2 3} 3) ; 3

; collections keys are (often) functions
(get {:a 5 :b 6} :b) ; 6
(:b {:a 5 :b 6}) ; 6

(get {:a 5 :b 6} :c 7) ; 7
(:c {:a 5 :b 6} 7) ; 7

(get #{:a :b :c} :d) ; nil
(:d #{:a :b :c}) ; nil

; show difference between collection as function and key as function
(defn get-foo
  [map]
  (:foo map))
(get-foo nil) ; nil
(defn get-bar
  [map]
  (map :bar))
(get-bar nil) ; NullPointerException

; easier to use collections without get
(map :name [{:age 21 :name "David"}
            {:gender :f :name "Suzanne"}
            {:name "Sarah" :location "NYC"}]) ; ("David" "Suzanne" "Sarah")

; some returns first value in sequence that returns true
(some #{1 3 7} [0 2 4 5 6]) ; nil
(some #{1 3 7} [0 2 3 4 5 6]) ; 3

; filter is more general
(filter :age [{:age 21 :name "David"}
              {:gender :f :name "Suzanne"}
              {:name "Sara" :location "NYC"}]) ; ({:age 21, :name "David"})
(filter (comp (partial <= 25) :age) [{:age 21 :name "David"}
              {:gender :f :name "Suzanne" :age 20}
              {:name "Sara" :location "NYC" :age 34}]) ; ({:age 34 :name "Sara" :location "NYC"})

; remove is compliment of filter
(remove (comp (partial <= 25) :age) [{:age 21 :name "David"}
              {:gender :f :name "Suzanne" :age 20}
              {:name "Sara" :location "NYC" :age 34}]) ; ({:age 21 :name "David"} {:age 20 :name "Suzanne" :gender :f})

; lists are singly linked lists
`(1 2 3) ; (1 2 3)

; expressions in lists are not evaluated unless list is called
`(1 2 (+ 1 2)) ; (1 2 (+ 1 2))
(list 1 2 (+ 1 2)) ; (1 2 3)

; vectors can be created with vector and vec
(vector 1 2 3) ; [1 2 3]
(vec (range 5)) ; [0 1 2 3 4]

; vectors as tuples
(defn euclidean-division
  [x y]
  [(quot x y) (rem x y)])
(euclidean-division 42 8) ; [5 2]
((juxt quot rem) 42 8) ; does the same thing

; vectors work with destructuring
(let [[q r] (euclidean-division 53 7)]
  (str "53/7 = " q " * 7 + " r)) ; 53/7 = 7 * 7 + 4

; set literals must not contain duplicate keys
#{1 2 3} ; #{1 2 3}
#{1 2 3 3} ; IllegalArgmentException Duplicate key: 3

; construct some sets
(hash-set :a :b :c :d) ; #{:c :b :d :a}
(set [1 6 1 8 3 7 7]) ; #{7 1 6 3 8}

; map literals must not contain duplicate keys
{:a 5 :b 6} ; {:b 6 :a 5}
{:a 5 :a 5} ; IllegalArgumentException Duplicate key: :a

; construct some maps
(hash-map :a 5 :b 6) ; {:b 6 :a 5}
(apply hash-map [:a 5 :b 6]) ; {:b 6 :a 5}

; use simple map like an object
(def playlist
  [{:title "Elephant" :artist "The White Stripes" :year 2003}
   {:title "Helioself" :artist "Papas Fritas" :year 1997}
   {:title "Stories from the City, Stories from the Sea" :artist "PJ Harvey" :year 2000}
   {:title "Buildings and Grounds" :artist "Papas Fritas" :year 2000}
   {:title "Zen Rodeo" :artist "Mardi Gras BB" :year 2002}])
(map :title playlist) ; ("Elephant" "Helioself" "Stories from the City, Stories from the Sea" "Buildings and Grounds" "Zen Rodeo")
(defn summarize [{:keys [title artist year]}]
  (str title " / " artist " / " year))

; maps can partition a collection by a key function
(group-by #(rem % 3) (range 10)) ; {0 [0 3 6 9], 1 [1 4 7], 2 [2 5 8]}
(group-by :artist playlist)

; define some order data
(def orders
  [{:product "Clock" :customer "Wile Coyote" :qty 6 :total 300}
   {:product "Dynamite" :customer "Wile Coyote" :qty 20 :total 5000}
   {:product "Shotgun" :customer "Elmer Fudd" :qty 2 :total 800}
   {:product "Shells" :customer "Elmer Fudd" :qty 4 :total 100}
   {:product "Hole" :customer "Wile Coyote" :qty 1 :total 1000}
   {:product "Anvil" :customer "Elmer Fudd" :qty 2 :total 300}
   {:product "Anvil" :customer "Wile Coyote" :qty 6 :total 900}])

; a mix of group-by and reduce
(defn reduce-by
  [key-fn f init coll]
  (reduce (fn [summaries  x]
            (let [k (key-fn x)]
              (assoc summaries k (f (summaries k init) x))))
          {} coll))

; order totals by customer
(reduce-by :customer #(+ %1 (:total %2)) 0 orders)

; get customers for each product
(reduce-by :product #(conj %1 (:customer %2)) #{} orders)

; all orders by customer, and the by product
(reduce-by (juxt :customer :product)
           #(+ %1 (:total %2)) 0 orders)

; a reduce-by that works with nested maps
(defn reduce-by-in
  [keys-fn f init coll]
  (reduce (fn [summaries x]
            (let [ks (keys-fn x)]
              (assoc-in summaries ks
                        (f (get-in summaries ks init) x))))
          {} coll))

; all orders by customer, and then by product into nested map
(reduce-by-in (juxt :customer :product)
           #(+ %1 (:total %2)) 0 orders)

; which is equivalent to:
(def flat-breakup
  {["Wile Coyote" "Anvil"] 900,
   ["Elmer Fudd" "Anvil"] 300,
   ["Wile Coyote" "Hole"] 1000,
   ["Elmer Fudd" "Shells"] 100,
   ["Elmer Fudd" "Shotgun"] 800,
   ["Wile Coyote" "Dynamite"] 5000,
   ["Wile Coyote" "Clock"] 300})
(reduce #(apply assoc-in %1 %2) {} flat-breakup)

; arguments are not modified by operation
(+ 1 2) ; 3

; define a vector of one million elements
(def v (vec (range 1e6)))
(count v) ; 1000000
(def v2 (conj v 1e6)) ; add an element into v
(count v2) ; 1000001
(count v) ; v not modified by conj

; free versioning
(def version1 {:name "Chas" :info {:age 31}})
(def version2 (update-in version1 [:info :age] + 3))
version1 ; {:name "Chas", :info {:age 31}}
version2 ; {:name "Chas", :info {:age 34}}

; there are mutable collections, transients
(def x (transient []))
(def y (conj! x 1))
(count y) ; 1
(count x) ; 1, x was modified by conj to y

; define implementation of into without transients
(defn naive-into
  [coll source]
  (reduce conj coll source))
(= (into #{} (range 500))
   (naive-into #{} (range 500))) ; true
(time (do (into #{} (range 1e6))
        nil)) ; "Elapsed time: 462.547902 msecs"
(time (do (naive-into #{} (range 1e6))
        nil)) ; "Elapsed time: 781.066263 msecs"

; define implementation of into with transients
(defn faster-into
  [coll source]
  (persistent! (reduce conj! (transient coll) source)))
(time (do (faster-into #{} (range 1e6))
        nil)) ; "Elapsed time: 451.34639 msecs"

; convert between persistent and transient
(def v [1 2])
(def tv (transient v))
(conj v 3) ; [1 2 3] not modified by tv
(persistent! tv) ; [1 2]
(get tv 0) ; IllegalAccessError Transient used after persistent! call

; transients support most persistent functions
(nth (transient [1 2]) 1) ; 2
(get (transient {:a 1 :b 2}) :a) ; 1
((transient {:a 1 :b 2}) :a) ; 1, transients are functions too
((transient [1 2]) 1) ; 2, transients are functions too
(find (transient {:a 1 :b 2}) 2) ; ClassCastException

; transients have no value semantics
(= (transient [1 2]) (transient [1 2])) ; false

; attach metadata to value literal
(def a^{:created (System/currentTimeMillis)} [1 2 3])
(meta a) ; {:created 1491849442427}

; keywords can be added as metadata
(meta ^:private [1 2 3]) ; {:private true}
(meta ^:private ^:dynamic [1 2 3]) ; {:private true :dynamic true}

; add metadata
(def b (with-meta a (assoc (meta a) :modified (System/currentTimeMillis))))
(meta b) ; {:modified 1491873252563 :created 1491873208957}

; update metadata
(def b (vary-meta a assoc :modified (System/currentTimeMillis)))
(meta b) ; {:modified 1491873343833 :created 1491873208957}

; metadata does not affect value semantics
(= a b) ; true

; metadata is copied from persistent modifications
(meta (conj a 500)) ; {:created 1491873208957}

; implement empty board for conway's game of life
(defn empty-board
  "Creates a rectangular empty board of the specified width and height"
  [w h]
  (vec (repeat w (vec (repeat h nil)))))

; add living cells to empty board
(defn populate
  "Turns :on each of the cells specified as [y x] coordinates"
  [board living-cells]
  (reduce (fn [board coordinates]
            (assoc-in board coordinates :on))
          board
          living-cells))

; use empty board and populate
(def glider (populate (empty-board 6 6) #{[2 0] [2 1] [2 2] [1 2] [0 1]}))
(print glider)

; create an indexed step function
(defn neighbours
  [[x y]]
  (for [dx [-1 0 1] dy [-1 0 1] :when (not= 0 dx dy)]
    [(+ dx x) (+ dy y)]))
(defn count-neighbours
  [board loc]
  (count (filter #(get-in board %) (neighbours loc))))
(defn indexed-step
  "Yields the next step of the board, using indicies to determine neighbours, liveness, etc."
  [board]
  (let [w (count board)
        h (count (first board))]
    (loop [new-board board x 0 y 0]
      (cond
        (>= x w) new-board
        (>= y h) (recur new-board (inc x) 0)
        :else
        (let [new-liveness
              (case (count-neighbours board [x y])
                2 (get-in board [x y])
                3 :on
                nil)]
          (recur (assoc-in new-board [x y] new-liveness) x (inc y)))))))
(-> (iterate indexed-step glider) (nth 8) print)

; get rid of manual iteration
(defn indexed-step2
  "Yields the next step of the board, using indicies to determine neighbours, liveness, etc."
  [board]
  (let [w (count board)
        h (count (first board))]
    (reduce
      (fn [new-board x]
        (reduce
          (fn [new-board y]
            (let [new-liveness
                  (case (count-neighbours board [x y])
                    2 (get-in board [x y])
                    3 :on
                    nil)]
              (assoc-in new-board [x y] new-liveness)))
          new-board (range h)))
      board (range w))))
(-> (iterate indexed-step2 glider) (nth 8) print)

; get rid of nested reductions
(defn indexed-step3
  "Yields the next step of the board, using indicies to determine neighbours, liveness, etc."
  [board]
  (let [w (count board)
        h (count (first board))]
    (reduce
      (fn [new-board [x y]]
        (let [new-liveness
              (case (count-neighbours board [x y])
                2 (get-in board [x y])
                3 :on
                nil)]
          (assoc-in new-board [x y] new-liveness)))
      board (for [x (range  h) y (range w)] [x y]))))
(-> (iterate indexed-step3x glider) (nth 8) print)

; window function to illustrate one dimensional partition
(partition 3 1 (concat [nil] (range 5) [nil])) ; ((nil 0 1) (0 1 2) (1 2 3) (2 3 4) (3 4 nil))
(defn window1D
  "Returns a lazy sequence of 3-item windows centered around each item of coll"
  [coll]
  (partition 3 1 (concat [nil] coll [nil])))
(window1D (range 5)) ; ((nil 0 1) (0 1 2) (1 2 3) (2 3 4) (3 4 nil))

; create two dimensional partition
(defn window 
  "Returns a lazy sequence of 3-item windows centered around each item of coll,
   padded as necessary with pad or nil."
  ([coll] (window nil coll))
  ([pad coll]
    (partition 3 1 (concat [pad] coll [pad]))))
(defn cell-block
  "Creates a sequences of 3x3 windows from a triple of 3 sequences."
  [[left mid right]]
  (window (map vector left mid right)))
(defn liveness
  "Returns the liveness (nil or :on) of the center cell for the next step"
  [block]
  (let [[_ [_ center _] _] block]
    (case (- (count (filter #{:on} (apply concat block)))
             (if (= :on center) 1 0))
      2 center
      3 :on
      nil)))
(defn step-row
  "Yields the next state of the center row"
  [rows-triple]
  (vec (map liveness (cell-block rows-triple))))
(defn index-free-step
  "Yields the next state of the board."
  [board]
  (vec (map step-row (window (repeat nil) board))))
(= (nth (iterate indexed-step glider) 8)
   (nth (iterate index-free-step glider) 8)) ; true

; elegantly implement Conway's game of life
(defn step
  "Yields the next state of the world"
  [cells]
  (set (for [[loc n] (frequencies (mapcat neighbours cells))
             :when (or (= n 3) (and (= n 2) (cells loc)))]
         loc)))

; iterate glider
(->> (iterate step #{[2 0] [2 1] [2 2] [1 2] [0 1]})
  (drop 8)
  first
  (populate (empty-board 6 6))
  print)

; define generic step for neighbours, birth, and survive functions
(defn stepper
  "Returns a step function for Life-like cell automata.
   neighbours takes a location and return a sequential collection of locations.
   survive? and birth? are predicates on the number of living neighbours."
  [neighbours birth? survive?]
  (fn [cells]
    (set (for [[loc n] (frequencies (mapcat neighbours cells))
               :when (if (cells loc) (survive? n) (birth? n))]
           loc))))

; equivalent to last iteration on glider but more generic
(->> (iterate (stepper neighbours #{3} #{2 3}) #{[2 0] [2 1] [2 2] [1 2] [0 1]})
  (drop 8)
  first
  (populate (empty-board 6 6))
  print)

; create Hex version of Conway's game of life
(defn hex-neighbours
  [[x y]]
  (for [dx [-1 0 1] dy (if (zero? dx) [-2 2] [-1 1])]
    [(+ dx x) (+ dy y)]))
(def hex-step (stepper hex-neighbours #{2} #{3 4}))

; oscillator of period 4
(hex-step #{[0 0] [1 1] [1 3] [0 4]}) ; #{[2 2] [1 5] [1 -1]}
(hex-step *1) ; #{[1 1] [1 3] [2 4] [2 0]}
(hex-step *1) ; #{[1 5] [1 -1] [0 2]}
(hex-step *1) ; #{[0 0] [1 1] [1 3] [0 4]}

; Wilson's algorithm
(defn maze
  "Returns a random maze carved out of walls;
   walls is a set of 2-item sets #{a b} where a and b are locations.
   The returned maze is a set of the remaining walls."
  [walls]
  (let [paths (reduce (fn [index [a b]] ; paths is map of locations to adjancent locations
                        (merge-with into index {a [b] b [a]}))
                      {} (map seq walls)) ; map seq walls converts walls into seq
        start-loc (rand-nth (keys paths))] ; keys paths contains all locations, so rand-th takes any location
    (loop [walls walls
           unvisited (disj (set (keys paths)) start-loc)] ; unvisited set is easier to write code with
      (if-let [loc (when-let [s (seq unvisited)] (rand-nth s))] ; seq makes sure unvisited is not empty and rand-nth can access it
        (let [walk (iterate (comp rand-nth paths) loc) ; generates infinite random walks of unvisited
              steps (zipmap (take-while unvisited walk) (next walk))] ; take-while unvisited walk stops random walk at visited location, and next walk is infinite but it is zipped with finite seq
          (recur (reduce disj walls (map set steps)) ; map set steps converts directions into walls
                 (reduce disj unvisited (keys steps))))
        walls))))

; define function to create fully walled maze
(defn grid
  [w h]
  (set (concat
         (for [i (range (dec w)) j (range h)] #{[i j] [(inc i) j]})
         (for [i (range w) j (range (dec h))] #{[i j] [i (inc j)]}))))

; define a draw function AND UPDATE
(defn draw
  [w h maze path]
  (doto (javax.swing.JFrame. "Maze")
    (.setContentPane
      (doto (proxy [javax.swing.JPanel] []
              (paintComponent [^java.awt.Graphics g]
                (let [g (doto ^java.awt.Graphics2D (.create g)
                          (.scale 10 10)
                          (.translate 1.5 1.5)
                          (.setStroke (java.awt.BasicStroke. 0.4)))]
                  (.drawRect g -1 -1 w h)
                  (doseq [[[xa ya] [xb yb]] (map sort maze)]
                    (let [[xc yc] (if (= xa xb)
                                    [(dec xa) ya]
                                    [xa (dec ya)])]
                      (.drawLine g xa ya xc yc)))
                  (.translate g -0.5 -0.5)
                  (.setColor g java.awt.Color/RED)
                  (doseq [[[xa ya] [xb yb]] path] ; path is collection of pairs, so draw it!
                    (.drawLine g xa ya xb yb)))))
        (.setPreferredSize (java.awt.Dimension.
                             (* 10 (inc w)) (* 10 (inc h))))))
    .pack
    (.setVisible true)))
; black magic ^^^

; draw a maze
(draw 80 80 (maze (grid 80 80)))

; modified maze into wmaze
(defn wmaze
  "The original Wilson's algorithm"
  [walls]
  (let [paths (reduce (fn [index [a b]] ; paths is map of locations to adjancent locations
                        (merge-with into index {a [b] b [a]}))
                      {} (map seq walls)) ; map seq walls converts walls into seq
        start-loc (rand-nth (keys paths))] ; keys paths contains all locations, so rand-th takes any location
    (loop [walls walls
           unvisited (disj (set (keys paths)) start-loc)] ; unvisited set is easier to write code with
      (if-let [loc (when-let [s (seq unvisited)] (rand-nth s))] ; seq makes sure unvisited is not empty and rand-nth can access it
        (let [walk (iterate (comp rand-nth paths) loc) ; generates infinite random walks of unvisited
              steps (zipmap (take-while unvisited walk) (next walk)) ; take-while unvisited walk stops random walk at visited location, and next walk is infinite but it is zipped with finite seq
              walk (take-while identity (iterate steps loc)) ; retraces only one branch of random walk
              steps (zipmap walk (next walk))] ; turns path into a map of [from-loc to-loc] entries
          (recur (reduce disj walls (map set steps)) ; map set steps converts directions into walls
                 (reduce disj unvisited (keys steps))))
        walls))))

; draw a wmaze
(draw 80 80 (wmaze (grid 80 80)))

; define function to create fully inner walled hex maze
(defn hex-grid
  [w h]
  (let [vertices (set (for [y (range h) x (range (if (odd? y) 1 0) (* 2 w) 2)]
                        [x y]))
        deltas [[2 0] [1 1] [-1 1]]]
    (set (for [v vertices d deltas f [+ -]
               :let [w (vertices (map f v d))]
               :when w] #{v w}))))

; define function to create fully outer walled hex maze
(defn- hex-outer-walls
  [w h]
  (let [vertices (set (for [y (range h) x (range (if (odd? y) 1 0) (* 2 w) 2)]
                        [x y]))
        deltas [[2 0] [1 1] [-1 1]]]
    (set (for [v vertices d deltas f [+ -]
               :let [w (map f v d)]
               :when (not (vertices w))] #{v (vec w)}))))

; define anoter draw function
(defn hex-draw
  [w h maze]
  (doto (javax.swing.JFrame. "Maze")
    (.setContentPane
      (doto (proxy [javax.swing.JPanel] []
              (paintComponent [^java.awt.Graphics g]
                (let [maze (into maze (hex-outer-walls w h))
                      g (doto ^java.awt.Graphics2D (.create g)
                          (.scale 10 10)
                          (.translate 1.5 1.5)
                          (.setStroke (java.awt.BasicStroke. 0.4
                                                             java.awt.BasicStroke/CAP_ROUND
                                                             java.awt.BasicStroke/JOIN_MITER)))
                      draw-line (fn [[[xa ya] [xb yb]]]
                                  (.draw g
                                    (java.awt.geom.Line2D$Double.
                                      xa (* 2 ya) xb (* 2 yb))))]
                  (doseq [[[xa ya] [xb yb]] (map sort maze)]
                    (draw-line
                      (cond
                        (= ya yb) [[(inc xa) (+ ya 0.4)] [(inc xa) (- ya 0.4)]]
                        (< ya yb) [[(inc xa) (+ ya 0.4)] [xa (+ ya 0.6)]]
                        :else [[(inc xa) (- ya 0.4)] [xa (- ya 0.6)]])))))) ; it's a rainbow!!
        (.setPreferredSize (java.awt.Dimension.
                             (* 20 (inc w)) (* 20 (+ 0.5 h)))))) ; so many colors in my IDE
    .pack
    (.setVisible true)))
; this drains my energy so quickly...

; draw a hex
(hex-draw 40 40 (maze (hex-grid 40 40)))
; use zipper api
(require '[clojure.zip :as z])
(def v [[1 2 [3 4]] [5 6]])
(-> v z/vector-zip z/node) ; [[1 2 [3 4]] [5 6]]
(-> v z/vector-zip z/down z/node) ; [1 2 [3 4]]
(-> v z/vector-zip z/down z/right z/node) ; [5 6]

; create new zipper nodes
(-> v z/vector-zip z/down z/right (z/replace 56) z/node) ; 56
(-> v z/vector-zip z/down z/right (z/replace 56) z/root) ; [[1 2 [3 4]] 56]
(-> v z/vector-zip z/down z/right z/remove z/node) ; 4
(-> v z/vector-zip z/down z/right z/remove z/root) ; [[1 2 [3 4]]]
(-> v z/vector-zip z/down z/down z/right (z/edit * 42) z/root) ; [[1 84 [3 4]] [5 6]]


; set up thesus myth
(def labyrinth
  (let [g (grid 10 10)] (reduce disj g (maze g))))
(def thesus (rand-nth (distinct (apply concat labyrinth))))
(def minotaur (rand-nth (distinct (apply concat labyrinth))))

; create custom zipper
(defn ariadne-zip
  [labyrinth loc]
  (let [paths (reduce (fn [index [a b]]
                        (merge-with into index {a [b] b [a]}))
                      {} (map seq labyrinth))
        children (fn [[from to]]
                   (seq (for [loc (paths to) ; require children to return a seq
                              :when (not= loc from)]
                          [to loc])))]
    (z/zipper (constantly true) ; all locations might have children
              children
              nil ; nil cannot perform updates
              [nil loc]))) ; nil loc is root

; perform depth-first walk of the maze
(->> thesus
  (ariadne-zip labyrinth)
  (iterate z/next)
  (filter #(= minotaur (second (z/node %))))
  first z/path
  (map second)) ; ([8 5][8 4][8 3][7 3][6 3][6 2][5 2][4 2][3 2][2 2][2 3][1 3][0 3][0 4][0 5][1 5])

; tell a myth with Clojure
(let [w 80
      h 80
      grid (grid w h)
      walls (maze grid)
      labyrinth (reduce disj grid walls)
      places (distinct (apply concat labyrinth))
      thesus (rand-nth places)
      minotaur (rand-nth places)
      path (->> thesus
             (ariadne-zip labyrinth)
             (iterate z/next)
             (filter #(= minotaur (first (z/node %))))
             first z/path rest)] ; replaces map second because first pair of locations is wierd
  (draw w h walls path))