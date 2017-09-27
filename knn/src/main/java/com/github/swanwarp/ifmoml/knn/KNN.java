package com.github.swanwarp.ifmoml.knn;

import com.github.swanwarp.ifmoml.knn.math.Dot;
import com.github.swanwarp.ifmoml.knn.math.Metric;
import com.github.swanwarp.ifmoml.knn.utils.CPair;
import com.github.swanwarp.ifmoml.knn.utils.Pair;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KNN {
    private Metric p;
    private ArrayList<Pair<Dot, Integer>> data;
    private int numOfClasses;
    public int k;
    private int tests = 100;

    public KNN(Metric p, ArrayList<Pair<Dot, Integer>> data, int numOfClasses) {
        this.p = p;
        this.data = data;
        this.numOfClasses = numOfClasses;

        findK((int) Math.sqrt(data.size()) * 4);
    }

    private void findK(int n) {
        int[][] ks = new int[n][tests];

        for(int i = 0; i < tests; i++) {
            ArrayList<Pair<Dot, Integer>> toRnd = new ArrayList<>(data);
            Collections.shuffle(toRnd);
            List<Pair<Dot, Integer>> test = toRnd.subList(0, n);
            List<Pair<Dot, Integer>> train = toRnd.subList(n, toRnd.size());

            for(int j = n; j >= 1; j--) {
                int m1 = 0;

                for(Pair<Dot, Integer> p : test) {
                    if (solve(p.first, train, j) != p.second) {
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

    private int solve(Dot d, List<Pair<Dot, Integer>> data, int k) {
        ArrayList<CPair> toSort = new ArrayList<>();

        for (Pair<Dot, Integer> aData : data) {
            toSort.add(new CPair(p.distance(d, aData.first), aData.second));
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
