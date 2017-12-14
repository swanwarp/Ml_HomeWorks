module Kernels where

type Kernel = Double -> Double

quartic :: Kernel
quartic x = (15.0 / 16.0) * (1 - x^2)^2

epanechnikov :: Kernel
epanechnikov x = 0.75 * (1 - x^2)

hauss :: Kernel
hauss x = sqrt (1 / (2 * pi)) * exp (-x ^ 2 / 2)

triangle :: Kernel
triangle x = 1 - abs x

rectangle :: Kernel
rectangle x = 1/2

magicKernel x b = (1-(x/b)^2)^2

