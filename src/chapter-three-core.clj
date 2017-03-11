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