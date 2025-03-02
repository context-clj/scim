(ns scim.validator
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn -validate [errors schema resource]

  )

(defn validate [schema resource]
  (-validate [] schema resource))

