module Metrics where

type Metric = Double -> Double -> Double

absDist x y = abs (x-y)

sqrDist x y = (x-y)^2
