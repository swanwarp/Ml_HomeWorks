package com.github.swanwarp.ifmoml.knn;

import com.github.swanwarp.ifmoml.knn.math.Dot;
import com.github.swanwarp.ifmoml.knn.math.Manhattan;
import com.github.swanwarp.ifmoml.knn.math.Metric;
import com.github.swanwarp.ifmoml.knn.utils.CPair;
import com.github.swanwarp.ifmoml.knn.utils.Pair;
import com.github.swanwarp.ifmoml.knn.utils.Util;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class KNN {
    private Metric p;
    private ArrayList<Pair<Dot, Integer>> data;
    private int numOfClasses;
    private int k;
    private final int cv = 9;

    public KNN(ArrayList<Pair<Dot, Integer>> data, int numOfClasses) {
        this.data = data;
        this.numOfClasses = numOfClasses;

        Pair<Metric, Integer> res = findMetric(data).first;

        this.p = res.first;
        this.k = res.second;

        System.out.println(this.p.getClass().getSimpleName()  + " " + this.k);
    }

    public KNN(Metric p, ArrayList<Pair<Dot, Integer>> data, int numOfClasses) {
        this.p = p;
        this.data = data;
        this.numOfClasses = numOfClasses;

        this.k = findK(this.p, this.data);

        System.out.println(k);
    }

    private Pair<Pair<Metric, Integer>, Double> findMetric(ArrayList<Pair<Dot, Integer>> data) {
        Pair<Metric, Integer>[] rets = new Pair[Util.metrics.length];

        List<Pair<Dot, Integer>> toRnd = new ArrayList<>(data);
        Collections.shuffle(toRnd);

        ArrayList<Pair<Dot, Integer>> kData = new ArrayList<>(toRnd .subList(0, 3*toRnd.size()/4));
        int u = toRnd.size() / cv;

        for(int i = 0; i < rets.length; i++) {
            rets[i] = new Pair<>(Util.metrics[i], findK(Util.metrics[i], kData));
        }

        double[][] ps = new double[rets.length][cv];

        for(int t = 0; t < cv; t++) {
            Collections.shuffle(toRnd);
            ArrayList<ArrayList<Pair<Dot, Integer>>> folds = new ArrayList<>();

            for(int i = 0; i < cv; i++) {
                folds.add(new ArrayList<>(toRnd.subList(u * i, u * (i + 1) < toRnd.size() ? u * (i + 1) : toRnd.size())));
            }

            for(int o = 0; o < rets.length; o++) {
                double tp = 0,
                        fp = 0,
                        fn = 0;

                for(int i = 0; i < folds.size(); i++) {
                    for(int j = 0; j < folds.size(); j++) {
                        if(j != i) {

                            ArrayList<Pair<Dot, Integer>> test = folds.get(i);

                            for(int k = 0; k < test.size(); k++) {
                                int res = solve(test.get(k).first, folds.get(j), rets[o].second, rets[o].first);

                                if(res == test.get(k).second) {
                                    if(res == 1) {
                                        tp++;
                                    }
                                } else {
                                    if(res == 1) {
                                        fp++;
                                    } else {
                                        fn++;
                                    }
                                }
                            }
                        }
                    }
                }

                if(tp != 0) {
                    double prec = tp / (tp + fp),
                            rec = tp / (tp + fn);

                    ps[o][t] = 2 * (rec * prec) / (rec + prec);
                }
            }
        }

        double p_fin[] = new double[ps.length];

        for(int i = 0; i < ps.length; i++) {
            p_fin[i] = 0;

            for(int j = 0; j < cv; j++) {
                p_fin[i] += ps[i][j];
            }

            p_fin[i] = p_fin[i] / (cv * cv);
        }

        double max = 0;

        Pair<Metric, Integer> ret = null;
        double p_ret = 0;

        for(int i = 0; i < ps.length; i++) {
            if(p_fin[i] >= max) {
                max = p_fin[i];
                p_ret = p_fin[i];
                ret = rets[i];
            }
        }

        return new Pair<>(ret, p_ret);
    }

    private int findK(Metric p, ArrayList<Pair<Dot, Integer>> data) {
        double[][] kss = new double[(int) Math.sqrt(data.size()) + 1][cv];
        int[] ks = new int[(int) Math.sqrt(data.size()) + 1];

        for(int i = 0; i < Math.sqrt(data.size()); i++) {
            ks[i] = i + 1;
        }

        for(int i = 0; i < kss.length; i++) {
            for(int j = 0; j < kss[i].length; j++) {
                kss[i][j] = 0;
            }
        }

        List<Pair<Dot, Integer>> toRnd = new ArrayList<>(data);
        Collections.shuffle(toRnd);
        int u = toRnd.size() / cv;
        int max_length = 0;

        for(int i = 0; i < ks.length; i++) {
            if(ks[i] >= u) {
                max_length = i - 1;
                break;
            } else {
                max_length = ks.length;
            }
        }


        for(int t = 0; t < cv; t++) {
            Collections.shuffle(toRnd);
            ArrayList<ArrayList<Pair<Dot, Integer>>> folds = new ArrayList<>();

            for(int i = 0; i < cv; i++) {
                folds.add(new ArrayList<>(toRnd.subList(u * i, u * (i + 1) < toRnd.size() ? u * (i + 1) : toRnd.size())));
            }

            for(int o = 0; o < max_length; o++) {
                double tp = 0,
                        fp = 0,
                        fn = 0;

                for(int i = 0; i < folds.size(); i++) {
                    for(int j = 0; j < folds.size(); j++) {
                        if(j != i) {

                            ArrayList<Pair<Dot, Integer>> test = folds.get(i);

                            for(int k = 0; k < test.size(); k++) {
                                int res = solve(test.get(k).first, folds.get(j), ks[o], p);

                                if(res == test.get(k).second) {
                                    if(res == 1) {
                                        tp++;
                                    }
                                } else {
                                    if(res == 1) {
                                        fp++;
                                    } else {
                                        fn++;
                                    }
                                }
                            }
                        }
                    }
                }

                if(tp != 0) {
                    double prec = tp / (tp + fp),
                            rec = tp / (tp + fn);

                    kss[o][t] += 2 * (rec * prec) / (rec + prec);
                }
            }
        }

        double k_fin[] = new double[max_length];

        for(int i = 0; i < max_length; i++) {
            k_fin[i] = 0;

            for(int j = 0; j < cv; j++) {
                k_fin[i] += kss[i][j];
            }

            k_fin[i] /= cv;
        }

        int k = 0;

        double max = 0;

        System.out.println(p.getClass().getSimpleName() + ":");

        for(int i = 0; i < max_length; i++) {
            System.out.print(k_fin[i] + " ");

            if(k_fin[i] >= max) {
                max = k_fin[i];
                k = ks[i];
            }
        }

        System.out.println();

        return k;
    }

    public int solve(Dot d) {
        return solve(d, data, k, p);
    }

    private int solve(Dot d, List<Pair<Dot, Integer>> data, int k, Metric p) {
        ArrayList<CPair> toSort = new ArrayList<>();

        for (Pair<Dot, Integer> aData : data) {
            if(aData.first.equals(d))
                continue;

            toSort.add(new CPair(p.distance(d, aData.first), aData.second));
        }

        Collections.sort(toSort);

        double most[] = new double[numOfClasses];

        for(int i = 0; i < k; i++) {
            most[toSort.get(i).c] += Kernel(toSort.get(i).d/toSort.get(k).d);
        }

        double max_length = 0;
        int max_c = 0;

        for(int i = 0; i < numOfClasses; i++) {
            if(most[i] > max_length) {
                max_length = most[i];
                max_c = i;
            }
        }

        return max_c;
    }

    private double Kernel(double d) {
        return Math.pow(Math.E, (-d*d/2))/Math.sqrt(2 * Math.PI);
    }
}
