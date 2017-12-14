module Plotter where

import Graphics.EasyPlot

draw :: [(String, Double -> Double)] ->[(Double, Double)] -> IO Bool
draw fs dat = plot X11 $
    Data2D [Style Points, Title "orig"] [] dat :
    map (\(name, f) -> Function2D [Style Lines, Title name] [Range 0 60] f) fs