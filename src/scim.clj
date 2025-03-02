(ns scim
  (:require
   [system]
   [http]
   [pg]
   [pg.repo]
   [cheshire.core]
   [uui]
   [uui.heroicons :as ico]
   [clojure.java.io :as io]
   [scim.repo]
   [scim.ui]))

(system/defmanifest
  {:description "SCIM"
   :deps ["pg" "pg.repo" "http"]
   :config {:history {:type "boolean"}}})

(def search-resource scim.repo/search-resource)

(defn search-resource-api  [context request]
  {:status 200})

(defn mount-routes [context]
  (http/register-endpoint context {:method :get :path "/scim/v2/:resource-type" :fn #'search-resource-api}))

(system/defstart
  [context config]
  (pg/migrate-prepare context)
  (pg/migrate-up context)
  (scim.repo/load-deaults context)
  (mount-routes context)
  {})

(def default-config
  {:services ["pg" "pg.repo" "http" "uui" "scim" "scim.ui"]
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
