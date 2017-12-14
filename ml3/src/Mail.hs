module Mail where

import Data.Bifunctor

data Mail = Mail
            { mSubject :: [Int]
            , mBody :: [Int] }

parseMail :: String -> Mail
parseMail = uncurry Mail . bimap magic magic . (\[s, _, m] -> (s, m)) . lines . dropWhile (/= ' ')
  where
    magic = map (read :: String -> Int) . words