(ns is-algo-proto.core
  (:use [analemma.charts :only [emit-svg xy-plot add-points]]
    [analemma.svg]
    [analemma.xml]
    [clojure.java.io :only [file]]))

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
  (loop [s (sorted-schedule (dispatch :schedule)) t (start-time dispatch) free ()]
    (if (empty? s)
      free
      (if (= t (start-time (first s)))
        (recur (rest s) (end-time (first s)) free)
        (recur (rest s) (end-time (first s)) (conj free {:start t :end (end-time (first s))}))))))

(defn sorted-tasks [dispatch]
  (reverse
    (sort-by #(task-importance %1 (start-time dispatch)) (dispatch :tasks))))

(defn task-schedule [dispatch]
  (let [free (free-times dispatch) tasks (sorted-tasks dispatch)]))

(defn scaler [domain-min domain-max range-min range-max]
  (fn [val]
    (let [dom (- domain-max domain-min)
          ran (- range-max range-min)]
      (-> val 
        (- domain-min)
        (* ran)
        (/ dom)
        (+ range-min)
        float))))

(defn svg-schedule [dispatch]
  (let [entry-width 400
        box-height 2000
        s (scaler (start-time dispatch) (end-time dispatch) 0 box-height)]
    (emit
      (svg
        (apply group
          (concat
            (map
              #(rect
                0
                (s (start-time %1))
                (- (s (end-time %1)) (s (start-time %1)))
                entry-width
                :fill "#E62E00")
              (dispatch :schedule))
            (map
              #(-> (text {:x 0 :y (+ (s (start-time %1)) 15)} (%1 :name))
                 (style :fill "#000066"
                        :font-family "Garamond"
                        :font-size "15px"
                        :alignment-baseline :middle))
              (dispatch :schedule))))))))
