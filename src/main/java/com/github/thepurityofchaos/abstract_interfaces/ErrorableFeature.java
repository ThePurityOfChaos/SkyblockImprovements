package com.github.thepurityofchaos.abstract_interfaces;

import java.util.HashSet;
import java.util.Set;

import com.github.thepurityofchaos.storage.Bazaar;
import com.github.thepurityofchaos.utils.Utils;

import net.minecraft.text.Text;

public abstract class ErrorableFeature extends Feature {
    protected Set<Text> errors = new HashSet<>();

    public Set<Text> getErrors(){return errors;}

    protected void addError(String s){
        errors.add(Text.of(Utils.getColorString('4')+s));
    }
    public void resolveErrors(){
        for(Text error : errors){
            Bazaar.putInBuy(error.getString(), 0.0);
            Bazaar.putInSell(error.getString(), 0.0);
        }
    }
}
