(ns scim.schemas
  (:require
   [pg]
   [pg.repo]
   [http]
   [uui]
   [uui.heroicons :as ico]
   [clojure.string :as str]
   [cheshire.core]
   [scim.repo]))

;;models

;;ui
(defn index [context request]
  (let [schemas (scim.repo/search-resource context {:resourceType "schema"})]
    (uui/layout
     context request
     [:div
      [:h1.uui-h1 "Schemas"]
      [:table.uui-table {:class "mt-4 text-sm"}
       [:thead
        [:tr [:th "name"] [:th "id"] [:th "description"]]]
       [:tbody
        (for [s schemas]
          [:tr
           [:td
            [:a.uui-link {:href (str "/scim/ui/Schemas/" (:id s))}
             (:name s)]]
           [:td (:id s)]
           [:td {:class "text-xs text-gray-500"}(:description s)]])]]])))

(defn type-icon [tp]
  (case tp
    "string"    (ico/pencil-square "size-4" :outline)
    "complex"   (ico/cube "size-4" :outline)
    "boolean"   (ico/question-mark-circle "size-4" :outline)
    "decimal"   (ico/numbered-list "size-4" :outline)
    "integer"   (ico/numbered-list "size-4" :outline)
    "dateTime"  (ico/calendar "size-4" :outline)
    "reference" (ico/link "size-4" :outline)
    "binary"    (ico/video-camera "size-4" :outline)
    ))

(defn attribute-rows [at & [ident]]
  (let [ident (or ident 0)]
    (into
     [[:tr {:class "even:bg-gray-50"}
       [:td {:class "flex space-x-2 items-center pl-6"}
        (for [i (range ident)]
          [:div {:class "w-4"}])
        [:div {:class "text-gray-400"} (type-icon (:type at))]
        [:div {:class "whitespace-nowrap"}
         [:b (:name at)]
         (when (:multiValued at) [:b "[...]"])
         (when (:required at) [:span {:class "text-red-500"} " *"])
         (when-not (= "none" (:uniqueness at)) (:uniqueness at))]]
       [:td (:type at)]
       [:td (:mutability at)]
       [:td (:returned at)]
       [:td {:class "text-xs text-gray-600"}
        (:description at)
        (when-let [vs (:canonicalValues at)]
          [:ul {:class "ml-4 list-disc"}
           (for [v vs]
             [:li v])])]]]
     (->> (:subAttributes at)
          (mapcat (fn [sat] (attribute-rows sat (inc ident))))))))

(defn show [context {{id :id} :route-params :as request}]
  (let [sch (scim.repo/read-resource context {:resourceType "schema" :id id})
        tabs (uui/tabs request
              "Schema" (fn []
                         [:table.uui-table {:class "text-sm border border-gray-200"}
                          [:tbody (mapcat attribute-rows (:attributes sch))]])
              "JSON" (fn []
                       [:pre.uui-code
                        (cheshire.core/generate-string sch {:pretty true})]))]
    (if (uui/hx-target request)
      (uui/response tabs)
      (uui/layout
       context request
       [:div
        (uui/breadcramp ["/scim/ui/Schemas" "Schemas"] ["#" (:id sch)])
        [:h1.uui-h1 {:class "mt-4"} (:name sch)]
        [:p.uui-text {:class "my-4"} (:description sch)]
        tabs]))))

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
