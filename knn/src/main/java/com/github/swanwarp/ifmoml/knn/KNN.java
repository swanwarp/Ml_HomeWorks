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

    public KNN(Metric p, ArrayList<Pair<Dot, Integer>> data, int numOfClasses) {
        this.p = p;
        this.data = data;
        this.numOfClasses = numOfClasses;

        findK();
    }

    private void findK() {
        int n = (int) Math.sqrt(data.size());

        ArrayList<int[]> kss = new ArrayList<>();
        ArrayList<Integer> ks = new ArrayList<>();

        for(int i = 1; i <= n; i++) {
            if(i % 2 != 0) {
                kss.add(new int[data.size()]);
                ks.add(i);
            }
        }


        //List<Pair<Dot, Integer>> toRnd = new ArrayList<>(data), trains[] = new ArrayList[cv - 1];

        //Collections.shuffle(toRnd);
        // trains[i] = new ArrayList<>(toRnd.subList(i * u, (i + 1) * u));

        //List<Pair<Dot, Integer>> test = toRnd.subList((cv - 1) * u, toRnd.size());

        for(int i = 0; i < kss.size(); i++) {
            for(int j = 0; j < data.size(); j++) {
                int m1 = 0;

                if (solve(data.get(j).first, data, ks.get(i)) != data.get(j).second) {
                    m1++;
                }

                kss.get(i)[j] += m1;
            }
        }

        double min = Integer.MAX_VALUE;

        for(int i = 0; i < kss.size(); i++) {
            double m = 0;

            for(int j = 0; j < kss.get(i).length; j++) {
                //System.out.print(kss.get(i)[j] + " ");

                m += kss.get(i)[j];
            }

            m = m / data.size();

            if(m < min) {
                min = m;
                k = ks.get(i);
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
            if(aData.first.equals(d))
                continue;

            toSort.add(new CPair(p.distance(d, aData.first), aData.second));
        }

        Collections.sort(toSort);

        double most[] = new double[numOfClasses];

        for(int i = 0; i < k; i++) {
            most[toSort.get(i).c] += 1 * toSort.get(i).d/toSort.get(k).d;
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
