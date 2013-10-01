(ns is-algo-proto.core
  (:gen-class))

(require '[clojure.data.json :as json])
(use 'clj-time.format)
(require '[clj-time.coerce :as tc])

(defn read-json-file [filename]
  (json/read-json (slurp filename)))

(defn to-time [m]
  (tc/to-long (parse (formatters :date-hour-minute-second) m)))

(defn start-time [m]
  (to-time (m :start)))

(defn end-time [m]
  (to-time (m :end)))

(defn priority-factor [prio]
  (* 5 prio))

(defn time-til-due-factor [due begin-time]
  (let [ttd (- (to-time due) (to-time begin-time))]
    (* 5 ttd)))

(defn time-to-finish-factor [ttf]
  0)

(defn task-importance [task begin-time]
  (+ (priority-factor (task :priority))
     (time-til-due-factor (task :due) begin-time)
     (time-to-finish-factor (task :time))))

(defn sorted-schedule [schedule]
  (sort-by #(start-time %1) schedule))

; This function relies on schdeules not overlapping.
; In order to properly handle this it might be necessary
; to collapse down any overlapping schdeule events into
; one.  That, or we can modify this function.
(defn free-times [dispatch]
  (loop [s (dispatch :schedule) t (start-time dispatch) free ()]
    (if (empty? s)
      free
      (if (= t (start-time (first s)))
        (recur (rest s) (end-time (first s)) free)
        (recur (rest s) (end-time (first s)) (conj free {:start t :end (end-time (first s))}))))))