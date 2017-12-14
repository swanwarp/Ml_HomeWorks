module RegressionTrain where

import Data.List(foldl')

import Kernels
import Metrics
import Regression

kernels = [ ("Quartic", quartic)
          , ("Epanechnikov", epanechnikov)
          , ("Hauss", hauss)
          , ("Triangle", triangle)
          , ("Rectangle", rectangle) ]

trainLowess dat = map chooseKernel kernels
  where
    chooseKernel (name, ker) = (name, foldl' chooseP (0.0, 10.0^20) [0.0, 0.01 .. 0.6])
      where
        chooseP (bestP, err) p =
            let (deltas, hs) = lowess absDist ker p dat
                (_,ys) = unzip dat
                sets = prepareLOOSets $ zip dat deltas
                nys = map (\(h,(((x,_),_),weiData)) -> getY absDist ker h weiData x) $ zip hs sets
                nErr = mse nys ys
            in if nErr < err
               then (p, nErr)
               else (bestP, err)

mse xs ys = mean $ zipWith (\x y -> (x - y)^2) xs ys
mean xs = sum xs / fromIntegral (length xs)