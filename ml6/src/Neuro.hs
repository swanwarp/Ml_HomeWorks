{-# LANGUAGE NamedFieldPuns #-}
{-# LANGUAGE RecordWildCards #-}

module Neuro where

import Mnist.MnistReader
import qualified Data.ByteString.Lazy as BL
import qualified Data.IntMap as DIM
import Data.IntMap((!))
import Control.Monad.State
import Data.Foldable(foldl')
import qualified Data.Array.Unboxed as DAU
import Debug.Trace(trace)

data Brain = Brain
           { bSensorFunction :: MnistImg -> Int -> Double -- n = row * W + col  ->  ±1.0
           , bMiddleLayer :: DIM.IntMap [Int]             -- bound sensors
           , bAnswers :: DIM.IntMap ([Int], [Double])     -- R num to (Ss, Cs)
           , bDiscardLevel :: Double }                    -- higher means true, else false

testPic Brain {..} img =
    let (MnistImgList _ height width _) = miSource img
        look = bSensorFunction img
        assocArr = DAU.listArray (1, DIM.size bMiddleLayer)
                 $ map (\n -> signum $ sum $ map look $ bMiddleLayer ! n) [1..(DIM.size bMiddleLayer)]
                 :: DAU.UArray Int Double
        assoc n = assocArr DAU.! n
        ansArr = DAU.listArray (0, DIM.size bAnswers - 1)
               $ map (\n -> let (aNs, cs) = bAnswers ! n
                            in linSum (map assoc aNs) cs)
                     [0..(DIM.size bAnswers - 1)]
               :: DAU.UArray Int Double
        answer n = ansArr DAU.! n
        in map answer [0..9]

trainPic :: (MnistImg, Int) -> State Brain ()
trainPic (img, label) = do
    br@ Brain {bSensorFunction, bMiddleLayer, bAnswers, bDiscardLevel} <- get
    let results = trace (show $ miOffset img) $ testPic br img
    let (wrongYes, wrongNo) = foldl' solution ([], Nothing) $ zip [0..9] results
          where
            solution (yes, no) (ix, ans)
                | ix == label && ans <= bDiscardLevel = (yes, Just ix) -- bad, should +1
                | ans > bDiscardLevel                 = (ix:yes, no)   -- bad, should -1
                | otherwise                           = (yes, no)      -- right, leave unmodified
    let toInsert = map (\i -> (i, unzip $ uncurry (zipWith $ \n c -> (n, c - 1.0)) (bAnswers ! i))) wrongYes
    put $ Brain bSensorFunction bMiddleLayer (DIM.union (DIM.fromList toInsert) bAnswers) bDiscardLevel
    return ()

train :: MnistImgList -> [Int] -> State Brain ()
train pics labels = mapM_ trainPic $ zip (genList pics) labels

linSum a b = sum $ zipWith (*) a b

buildBrain :: Int -> Int -> Brain
buildBrain w h = Brain senseFilter (DIM.fromList $ zip [1..] ss) (DIM.fromList $ map (\x -> (x,([1..sl], (replicate sl 0.0)))) [0..9]) 0.0
  where
    phw = w `div` 3 + 1
    phh = h `div` 5 + 1
    pvw = w `div` 5 + 1
    pvh = h `div` 2 + 1
    check :: Int -> Int -> Int -> Int -> Int -> Bool
    check wf wt hf ht p = hf <= x && x <= ht && wf <= y && y <= wt
      where
        (x, y) = p `divMod` w
    points = [0..(w*h-1)]
    magic w h = [0..h] >>= (\x -> map (\a -> (x,a)) [0..w])
    sh = map (\(x, y) -> check (phw * x) (phw * (x+1)) (phh * y) (phh * (y + 1))) $ magic 2 4
    sv = map (\(x, y) -> check (pvw * x) (pvw * (x+1)) (pvh * y) (pvh * (y + 1))) $ magic 4 1
    ss = map (\f -> filter f points) (sh ++ sv)
    sl = length ss
    senseFilter i o = if getPxByOffset i o >= 128 then 1.0 else 0.0