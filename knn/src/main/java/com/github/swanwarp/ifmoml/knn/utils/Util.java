package com.github.swanwarp.ifmoml.knn.utils;

import com.github.swanwarp.ifmoml.knn.math.Manhattan;
import com.github.swanwarp.ifmoml.knn.math.Metric;
import com.github.swanwarp.ifmoml.knn.math.Minkowski;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Util {
    public static final Metric[] metrics = {new Manhattan(), new Minkowski()};

    public static <T> T[] shuffleArray(T[] arr) {
        T[] ret = Arrays.copyOf(arr, arr.length);

        Random rnd = ThreadLocalRandom.current();
        for (int i = ret.length - 1; i > 0; i--)
        {
            int index = rnd.nextInt(i + 1);

            T a = ret[index];
            ret[index] = ret[i];
            ret[i] = a;
        }

        return ret;
    }
}
