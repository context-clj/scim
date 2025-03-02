(ns scim
  (:require [system]
            [http]
            [pg]
            [pg.repo]
            [cheshire.core]
            [uui]
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

(defn index [context request]
  (uui/boost-response
   context request
   [:div
    [:h1 "Hello"]
    [:br]
    [:a {} "ResourceTypes"]
    [:br]
    [:a {} "Schemas"]

    ]))

(defn mount-routes [context]
  (http/register-endpoint context {:method :get :path "/scim/v2/:resource-type" :fn #'search-resource-api})
  (http/register-endpoint context {:method :get :path "/" :fn #'index})

  )

(system/defstart
  [context config]
  (pg/migrate-prepare context)
  (pg/migrate-up context)
  (load-deaults context)
  {})

(def default-config
  {:services ["pg" "pg.repo" "http" "uui" "scim"]
   :http {:port 8885}})


(comment
  (require '[pg.docker :as pgd])
  (require '[system.dev :as dev])

  (dev/update-libs)

  (pgd/delete-pg "scim-pg")

  (def pg-config (pgd/ensure-pg "scim-pg"))

  (def context (system/start-system (assoc default-config :pg pg-config)))
  (system/stop-system context)

  (mount-routes context)



  (pg/execute! context {:sql ["select 1"]})

  (pg/migrate-prepare context)

  (pg/migrate-up context)
  (pg/migrate-down context)

  (pg/execute! context {:sql ["select * from scim.schema"]})

  (pg.repo/clear-table-definitions-cache context)

  (->> (pg.repo/select context {:table :scim.schema})
       (mapv :id))

  (pg.repo/select context {:table :scim.resourcetype})

  )
