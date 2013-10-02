(ns is-algo-proto.main
  (:gen-class)
  (:use clojure.contrib.command-line))

(require '[is-algo-proto.core :as core])

(defn test-dispatch-one []
  (core/read-json-file "resources/test_dispatch_one.json"))

(def td (test-dispatch-one))

(defn -main
  [& args]
  (with-command-line args
    "Usage: isalgo [-f json|svg] -i input.json -o output.json|svg"
    [[input i "name of the input dispatch file"]
     [format f "output format" "json"]
     [output o "output filename"]]
     (let [scheduled-dispatch (core/task-schedule (core/read-json-file input))]
       (case format
        "svg" (core/svg-schedule scheduled-dispatch)
        "json" (core/scheduled-dispatch-to-json-file scheduled-dispatch output)))))
