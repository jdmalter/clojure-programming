(ns chapter_one.core-test
  (:require [clojure.test :refer :all]
            [chapter_one.core :refer :all]))

; Check that the average of [60 80 100 400] is 160
(deftest average-test
  (testing "FIXME, I fail."
    (is (= (average [60 80 100 400]) 160))))
