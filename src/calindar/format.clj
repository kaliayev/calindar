(ns calindar.format
  (require [clojure.string :as str]
           [io.aviso.ansi :as colour]
           [hiccup.page :refer [html5 include-css]]))

(def trim-width 80)

(def horizontal-line
  (apply str (repeat 80 "-")))

(defn trunc
  [s n]
  (subs s 0 (min (count s) n)))

(def line-breaks #{\- \space})

(defn adj-break?
  [s i]
  (try (if (or (contains? line-breaks (nth s i))
               (contains? line-breaks (nth s (dec i))))
         true
         false)
       (catch Exception e true)))

(defn line-breaker
  [s width]
  (if (< (count s) (- width 1))
    s
    (str (trunc s width)
         (if (adj-break? s width) "\n" "-\n")
         (line-breaker (subs s width) width))))

(defn cap [text]
  (let [str-l (- (count text) 3)]
    (str "+" (apply str (repeat str-l "-")) "+\n")))

(defn header-frame
  [s curl?]
  (let [source-title (str "| " (str/upper-case s) " |\n") 
        formatted-title (str "| " (colour/bold-red (str/upper-case s)) " |\n") 
        cap (cap source-title)]
    (str "\n" cap (if curl? formatted-title source-title) cap)))

(defn browser-format
  [t]
  (str "\n\n"
       (:time t) " - " (str/upper-case (:name t))
       "\n\n"(or (line-breaker (str "  " (:description t)) (- trim-width 3)) "")
       "\n\n"))

(defn browser-page
  [todo-list]
  (html5 {:lang "en"}
         [:body [:pre todo-list]]))

(defn curl-format
  [t]
  (str "\n\n"
       (colour/bold-magenta (:time t)) " - " (colour/bold-cyan (str/upper-case (:name t)))
       "\n\n" (or (line-breaker (str "   " (:description t)) (- trim-width 3)) "")
       "\n\n"
       horizontal-line))

(defn org-mode-format
  [t]
  (str "** TODO " (:name t) "\n" (:time t) "\n\n"
       (str (line-breaker (:description t) (- trim-width 3)) "\n\n")))

(defn format-by-ua
  [ua date todos]
  (let [curl? (or (re-find #"curl" ua) (re-find #"wget" ua))
        date (format "%s-%s-%s" (:year date) (:month date) (:day date))
        format-todo (if curl? curl-format browser-format)
        todo-list (reduce (fn [acc t]
                            (str acc (format-todo t)))
                          "" todos)
        org-list (str "* " date "\n\n"
                      (reduce (fn [acc t]
                                (str acc (org-mode-format t)))
                              "" todos))
        header (header-frame date curl?)
        formatted-list (str header horizontal-line todo-list)]
    (spit (str "resources/org-files/" date ".org") org-list)
    (if curl? formatted-list (browser-page formatted-list))))
