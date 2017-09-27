package com.github.swanwarp.ifmoml.knn;

import com.company.math.Dot;
import com.company.math.Metric;
import com.company.utils.CPair;
import com.company.utils.Util;
import javafx.util.Pair;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;

public class KNN {
    private Metric p;
    private Pair<Dot, Integer>[] data;
    private int numOfClasses;
    public int k;
    private int tests = 100;

    public KNN(Metric p, Pair<Dot, Integer>[] data, int numOfClasses) {
        this.p = p;
        this.data = data;
        this.numOfClasses = numOfClasses;

        findK((int) Math.sqrt(data.length) * 4);
    }

    private void findK(int n) {
        int[][] ks = new int[n][tests];

        for(int i = 0; i < tests; i++) {
            Pair<Dot, Integer>[] toRnd = Util.shuffleArray(data);
            Pair<Dot, Integer>[] test = Arrays.copyOfRange(toRnd, 0, n),
                    train = Arrays.copyOfRange(toRnd, n, toRnd.length);

            for(int j = n; j >= 1; j--) {
                int m1 = 0;

                for(Pair<Dot, Integer> p : test) {
                    if(solve(p.getKey(), train, j) != p.getValue()) {
                        m1++;
                    }
                }

                ks[j - 1][i] = m1;
            }
        }

        double min = n;

        for(int i = 0; i < ks.length; i++) {
            double m = 0;

            for(int j = 0; j < ks[i].length; j++) {
                //System.out.print(ks[i][j] + " ");

                m += ks[i][j];
            }

            m = m/ks[i].length;

            if(m < min) {
                min = m;
                k = i + 1;
            }

            //System.out.println();
        }
    }

    public int solve(Dot d) {
        return solve(d, data, k);
    }

    private int solve(Dot d, Pair<Dot, Integer>[] data, int k) {
        ArrayList<CPair> toSort = new ArrayList<>();

        for(int i = 0; i < data.length; i++) {
            toSort.add(new CPair(p.distance(d, data[i].getKey()), data[i].getValue()));
        }

        Collections.sort(toSort);

        double most[] = new double[numOfClasses];

        for(int i = 0; i < k; i++) {
            most[toSort.get(i).c] += 1*toSort.get(i).d/toSort.get(k).d;
        }

        double max = 0;
        int max_c = 0;

        for(int i = 0; i < numOfClasses; i++) {
            if(most[i] > max) {
                max = most[i];
                max_c = i;
            }
        }

        return max_c;
    }
}
