module Regression where

import Data.List(foldl',sort)

import Kernels
import Metrics

getY2 :: Metric
      -> Kernel
      -> [(Double, Double)]
      -> [Double]
      -> Double
      -> Double
      -> Double
getY2 (|-) ker dots deltas p x = getY (|-) ker (xToH (|-) p (map fst dots) x) (zip dots deltas) x

getFunction (|-) ker p dat = getY2 (|-) ker dat (fst $ lowess (|-) ker p dat) p

getY :: Metric
     -> Kernel
     -> Double                       -- window
     -> [((Double, Double), Double)] -- ((x,y),delta)
     -> Double                       -- x
     -> Double                       -- returns y
getY (|-) ker h weiData x = let (a, b) = foldl' magic (0,0) weiData
                            in if b == 0 then 0 else a / b
  where
    magic (a,b) ((xc, yc), delta) = let wc = ker ((x |- xc) / h)
                                    in (a + yc * delta * wc, b + delta * wc)

lowess :: Metric -> Kernel -> Double -> [(Double, Double)] -> ([Double], [Double])
lowess (|-) ker p dat = (iterate action [1.0,1.0..] !! 3, hs)
  where
    (xs,ys) = unzip dat
    hs = map (xToH (|-) p xs) xs
    action deltas =
        let sets = prepareLOOSets $ zip dat deltas
            nys = map (\(h,(((x,_),_),weiData)) -> getY (|-) ker h weiData x) $ zip hs sets
            dss = zipWith (|-) nys ys
            med = median dss
        in map (\e -> magicKernel e (6*med)) dss

percentToK :: Double -> Int -> Int
percentToK p n = ceiling (p * fromIntegral n)

xToH :: Metric -> Double -> [Double] -> Double -> Double
xToH (|-) p xs x = let k = percentToK p (length xs)
                   in sort (map (|- x) xs) !! k

prepareLOOSets :: [a] -> [(a, [a])] -- [a_i] -> [(a_i,[a])] where [a] does not have a_i
prepareLOOSets pairs = zip pairs (mxLOO pairs)
  where
    mxLOO :: [a] -> [[a]]
    mxLOO [] = []
    mxLOO (p:ps) = ps : map (p:) (mxLOO ps)

median l
    | odd len   = sl !! (len `div` 2)
    | otherwise = sum (take 2 $ drop (len `div` 2 - 1) $ sort sl) / 2
      where
        len = length l
        sl = sort l
