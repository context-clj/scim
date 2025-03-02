(ns scim.validator-test
  (:require
   [clojure.test :as t]
   [scim.validator :as subj]
   [matcho.core :as matcho]))

(defmacro match-validate [sch res pat]
  `(let [r# (subj/validate ~sch ~res)]
     (matcho/match r# ~pat)
     r#))

(t/deftest test-scim-validator

  (match-validate
   {:attributes [{:name "a" :type "string" :required true}]} {}
   [{:type "required", :path ["a"]}])

  (match-validate
   {:attributes [{:name "a" :type "string" :required true}]} {:a "a"}
   empty?)

  (match-validate
   {:attributes [{:name "a" :type "string" :required true}]} {"a" "a"}
   empty?)

  (match-validate
   {:attributes [{:name "a" :type "string" :required true}]} {:a "a" :ups 1}
   [{:type "unknown", :path ["ups"]}] )

  (match-validate
   {:attributes [{:name "a" :type "string" :required true}]} {:a 1}
   [{:type "type", :path ["a"]}])

  (match-validate
   {:attributes [{:name "a" :type "string" :multiValued true}]} {:a "a"}
   [{:type "multiValued", :path ["a"]}])

  (match-validate
   {:attributes [{:name "a" :type "string" :multiValued true}]} {:a [1]}
   [{:type "type", :path ["a" 0], :expected "string", :got "integer"}])

  (match-validate
   {:attributes [{:name "a" :type "string" :multiValued true}]} {:a ["a" 1]}
   [{:type "type", :path ["a" 1], :expected "string", :got "integer"}])


  (match-validate
   {:attributes [{:name "a" :type "string" :canonicalValues ["x" "y"]}]} {:a "x"}
   empty?)

  (match-validate
   {:attributes [{:name "a" :type "string" :canonicalValues ["x" "y"]}]} {:a "b"}
   [{:type "canonicalValues", :path ["a"], :expected ["x" "y"], :got "b"}])

  (match-validate
   {:attributes [{:name "a" :type "string" :canonicalValues ["x" "y"]}]} {:a "b"}
   [{:type "canonicalValues", :path ["a"], :expected ["x" "y"], :got "b"}])

  (match-validate
   {:attributes [{:name "a" :type "complex" :subAttributes [{:name "b" :type "string"}]}]} {:a "b"}
   [{:type "type", :path ["a"], :expected "complex", :got "string"}])

  (match-validate
   {:attributes [{:name "a" :type "complex" :subAttributes [{:name "b" :type "string"}]}]} {:a {:b 1}}
   [{:type "type", :path ["a" "b"], :expected "string", :got "integer"}])

  (match-validate
   {:attributes [{:name "a" :type "complex" :subAttributes [{:name "b" :type "string"}]}]} {:a {:b "b"}}
   empty?)

  )
