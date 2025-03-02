(ns test-util
  (:require
   [clojure.test :as t]
   [system]
   [pg]
   [pg.repo]
   [scim :as scim]
   [pg.docker :as pgd]))

(defonce system-context (atom nil))
(declare context)

(defn ensure-system []
  (or @system-context
      (let [pg-config (pgd/ensure-pg "scim-pg-test")]
        (def context (system/start-system (assoc scim/default-config :pg pg-config :http {:port 8990})))
        (reset! system-context context)))
  :ok)

(defn restart-system []
  (when-let [sys @system-context]
    (system/stop-system sys)
    (def context nil)
    (reset! system-context nil)
    (ensure-system)))

(defn execute! [opts]
  (pg/execute! context opts))

(comment
  (ensure-system)

  (restart-system)

  (execute! {:sql "select 1"})

  )
