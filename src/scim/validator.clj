(ns scim.validator
  (:require [clojure.string :as str]
            [clojure.set :as set]))

(defn get-key [m k]
  (or (get m k) (get m (keyword k))))

(declare -validate)

(defmulti validate-rule
  (fn [errors path schema-rule schema-value attr-name attr-value]
    (keyword schema-rule)))

(defn value-type [value]
  (cond
    (string? value) "string"
    (int? value) "integer"
    (number? value) "number"
    (map? value) "complex"
    :else (str (type value))))

(defmethod validate-rule
  :type
  [errors path _schema-rule schema-value attr-name attr-value]
  (let [vtype (value-type attr-value)]
    (if-not (= vtype schema-value)
      (conj errors {:type "type" :path path :expected schema-value :got vtype})
      errors)))

(defmethod validate-rule
  :canonicalValues
  [errors path _schema-rule schema-value attr-name attr-value]
  (if-not (some #(= % attr-value) schema-value)
    (conj errors {:type "canonicalValues" :path path :expected schema-value :got attr-value})
    errors))

(defmethod validate-rule
  :subAttributes
  [errors path _schema-rule schema-value attr-name attr-value]
  (if (map? attr-value)
    (-validate errors path schema-value attr-value)
    errors))

(defmethod validate-rule
  :default
  [errors path schema-rule schema-value attr-name attr-value]
  errors)

(defn validate-attribute [errors path attr-schema attr-name attr-value]
  (if (:multiValued attr-schema)
    (if (not (sequential? attr-value))
      (conj errors {:type "multiValued" :path path})
      (->> attr-value
           (map-indexed (fn [idx attr-value] [idx attr-value]))
           (reduce (fn [errors [idx attr-value]]
                     (->> (select-keys attr-schema [:type])
                          (reduce (fn [errors [schema-rule schema-value]]
                                    (validate-rule errors (conj path idx) schema-rule schema-value attr-name attr-value))
                                  errors)))
                   errors)))
    (->> attr-schema
         (reduce (fn [errors [schema-rule schema-value]]
                   (validate-rule errors path schema-rule schema-value attr-name attr-value))
                 errors))))

(defn validate-requireds [errors path reqs resource]
  (->> reqs
       (reduce (fn [errors {nm :name}]
                 (if (nil? (get-key resource nm))
                   (conj errors {:type "required" :path (conj path nm)})
                   errors))
               errors)))

(defn validate-attributes [errors path attrs-map resource]
  (->> resource
       (reduce
        (fn [errors [k v]]
          (if-let [key-schema (get-in attrs-map [(name k) 0])]
            (validate-attribute errors (conj path (name k)) key-schema k v)
            (conj errors {:type "unknown" :path (conj path (name k))})))
        errors)))

(defn -validate [errors path attrs resource]
  (let [attrs-map (group-by :name attrs)
        reqs (filter :required attrs)
        errors (validate-requireds errors path reqs resource)
        errors (validate-attributes errors path attrs-map resource)]
    errors))

(defn validate [schema resource]
  (-validate [] [] (:attributes schema) resource))

