package com.github.thepurityofchaos.utils.kmeans;

import java.util.Map;

public class EuclideanDistance implements Distance {

    @Override
    public double calculate(Map<String, Double> c1, Map<String, Double> c2) {
        double sum = 0;
            for(String color : c1.keySet()){
                Double v1 = c1.get(color);
                Double v2 = c2.get(color);
                if(v1!=null && v2!=null){
                    sum+=Math.pow(v1-v2,2);
                }
            }
        return Math.sqrt(sum);
    }
    public EuclideanDistance(){
        super();
    }
}
