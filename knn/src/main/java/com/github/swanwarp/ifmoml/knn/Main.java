package com.github.swanwarp.ifmoml.knn;

import com.github.swanwarp.ifmoml.knn.math.Dot;
import com.github.swanwarp.ifmoml.knn.math.Minkowski;
import com.github.swanwarp.ifmoml.knn.utils.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        System.out.println(Arrays.toString(args));
        final String filename;
        if (args.length < 1) {
            try (BufferedReader stdin = new BufferedReader(new InputStreamReader(System.in))) {
                filename = stdin.readLine();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        } else {
            filename = args[0];
        }

        ArrayList<Pair<Dot, Integer>> data = new ArrayList<>();

        try (BufferedReader br = new BufferedReader(new FileReader(new File(filename)))) {
            String line;
            while ((line = br.readLine()) != null) {
                if (line.equals("")) {
                    continue;
                }

                String[] l = line.split(",");

                if (l.length != 3) {
                    System.err.println("wrong formatting!");
                    return;
                }

                data.add(new Pair<>(new Dot(new Double(l[0]), new Double(l[1])), new Integer(l[2])));
            }
        } catch (FileNotFoundException e) {
            System.err.println("no file: " + filename);
            return;
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        ArrayList<Pair<Dot, Integer>> data_copy = new ArrayList<>(data);

        for(int i = 0; i < 10; i++) {
            KNN knn = new KNN(new Minkowski(), data_copy, 2);

            System.out.println(knn.k);
        }
    }
}
