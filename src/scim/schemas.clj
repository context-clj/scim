(ns scim.schemas
  (:require
   [pg]
   [pg.repo]
   [http]
   [uui]
   [clojure.string :as str]
   [scim.repo]))

;;models

;;ui
(defn index [context request]
  (let [schemas (scim.repo/search-resource context {:resourceType "schema"})]
    (uui/layout
     context request
     [:div
      [:h1 "Schemas"]
      (for [s schemas]
        [:div.uui-hbox
         [:a.uui-link {:href (str "/scim/ui/Schemas/" (:id s))}
          (:name s)]
         [:span (:id s)]
         #_[:pre (pr-str (dissoc s :name :id))]]
        )])))

(defn show [context {{id :id} :route-params :as request}]
  (let [sch (scim.repo/read-resource context {:resourceType "schema" :id id})]
    (uui/layout
     context request
     [:div
      [:h1.uui-h1 (:name sch)]
      [:h2.uui-h2 (:id sch)]
      [:table.uui-table {:class "text-sm"}
       [:tbody
        (for [at (:attributes sch)]
          [:tr
           [:td [:b (:name at)]
            (when (:multiValued at) "[]")
            (when (:required at) [:span {:class "text-red-500"} " *"])
            ]
           [:td (:uniqueness at)]
           [:td (:type at)]
           [:td (:mutability at)]
           [:td (:returned at)]
           ;; [:td (pr-str (:subAttributes at))]
           [:td {:class "text-xs text-gray-600"} (:description at)]
           ;; [:td (pr-str at)]
           ])]]])))

(defn create [context request])
(defn update [context request])
(defn delete [context request])

(defn mount-routes [context]
  (http/register-endpoint context {:method :get :path "/scim/ui/Schemas" :fn #'index :uui.menu/title "Schemas"})
  (http/register-endpoint context {:method :get :path "/scim/ui/Schemas/:id" :fn #'show})
  )

(comment
  (def context scim/context)


  )
