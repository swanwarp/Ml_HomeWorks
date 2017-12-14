module HW2.Plotter where

import HW2.CSVTransformer
import Data.Coerce
import Graphics.EasyPlot
import Data.Ratio((%))

tableToPointSet :: Fractional a => Table Rational -> [(a, a, Double)]
tableToPointSet = map (\[a,b,c] -> (fromRational a, fromRational b, fromRational c)) . coerce

draw :: [(Double, Double, Double)] -> (Double -> Double -> Double) -> IO Bool
draw d f = plot' [Interactive] X11 [Data3D [Style Points, Color Magenta] [RangeX 0.0 1.0, RangeY 0.0 1.0] d,
                                    Function3D [Color Blue] [RangeX 0.0 1.0, RangeY 0.0 1.0] f]
