(ns chapter_one.core)

; Takes the average of some numbers
(defn average
  [numbers]
  (/ (apply + numbers) (count numbers)))