(ns scim.repo
  (:require
   [system]
   [http]
   [pg]
   [pg.repo]
   [cheshire.core]
   [clojure.java.io :as io]
   [scim.ui]))

(defn read-schema [schema-name]
  (cheshire.core/parse-string (slurp (io/resource (str "scim/schemas/" schema-name ".json"))) keyword))

(def resource-schemas ["User" "Group" "ResourceType" "Schema" "SearchRequest" "ServiceProviderConfig"])
(def extra-schemas ["EnterpriseUser"])

(defn load-deaults [context]
  (doseq [s (concat resource-schemas extra-schemas)]
    (let [res (read-schema s)]
      (pg.repo/upsert context {:table :scim.schema :resource (assoc res :readOnly true)}))))

;; TODO: do we need history?

(defn search-resource
  [context {rt :resourceType}]
  (assert rt)
  (pg.repo/select context {:table (str "scim." rt)}))

(defn read-resource
  [context {rt :resourceType id :id}]
  (assert (and rt id))
  (pg.repo/read context {:table (str "scim." rt) :match {:id id}}))

(defn delete-resource   [context {rt :resourceType {id :id} :resource}])
(defn validate-resource [context {rt :resourceType res :resource}])
(defn create-resource   [context {rt :resourceType res :resource}])
(defn update-resource   [context {rt :resourceType res :resource}])
(defn patch-resource    [context {rt :resourceType res :resource}])


(comment
  (def context scim/context)

  (search-resource context {:resourceType "schema"})

  )
