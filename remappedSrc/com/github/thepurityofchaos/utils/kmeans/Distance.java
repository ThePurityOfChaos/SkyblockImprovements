package com.github.thepurityofchaos.utils.kmeans;

import java.util.Map;

public interface Distance {
    double calculate(Map<String,Double> c1, Map<String,Double> c2);
}
