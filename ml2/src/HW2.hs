module HW2 where

import HW2.CSVTransformer(Table(..))
import Data.Coerce(coerce)
import Data.List(foldl')

orientVertical :: Table a -> Table a
orientVertical = coerce
               . foldr (\[e1, e2, e3] [l1, l2, l3] -> [e1:l1, e2:l2, e3:l3]) [[], [], []]
               . (coerce :: Table a -> [[a]])

orientHorizontal :: Table a -> Table a
orientHorizontal = coerce
                 . magicFold
                 . (coerce :: Table a -> [[a]]) where
    magicFold :: [[a]] -> [[a]]
    magicFold [[], [], []] = []
    magicFold lists = map head lists : magicFold (map tail lists)

normalizeTable :: (Fractional a, Ord a) => Table a -> Table a
normalizeTable = orientHorizontal
               . coerce
               . map normalize
               . (coerce :: Table a -> [[a]])
               . orientVertical

normalize :: (Fractional a, Ord a) => [a] -> [a]
normalize l = map (/ k) l where
    k = foldl' max (head l) l

data Coefficients a = Coefficients a a a deriving Show

apply :: Num a => Coefficients a -> a -> a -> a
apply (Coefficients a b c) x y = x*a + y*b + c

deviation :: Fractional a => [(a, a)] -> a
deviation l = sum (map (\(a, b) -> (a - b) ^ 2) l) / fromIntegral (length l)