(ns chapter-two-core)

; evalutes to true every time
(= 5 5)
(= 5 (+ 2 3))
(= "boot" (str "bo" "ot"))
(= nil nil)
(let [a 5]
  (- 2 a)
  (= a 5))

; demonstrate immutability
(def h {[1 2] 3})
(h [1 2])
(conj (first (keys h)) 3)
(h [1 2])
h

; call a function twice
(defn call-twice [f x]
 (f x)
 (f x))
(call-twice println 123)

; call functions as first class objects
(max 5 6)
(require 'clojure.string)
(clojure.string/lower-case "Clojure")
; use map function
(map clojure.string/lower-case ["Java" "Imperative" "Weeping" "Clojure" "Learning" "Peace"])
(map * [1 2 3 4 5] [5 6 7 8 9])

; use reduce function
(reduce max [0 -3 10 48])
(reduce + 50 [1 2 3 4])
(reduce
  (fn [m v]
    (assoc m v (* v v)))
  {}
  [1 2 3 4])

; Use function application
(apply hash-map [:a 5 :b 6])
(def only-strings (partial filter string?))
(only-strings ["a" 5 "b" 6])

; Define a function literal which forces all arguments to be specified
(#(filter string? %) ["a" 5 "b" 6])
(#(filter % ["a" 5 "b" 6]) number?)

; Use function composition
(def negated-sum-str (comp str - +))
(negated-sum-str 10 12 3.4)
(require '[clojure.string :as str])
(def camel->keyword (comp keyword
                          str/join
                          (partial interpose \-)
                          (partial map str/lower-case)
                          #(str/split % #"(?<=[a-z])(?=[A-Z])")))
(camel->keyword "CamelCase")
(camel->keyword "lowerCamelCase")

; Define some higher order functions
(defn adder
  [x]
  (fn [y] (+ x y)))
((adder 5) 18)
(defn doubler
  [f]
  (fn [& args]
    (* 2 (apply f args))))
(def double-+ (doubler +))
(double-+ 1 2 3)
; Define some logger that redirects standard output
(defn print-logger
  [writer]
  #(binding [*out* writer]
     (println %)))

; Define logger to standard output and use it
(def *out*-logger (print-logger *out*))
(*out*-logger "hello")

; Define logger to in-memory buffer
(def writer (java.io.StringWriter.))
(def retained-logger (print-logger writer))
(retained-logger "hello")
(str writer)

; Define logger to file system
(require `clojure.java.io)
(defn file-logger
  [file]
  #(with-open [f (clojure.java.io/writer file :append true)]
     ((print-logger f) %)))
(def log->file (file-logger "messages.log"))
(log->file "hello")

; Define logger to accept a sequence of logging functions
(defn multi-logger
  [& logger-fns]
  #(doseq [f logger-fns]
     (f %)))

; Define logger to standard output and file system
(def log (multi-logger
           (print-logger *out*)
           (file-logger "messages.log")))
(log "hello again")

; Define logger to timestamp its parameter logger
(defn timestamped-logger
  [logger]
  #(logger (format "[%1$tY-%1$tm-%1$te %1$tH:%1$tM:%1$tS] %2$s" (java.util.Date.) %)))
(def log-timestamped (timestamped-logger
                       (multi-logger
                         (print-logger *out*)
                         (file-logger "messages.log"))))
(log-timestamped "goodbye, now")

; Use memoization with prime functions
(defn prime?
  [n]
  (cond
    (== 1 n) false
    (== 2 n) true
    (even? n) false
    :else (->> (range 3 (inc (Math/sqrt n)) 2)
            (filter #(zero? (rem n %)))
            empty?)))
(time (prime? 1125899906842679))
(let [m-prime? (memoize prime?)]
  (time (m-prime? 1125899906842679)) ; Elapsed time: 3421.49153 msecs
  (time (m-prime? 1125899906842679))) ; Elapsed time: 0.0536 msecs