(ns is-algo-proto.main
  (:gen-class))

(require '[is-algo-proto.core :as core])

(defn test-dispatch-one []
  (core/read-json-file "resources/test_dispatch_one.json"))

(def td (test-dispatch-one))

(defn -main
  []
  ;(println (str (core/read-json-file "resources/test_dispatch_one.json"))))
  (println (core/svg-schedule td)))
