package com.github.swanwarp.ifmoml.knn.utils;

import java.util.Objects;

public class Pair<F, S> {
    public final F first;
    public final S second;

    public Pair(F first, S second) {
        this.first = first;
        this.second = second;
    }

    @Override
    public int hashCode() {
        return first.hashCode() ^ second.hashCode();
    }

    @Override
    public boolean equals(Object obj) {
        return obj instanceof Pair
                && Objects.equals(((Pair) obj).first, first)
                && Objects.equals(((Pair) obj).second, second);
    }

    @Override
    public String toString() {
        return '(' + first.toString() + "; " + second.toString() + ')';
    }
}
