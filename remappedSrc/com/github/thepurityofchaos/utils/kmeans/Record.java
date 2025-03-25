package com.github.thepurityofchaos.utils.kmeans;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.github.thepurityofchaos.utils.math.ColorUtils;

public class Record {
    private final int[] location;
    private final Map<String,Double> colors;

    public Record(int[] loc, int colors){
        // loc should be [x,y]
        this.location = loc;
        this.colors = new HashMap<>();
        this.colors.put("r",ColorUtils.getRed(colors)+0.0);
        this.colors.put("g",ColorUtils.getGreen(colors)+0.0);
        this.colors.put("b",ColorUtils.getBlue(colors)+0.0);
        this.colors.put("a",ColorUtils.getAlpha(colors)+0.0);
    }
    public Record getRecord(){
        return this;
    }
    public Map<String,Double> getInfo(){
        return colors;
    }
    public int[] location(){
        return location;
    }
    public List<Double> getRGBList(){
        List<Double> retVal = new ArrayList<>();
        retVal.add(colors.get("r"));
        retVal.add(colors.get("g"));
        retVal.add(colors.get("b"));
        return retVal;
    }

}
