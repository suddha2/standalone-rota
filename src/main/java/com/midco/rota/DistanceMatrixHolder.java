package com.midco.rota;

import java.util.Map;

import org.optaplanner.core.impl.util.Pair;

public class DistanceMatrixHolder {
    private static Map<Pair<String, String>, Double> distanceMap;

    public static void setDistanceMap(Map<Pair<String, String>, Double> map) {
        distanceMap = map;
    }

    public static double getDistance(String origin, String destination) {
        return distanceMap.getOrDefault(Pair.of(origin.trim().toLowerCase(), destination.trim().toLowerCase()), Double.MAX_VALUE);
    }
}

