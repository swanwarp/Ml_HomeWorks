package com.github.swanwarp.ifmoml.knn.math;

@FunctionalInterface
public interface Metric {
    double distance(Dot x, Dot y);

    default void checkSameDimensions(Dot x, Dot y) {
        if (x.dim() != y.dim())
            throw new IllegalArgumentException("x have " + x.dim() + " dimension, but y have " + y.dim());
    }
}
