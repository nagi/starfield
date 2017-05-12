(ns starfield.core
  (:require [quil.core :as q :include-macros true]
            [quil.middleware :as m]))


(enable-console-print!)

;; define your app data so that it doesn't get over-written on reload
(defonce app-state (atom {:text "Hello world!"}))

(defn on-js-reload []
  ;; optionally touch your app-state to force rerendering depending on
  ;; your application
  ;; (swap! app-state update-in [:__figwheel_counter] inc)
  )

(def canvas-x 500)
(def canvas-y 500)
(def star-size 6)
(def z-acceleration 0.009)
(def frame-rate 60)

(defn random-star []
  ;; (repeatedly 3 #(- (rand) 0.5)))
  (list  (- (rand) 0.5)
         (- (rand) 0.5)
         (- (rand) 0.5)
         ))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate frame-rate)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :rgb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:stars (sort-by last (repeatedly 1000 random-star))})

(defn move-star [star]
  (let [x (first star)
        y (second star)
        z (last star)
        z (+ z-acceleration z)
        z (if (> z 1) (- z 1) z)]
    (list x y z)))

(defn update-state [state]
  (let [stars (:stars state)]
    {:stars
     (sort-by last
              (map move-star stars))}))

(defn scale-perspective [x axis z]
  (let [z (Math.pow 209 z) ;; WTF??
        in-a-square (+ (/ axis 2) (* axis x))
        half-axis (/ axis 2)
        gap (- 1 z) 
        shift (* half-axis gap)
        ]
    (+ shift
       (* z in-a-square))))

(defn draw-star [star]
  (let [z (last star)
        x (scale-perspective (first star) canvas-x z)
        y (scale-perspective (second star) canvas-y z)
        ;; size (* (Math.pow 2 star-size) z)
        size (* star-size (Math.pow 2 z))
        shade (* z 255)]
    (q/fill shade shade shade)
    (q/ellipse x y size size)))

(defn draw-state [state]
  (q/background 0)
  ; Calculate x and y coordinates of the circle.
  (doall
   (map draw-star (:stars state))))

(q/defsketch quil-cljs
  :host "quil-canvas"
  :size [500 500]
  ; setup function called only once, during sketch initialization.
  :setup setup
  ; update-state is called on each iteration before draw-state.
  :update update-state
  :draw draw-state
  ; This sketch uses functional-mode middleware.
  ; Check quil wiki for more info about middlewares and particularly
  ; fun-mode.
  :middleware [m/fun-mode])
