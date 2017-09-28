package com.github.swanwarp.ifmoml.knn.math;

public class Manhattan implements Metric {
    @Override
    public double distance(Dot x, Dot y) {
        checkSameDimensions(x, y);
        double distance = 0.0;
        for (int i = 0; i < x.dim(); ++i) {
            distance += Math.abs(x.get(i) - y.get(i));
        }
        return distance;
    }
}
