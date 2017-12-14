module HW2.CSVTransformer
       ( Table(..)
       , parseCSV
       ) where

import Data.Ratio((%))

splitOn :: Char -> String -> [String]
splitOn c = foldr magic [[]] where
    magic cc (l:ls) = if cc == c then []:l:ls else (cc:l):ls

newtype Table a = Table [[a]] deriving Show

parseCSV :: String -> Table Rational
parseCSV = Table . map (map ((% 1) . read) . splitOn ',') . tail . lines
