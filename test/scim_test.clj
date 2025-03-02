(ns scim-test
  (:require
   [clojure.test :as t]
   [system]
   [scim :as subj]
   [test-util :as tu]
   [matcho.core :as matcho]))


(t/deftest test-scim
  (tu/ensure-system)

  (comment
    (tu/execute! {:sql "truncate scim.resourcetype; truncate scim.schema;"})

    (tu/restart-system)
    )

  (matcho/match
      (subj/search-resource tu/context {:resourceType "schema" :attributes [:name :id]})
    [{:name "User", :id "urn:ietf:params:scim:schemas:core:2.0:User"}
     {:name "Group", :id "urn:ietf:params:scim:schemas:core:2.0:Group"}
     {:name "ResourceType", :id "urn:ietf:params:scim:schemas:core:2.0:ResourceType"}
     {:name "Schema", :id "urn:ietf:params:scim:schemas:core:2.0:Schema"}
     {:name "SearchRequest", :id "urn:ietf:params:scim:api:messages:2.0:SearchRequest"}
     {:name "Service Provider Configuration", :id "urn:ietf:params:scim:schemas:core:2.0:ServiceProviderConfig"}
     {:name "EnterpriseUser", :id "urn:ietf:params:scim:schemas:extension:enterprise:2.0:User"}])

  (tu/execute! {:sql "select * from scim.resourcetype"})

  (tu/execute! {:sql "select id from scim.schema"})

  (subj/search-resource tu/context {:resourceType "resourcetype"})
  (mapv #(select-keys % [:name :id]) (subj/search-resource tu/context {:resourceType "schema"}))

  )

