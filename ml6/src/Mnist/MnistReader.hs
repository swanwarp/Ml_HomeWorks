module Mnist.MnistReader
       ( parseMnistFile
       , parseMnistLabels
       , getPicture
       , getPx
       , getPxByOffset
       , genList
       , MnistImgList(..)
       , MnistImg(..)
       ) where

import qualified Data.ByteString.Lazy as BL
import qualified Data.ByteString as BS
import Data.Binary
import Data.Binary.Get
import Control.Monad

parseMnistLabels :: Get [Int]
parseMnistLabels = do
    getWord32be -- magic
    labels <- getWord32be
    ls <- replicateM (fromIntegral labels) getWord8
    return $ map fromIntegral ls

data MnistImgList = MnistImgList
                  { milLength :: !Int
                  , milHeight :: !Int
                  , milWidth :: !Int
                  , milSource :: BL.ByteString }

data MnistImg = MnistImg
              { miSource :: MnistImgList
              , miOffset :: !Int }

parseMnistFile :: Get MnistImgList
parseMnistFile = do
    getWord32be
    amount <- getWord32be
    rows <- getWord32be
    cols <- getWord32be
    source <- getLazyByteString $ fromIntegral $ amount * rows * cols
    return $ MnistImgList (fromEnum amount) (fromEnum rows) (fromEnum cols) source

getPicture :: Int -> MnistImgList -> MnistImg
getPicture n list = MnistImg list (n * milHeight list * milWidth list)

getPx :: MnistImg -> Int -> Int -> Word8
getPx (MnistImg src off) r c = BL.index (milSource src) $ fromIntegral (off + r * milWidth src + c)

getPxByOffset :: MnistImg -> Int -> Word8
getPxByOffset (MnistImg src off) o = BL.index (milSource src) $ fromIntegral (off + o)

genList :: MnistImgList -> [MnistImg]
genList mil@(MnistImgList l h w s) = map (MnistImg mil) [0, h*w .. (l-1) * h*w]