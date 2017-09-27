package com.github.swanwarp.ifmoml.knn.math;

public interface Metric {
    double distance(Dot x, Dot y);
}
