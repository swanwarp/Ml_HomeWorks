package com.github.swanwarp.ifmoml.knn.math;

public class Dot {
    private final double[] x;

    public Dot(int dim) {
        x = new double[dim];
    }

    public Dot(double x, double y) {
        this.x = new double[2];
        this.x[0] = x;
        this.x[1] = y;
    }

    public void set(int i, double x_i) {
        x[i] = x_i;
    }

    public double get(int i) {
        return x[i];
    }

    public int dim() {
        return x.length;
    }
}
