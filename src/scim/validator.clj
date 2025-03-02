(ns scim.validator
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn -validate-attrs [errors attrs resource]
  (let [attrs (group-by :name attrs)
        reqs (->> attrs (filter :required))]

    ))

(defn validate [schema resource]
  (-validate-attrs [] (:attributes schema) resource))

