package com.github.swanwarp.ifmoml.knn.utils;

public class CPair implements Comparable<CPair> {
    public final double d;
    public final int c;

    public CPair(double d, int c) {
        this.d = d;
        this.c = c;
    }

    @Override
    public int compareTo(CPair o) {
        return Double.compare(this.d, o.d);
    }
}
