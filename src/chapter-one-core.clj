(ns chapter-one-core)

; Takes the average of some numbers
(defn average
  [numbers]
  (/ (apply + numbers) (count numbers)))
(= (average [60 80 100 400]) 160)

; Read a string and return a literal
(read-string "42")
(= (read-string "42") 42)

; Read a string a return a string literal
(pr-str [1 2 3])
(= (pr-str [1 2 3]) "[1 2 3]")
; Declare a string literal
"hello world!"

; Declare a multiline string literal
"multiline stings
are easy to declare"

; Declare some character literal
(class \c) ;= java.lang.Character
\u00ff ;= \ÿ
\o41 ;= \!

; Declare a keyword which evaluates to itself
(def person {:name "Jacob Malter"
             :city "Raleigh, NC"})
(= (:name person) "Jacob Malter")

; Declare two keywords with the same name but in different namespaces
(def calzones {:name "DP Dough"
               :location "Raleigh, NC"
               ::location "35.779385, -78.675592"}) ; every tuesday
(= (:chapter_one.core/location calzones) "35.779385, -78.675592")

; Declare a regex
(def regex #"(\d+)-(\d+)")
(class regex)
(re-seq regex "1-3")


; Use the reader macro
(read-string "(+ 1 2 #_(* 2 2) 8)")

; Use comment
(comment (println "hello"))

; Demonstrate the commas evaluate to whitespace
(= [1 2 3] [1, 2, 3])
(= [1 2 3] [,,,1, 2,,, ,,,, 3,,,,,,,,,,,,,,])

; Show off literal data structure
'(a b :name 12.5) ;; list
['a 'b :name 12.5] ;; vector
{:name "Jacob" :age 20} ;; map
#{1 2 3} ;; set

; define a variable
(def x 1)
x ;= 1

; Demonstrate what eval does
(eval :foo) ;= :foo
(eval [1 2 3]) ;= [1 2 3]
(eval "text") ;= "text"