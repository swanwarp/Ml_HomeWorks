module Spline where

import Data.List(foldl')

type Coefs = (Double, Double, Double, Double)

buildFunction :: [(Double, Double)] -> (Double -> Double)
buildFunction dat = createFunction (map fst dat) (buildCoefs dat)

createFunction xs cs = applySpline
  where
    mapping = zip xs cs
    applySpline x = go mapping
      where
        go ((xc, cc):(xn, cn):l)
          | x >= xc && x < xn = applyCoefs xc cn x
          | x >= xn = go ((xn, cn) : l)
          | otherwise = 0
        go [(xc, cc)] = applyCoefs xc cc x

applyCoefs x0 (a,b,c,d) x = a + b * (x - x0) + c/2 * (x - x0)^2 + d/6 * (x - x0)^3

buildCoefs :: [(Double, Double)] -> [Coefs]
buildCoefs dat = map (\((a,b),(c,d)) -> (a,b,c,d)) $ zip (zip as bs) (zip cs ds)
  where
    (xs,ys) = unzip dat
    as = ys
    hs = head xs : foldr (\x (hp:l) -> (x - hp):hp:l) [head xs] (tail xs)
    cys = zipWith (\(hc,hn) (yn,yc,yp) ->
                    6 * ((yn - yc)/hn - (yc - yp)/hc)) (zip hs (tail hs)) (zip3 (tail ys ++ [0]) ys (0:ys))
    cs = backwards (reverse xs, reverse cys) (forward (xs, cys))
    ds = zipWith3 (\cc cp hp -> (cc - cp)/hp) cs (0:cs) hs
    bs = zipWith3 (\f c h -> f/h + (h*c)/6) (zipWith (-) ys (0:ys)) (zipWith (\a b -> 2*a+b) cs (0:cs)) hs

 -- shuttle
 -- a x_(i-1) - b x_i + c x_(i+1) = y
 -- forward
 -- p_i = c_i / (b_i - a_i p_(i-1))
 -- q_i = (a_i * q_(i-1) - c_i) / (b_i - a_i p_(i-1))
 -- backward
 -- x_i = p_i x_(i+1) + q_i

 -- input
 -- a_i = h_i
 -- b_i = 2(h_i + h_(i+1))
 -- c_i = h_(i+1)
 -- h_i = x_i - x_(i-1)

forward (xf:xs, yf:ys) = foldl' magic ([head xs - xf / (2 * head xs)], [yf / (2 * head xs)]) $ zip shift ys
  where
    magic (po:ps,qo:qs) ((xp,xc,xn),yc) =
      let a = xc - xp
          b = 2 * (xp + xn)
          c = xn - xc
      in ((c / (b - a * po)):po:ps, ((a * qo - yc) / (b - a * po)):qo:qs)
    shift = zip3 (xf:xs) xs (tail xs)

backwards (xl:rxs, yl:rys) (pf:ps,qf:qs) =
    foldl' magic [ ((xl - head rxs) * qf - yl)
                 / (2 * head rxs - (xl - head rxs) * pf)]
                     $ zip (pf:ps) (qf:qs)
  where
    magic (r:rs) (p, q) = (p * r + q):r:rs