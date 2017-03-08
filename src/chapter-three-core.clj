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
