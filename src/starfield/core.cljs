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
(def star-size 9)
(def z-acceleration 0.009)

(defn random-star []
  (repeatedly 3 rand))

(defn setup []
  ; Set frame rate to 30 frames per second.
  (q/frame-rate 40)
  ; Set color mode to HSB (HSV) instead of default RGB.
  (q/color-mode :rgb)
  ; setup function returns initial state. It contains
  ; circle color and position.
  {:stars (sort-by last (repeatedly 200 random-star))})

(defn move-star [star]
  (let [x (+ (* z-acceleration (* (last star)(last star))) (first star))
        x (if (> x 1) (- x 1) x)
        y (second star)
        z (last star)]
    (list x y z)))

(defn update-state [state]
  (let [stars (:stars state)]
    {:stars
     (map move-star stars)}))

(defn draw-star [star]
  (let [x (* canvas-x (first star))
        y (* canvas-y (second star))
        z (last star)
        size (* star-size z)
        shade (* z 255)]
    (q/fill shade shade shade)
    (q/ellipse x y size size)))

(defn draw-state [state]
  ; Clear the sketch by filling it with light-grey color.
  (q/background 0)
  ; Set circle color.
  (q/fill 225 255 255)
  ; Calculate x and y coordinates of the circle.
  ; (draw-star (first (:stars state)))
  (doall
   (map draw-star (:stars state)))
  )

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
