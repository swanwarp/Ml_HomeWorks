module HW2.Gradient where

import Prelude hiding (error)
import HW2
import HW2.CSVTransformer (Table(..))

choose :: (Fractional a, Ord a) => Table a -> Coefficients a -> a -> Coefficients a
choose (Table t) cs error = fst $ head $ dropWhile (\r -> snd r >= error) $ iterate step (cs, error * 100) where
    step (c, _) = (recalculate c t (predict c t), deviation (zip (predict c t) (map (\[_, _, e] -> e) t)))

predict :: Num a => Coefficients a -> [[a]] -> [a]
predict cs = map (\(x:y:_) -> apply cs x y)

recalculate :: Fractional a => Coefficients a -> [[a]] -> [a] -> Coefficients a
recalculate (Coefficients oa ob oc) table predictions =
    (\[a,b,c] -> Coefficients (oa - alpha * a / len) (ob - alpha * b / len) (oc - alpha * c / len)) $
    foldr (\([x,y,e], p) [sa, sb, sc] -> [sa + (p-e) * x, sb + (p-e)*y, sc + (p-e)]) [0, 0, 0] $
    zip table predictions where
    alpha :: Fractional a => a
    alpha = 1 / 2
    len = fromIntegral $ length predictions

