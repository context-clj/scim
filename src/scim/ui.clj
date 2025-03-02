(ns scim.ui
  (:require [http]
            [uui]
            [uui.heroicons :as ico]))

(system/defmanifest
  {:description "SCIM"
   :deps ["pg" "pg.repo" "http"]
   :config {:history {:type "boolean"}}})

(defn index [context request]
  (uui/boost-response
   context request
   [:div {:class "flex"}
    (uui/menu-button context request)
    [:div {:class "px-6 py-4 flex-1"}
     [:h1 "Hello" (ico/folder "mx-4 size-6 inline")]
     [:br]
     [:a {} "ResourceTypes"]
     [:br]
     [:a {} "Schemas"]

     ]]))

(defn mount-routes [context]
  (http/register-endpoint context {:method :get :path "/" :fn #'index}))

(system/defstart
  [context config]
  (mount-routes context)
  {})

(comment
  (def context scim/context)

  )
