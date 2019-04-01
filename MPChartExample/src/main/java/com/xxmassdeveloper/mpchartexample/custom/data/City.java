package com.xxmassdeveloper.mpchartexample.custom.data;

import java.util.Comparator;
import java.util.Locale;

import androidx.annotation.NonNull;

public class City {
    public final String name;
    public final float latitude;
    public final float longitide;
    public final int population;
    public final boolean isCapital;

    public City(String name, float lat, float lon, int pop) {
        this.name = name;
        this.latitude = lat;
        this.longitide = lon;
        this.population = pop;
        isCapital = false;
    }

    public City(String name, float lat, float lon, int pop, boolean isCapital) {
        this.name = name;
        this.latitude = lat;
        this.longitide = lon;
        this.population = pop;
        this.isCapital = isCapital;
    }

    @NonNull
    @Override
    public String toString() {
        return String.format(Locale.getDefault(), "%s @ %.1f, %.1f, pop= %,d", name, latitude, longitide, population);
    }

    public static Comparator<City> BY_POPULATION = new Comparator<City>() {
        @Override
        public int compare(City o1, City o2) {
            return o1.population - o2.population;
        }
    };
    public static Comparator<City> BY_REVERSE_POPULATION = new  Comparator<City>() {
        @Override
        public int compare(City o1, City o2) {
            return o2.population - o1.population;
        }
    };
    public static Comparator<City> BY_LONGITUDE = new Comparator<City>() {
        @Override
        public int compare(City o1, City o2) {
            return Float.compare(o1.longitide, o2.longitide);
        }
    };
    public static Comparator<City> BY_LATITUDE = new Comparator<City>() {
        @Override
        public int compare(City o1, City o2) {
            return Float.compare(o1.latitude, o2.latitude);
        }
    };
}