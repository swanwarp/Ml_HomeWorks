package com.github.swanwarp.ifmoml.knn.utils;

import java.util.Arrays;
import java.util.Random;
import java.util.concurrent.ThreadLocalRandom;

public class Util {
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
