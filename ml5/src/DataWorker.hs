module DataWorker where

import Data.Char(isSpace)

importCSV :: String -> [(Double, Double)]
importCSV = map (\[_, x, y] -> (read x, read y))
          . tail
          . foldr magic [[""]]
          . drop 7 where
    magic c ((ci:cl):ol)
        | c == '\n' = [""]:(ci:cl):ol
        | isSpace c = (ci:cl):ol
        | c == ';'  = ([]:ci:cl):ol
        | otherwise = ((c:ci):cl):ol
