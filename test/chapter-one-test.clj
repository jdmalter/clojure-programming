(ns chapter_one.core-test
  (:require [clojure.test :refer :all]
            [chapter_one.core :refer :all]))

; Check that the average of [60 80 100 400] is 160
(deftest average-test
  (testing "FIXME, I fail."
    (is (= (average [60 80 100 400]) 160))))

; Check that read-string returns a literal
(deftest read-string-test
  (testing "FIXME, I fail."
    (is (= (read-string "42") 42))))

; Check that pr-str returns a string
(deftest pr-str-test
  (testing "FIXME, I fail."
    (is (= (pr-str [1 2 3]) "[1 2 3]"))))

; Check that a keyword is properly declared
(deftest keyword-test
  (testing "FIXME, I fail."
    (is (= (:name person) "Jacob Malter"))))

; Check that there is naming collision
(deftest namespace-test
  (testing "FIXME, I fail."
    (is (= (:chapter_one.core/location calzones) "35.779385, -78.675592"))))

; Check that comma evalues to whitespace
(deftest comma-test
  (testing "FIXME, I fail."
    (is (= [1 2 3] [,,,1, 2,,, ,,,, 3,,,,,,,,,,,,,,]))))