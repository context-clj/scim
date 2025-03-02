(ns scim
  (:require [system]
            [http]
            [pg]
            [pg.repo]
            [cheshire.core]
            [clojure.java.io :as io]))


(system/defmanifest
  {:description "SCIM"
   :deps ["pg" "pg.repo" "http"]
   :config {:history {:type "boolean"}}})


(defn read-schema [schema-name]
  (cheshire.core/parse-string (slurp (io/resource (str "scim/schemas/" schema-name ".json"))) keyword))

(def resource-schemas ["User" "Group" "ResourceType" "Schema" "SearchRequest" "ServiceProviderConfig"])
(def extra-schemas ["EnterpriseUser"])

(comment
  (read-schema "User")

  )

(defn load-deaults [context]
  (doseq [s (concat resource-schemas extra-schemas)]
    (let [res (read-schema s)]
      (pg.repo/upsert context {:table :scim.schema :resource (assoc res :readOnly true)}))))

;; TODO: do we need history?

(defn read-resource     [context {rt :resourceType {id :id} :resource}])
(defn delete-resource   [context {rt :resourceType {id :id} :resource}])
(defn validate-resource [context {rt :resourceType res :resource}])
(defn create-resource   [context {rt :resourceType res :resource}])
(defn update-resource   [context {rt :resourceType res :resource}])
(defn patch-resource    [context {rt :resourceType res :resource}])

(defn search-resource
  [context {rt :resourceType}]
  (pg.repo/select context {:table (str "scim." rt)}))

(defn search-resource-api  [context request]
  {:status 200})

(defn mount-routes [context]
  (http/register-endpoint
   context {:method :get
            :path "/scim/v2/:resource-type"
            :fn #'search-resource-api}))

(system/defstart
  [context config]
  (pg/migrate-prepare context)
  (pg/migrate-up context)
  (load-deaults context)
  {})

(def default-config
  {:services ["pg" "pg.repo" "scim"]
   :http {:port 8885}})

(comment
  (require '[pg.docker :as pgd])

  (pgd/delete-pg "scim-pg")

  (def pg-config (pgd/ensure-pg "scim-pg"))

  (def context (system/start-system (assoc default-config :pg pg-config)))

  (mount-routes context)

  (system/stop-system context)

  (pg/execute! context {:sql ["select 1"]})

  ;; (pg/generate-migration "init-scim")


  (def schemas (cheshire.core/parse-string (slurp (io/resource "scim-v2.json")) keyword))

  (keys schemas)

  (->> (:ServiceProviderConfigs schemas))

  (->> (:Resources (:Schemas schemas))
       ;; (first)
       (mapv :id))

  ;; scim.ResourceType <- create -> create table
  ;; scim.Schema
  ;; scim.User
  ;; scim.Group
  ;; -- scim.Membership


  (pg/migrate-prepare context)

  (pg/migrate-up context)
  (pg/migrate-down context)

  ;; (pg/migrate-down context "notebooks")


  (pg/execute! context {:sql ["select * from scim.schema"]})

  (pg.repo/clear-table-definitions-cache context)

  (->> (pg.repo/select context {:table :scim.schema})
       (mapv :id))

  (pg.repo/select context {:table :scim.resourcetype})



  ;; (pg/generate-migration "init")
  ;; (pg/generate-migration "notebooks")

  )
