package com.github.swanwarp.ifmoml.knn.math;

public class Minkowski implements Metric {
    @Override
    public double distance(Dot x, Dot y) {
        checkSameDimensions(x, y);
        double ret = 0;
        for(int i = 0; i < x.dim(); i++) {
            ret += Math.pow(x.get(i) - y.get(i), 2);
        }
        return Math.sqrt(ret);
    }
}
