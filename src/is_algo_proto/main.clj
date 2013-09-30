(ns is-algo-proto.main
  (:gen-class))

(require '[is-algo-proto.core :as core])

(defn -main
  []
  (println (str (core/read-json-file "resources/test_dispatch_one.json"))))