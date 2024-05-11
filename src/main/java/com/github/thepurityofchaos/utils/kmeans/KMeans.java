package com.github.thepurityofchaos.utils.kmeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.stream.Collectors;

// https://www.baeldung.com/java-k-means-clustering-algorithm has this implementation. 

public class KMeans {
    private static final Random random = new Random();

    public static Map<Centroid,List<Record>> fit(List<Record> records, int k, Distance distanceType, int maxIterations){
        List<Centroid> centroids = randomCentroids(records, k);
        Map<Centroid,List<Record>> clusters = new HashMap<>();
        Map<Centroid,List<Record>> lastState = new HashMap<>();

        for(int i=0; i<maxIterations; i++){
            boolean isLastIteration = i==maxIterations-1;

            for(Record record : records){
                Centroid centroid = nearestCentroid(record, centroids, distanceType);
                assign(clusters, record, centroid);
            }
            for (Centroid centroid : centroids) {
                clusters.putIfAbsent(centroid, new ArrayList<>());
            }
            boolean shouldTerminate = isLastIteration || clusters.equals(lastState);
            lastState = clusters;
            if(shouldTerminate){
                break;
            }
            centroids = relocateCentroids(clusters);
            clusters = new HashMap<>();
        }
        
        return lastState;
    }
    private static List<Centroid> randomCentroids(List<Record> records, int k){
        List<Centroid> centroids = new ArrayList<>();
        Map<String,Double> maxs = new HashMap<>();
        Map<String,Double> mins = new HashMap<>();
        
        for(Record record : records){
            record.getInfo().forEach((key,value)->{
                maxs.compute(key, (k1,max) -> max == null || value > max ? value:max );
                mins.compute(key, (k1,min) -> min == null || value < min ? value:min );
            });
        }
        Set<String> attributes = records.stream().flatMap(e -> e.getInfo().keySet().stream()).collect(Collectors.toSet());

        for(int i = 0; i < k; i++){
            Map<String,Double> coordinates = new HashMap<>();
            for(String attribute : attributes){
                double max = maxs.get(attribute);
                double min = mins.get(attribute);
                coordinates.put(attribute, random.nextDouble()*(max-min)+min);
            }
            centroids.add(new Centroid(coordinates));
        }

        return centroids;
    }
    private static Centroid nearestCentroid(Record record, List<Centroid> centroids, Distance distanceType){
        double minDistance = Double.MAX_VALUE;
        Centroid nearest = null;
        for(Centroid centroid : centroids){
            double currentDistance = distanceType.calculate(record.getInfo(), centroid.getCoordinates());
            if(currentDistance<minDistance){
                minDistance = currentDistance;
                nearest = centroid;
            }
        }
        return nearest;
    }
    private static void assign(Map<Centroid,List<Record>> clusters, Record record, Centroid centroid){
        clusters.compute(centroid, (key,list)-> {
            if(list==null){
                list=new ArrayList<>();
            }
            list.add(record);
            return list;
        });
    }
    private static Centroid average(Centroid centroid, List<Record> records){
        if(records == null || records.isEmpty()){
            return centroid;
        }
        Map<String,Double> average = centroid.getCoordinates();
        records.stream().flatMap(e -> e.getInfo().keySet().stream()).forEach(k-> average.put(k,0.0));
        for(Record record : records){
            record.getInfo().forEach((k,v) -> average.compute(k, (k1,currentValue)-> v+currentValue));
        }
        average.forEach((k,v)-> average.put(k,v/records.size()));
        return new Centroid(average);
    }
    private static List<Centroid> relocateCentroids(Map<Centroid,List<Record>> clusters){
        return clusters.entrySet().stream().map(e-> average(e.getKey(), e.getValue())).collect(Collectors.toList());
    }
    
}
