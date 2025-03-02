(ns scim.ui
  (:require [http]
            [uui]))


(system/defmanifest
  {:description "SCIM"
   :deps ["pg" "pg.repo" "http"]
   :config {:history {:type "boolean"}}})
