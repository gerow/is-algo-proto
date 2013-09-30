(ns is-algo-proto.core
  (:gen-class))

(require '[clojure.data.json :as json])

(defn foo
  "I don't do a whole lot."
  [x]
  (println x "Hello, World!"))

(defn read-json-file [filename]
  (json/read-json (slurp filename)))
