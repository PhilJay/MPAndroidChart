package com.xxmassdeveloper.mpchartexample.custom.utils;

import android.content.res.AssetManager;
import android.graphics.PointF;
import android.util.Log;

import com.xxmassdeveloper.mpchartexample.custom.data.City;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class ExampleFileUtils {
    private static final String LOG = "MPExamples-FileUtils";

    public static List<City> loadCaliforniaCitiesFromAssets(AssetManager am, String path) {
        List<City> entries = new ArrayList<>();
        int i = 0;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(am.open(path), "UTF-8"));

            String line = reader.readLine();

            while (line != null) {
                // process line
                String[] split = line.split(",");

                // skip the first line if it has labels; skip non-California cities
                if (split[0].equals("city") || !isCalifornia(split[7])) {
                    line = reader.readLine();
                    continue;
                }

                String name = split[0];
                float lat = Float.valueOf(split[2]);
                float lon = Float.valueOf(split[3]);
                int population = Integer.valueOf(split[9]);
                boolean capital = split[8].equals("admin");

                City city = new City(name, lat, lon, population, capital);
                entries.add(city);


                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(LOG, e.toString());

        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG, e.toString());
                }
            }
        }

        // this should be done before creating the file, but just in case...
        Collections.sort(entries, City.BY_REVERSE_POPULATION);
        return entries;
    }

    private static boolean isCalifornia(String name) {
        return (name.equals("California"));
    }

    public static List<PointF> loadBoundariesFromAssets(AssetManager am, String path) {
        List<PointF> points = new ArrayList<>();
        int i = 0;

        BufferedReader reader = null;
        try {
            reader = new BufferedReader(
                    new InputStreamReader(am.open(path), "UTF-8"));

            String line = reader.readLine();

            while (line != null) {
                // process line
                String[] split = line.split(",");

                // skip the first line if it has labels; skip non-contiguous geographies
                if (split[0].equals("state")) {
                    line = reader.readLine();
                    continue;
                }

                float lat = Float.valueOf(split[0]);
                float lon = Float.valueOf(split[1]);
                PointF point = new PointF(lat, lon);
                points.add(point);

                line = reader.readLine();
            }
        } catch (IOException e) {
            Log.e(LOG, e.toString());

        } finally {

            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    Log.e(LOG, e.toString());
                }
            }
        }
        return points;
    }
}
