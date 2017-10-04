package com.github.swanwarp.ifmoml.knn;

import com.github.swanwarp.ifmoml.knn.math.Dot;
import com.github.swanwarp.ifmoml.knn.math.Manhattan;
import com.github.swanwarp.ifmoml.knn.math.Minkowski;
import com.github.swanwarp.ifmoml.knn.utils.Pair;

import java.io.*;
import java.util.ArrayList;
import java.util.Arrays;

public class Main {
    public static void main(String[] args) {
        final String filename;
        if (!(new File("in").exists())) {
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
        } else {
            filename = "in";
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
        }

        data_copy = new ArrayList<>();

        for(int i = 0; i < data.size(); i++) {
            Dot temp = data.get(i).first;
            double a = ((double) 1)/3, b = ((double) 2)/3;
            data_copy.add(new Pair<>(new Dot(a * temp.get(0) + b * temp.get(1), b * temp.get(0) - a * temp.get(1)), data.get(i).second));
        }

        for(int i = 0; i < 10; i++) {
            KNN knn = new KNN(new Minkowski(), data_copy, 2);
        }
    }
}
