package com.github.thepurityofchaos.utils.kmeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Centroid {
    private Map<String,Double> coordinates;

    public Centroid(Map<String,Double> coordinates){
        this.coordinates = coordinates;
    }
    public Map<String,Double> getCoordinates(){
        return coordinates;
    }
    public List<Double> getRGBList(){
        List<Double> retVal = new ArrayList<>();
        retVal.add(coordinates.get("r"));
        retVal.add(coordinates.get("g"));
        retVal.add(coordinates.get("b"));
        return retVal;
    }
    public Centroid(Record recordToCopy){
        this.coordinates = new HashMap<>();
        this.coordinates.putAll(recordToCopy.getInfo());
    }
}
