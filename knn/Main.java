package com.github.swanwarp.ifmoml.knn

import com.company.math.Dot;
import com.company.math.Minkowski;
import javafx.util.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.StringTokenizer;

public class Main {
    public static void main(String[] args) throws FileNotFoundException, IOException {
        BufferedReader br = new BufferedReader(new FileReader(new File("in")));

        ArrayList<Pair<Dot, Integer>> data = new ArrayList<>();

        while (true) {
            String line = br.readLine();

            if(line == null) {
                break;
            }

            if(line.equals("")) {
                continue;
            }

            String[] l = line.split(",");

            data.add(new Pair<>(new Dot(new Double(l[0]), new Double(l[1])), new Integer(l[2])));
        }

        Pair<Dot, Integer>[] data_copy = new Pair[data.size()];

        for(int i = 0; i < data.size(); i++) {
            data_copy[i] = data.get(i);
        }

        for(int i = 0; i < 10; i++) {
            KNN knn = new KNN(new Minkowski(), data_copy, 2);

            System.out.println(knn.k);
        }
    }

    static class FastScanner {
        BufferedReader br;
        StringTokenizer st;

        FastScanner(File f) {
            try {
                br = new BufferedReader(new FileReader(f));
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
        }

        String next() {
            while (st == null || !st.hasMoreTokens()) {
                try {
                    st = new StringTokenizer(br.readLine());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            return st.nextToken();
        }

        int nextInt() {
            return Integer.parseInt(next());
        }
    }
}
