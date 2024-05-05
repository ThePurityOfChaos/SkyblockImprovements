package com.github.thepurityofchaos.utils.math;


import java.util.HashMap;

import java.util.Map;
import java.util.Stack;

import com.github.thepurityofchaos.utils.Utils;

public class MathSolutions {
    private static Map<Character,Integer> operations = new HashMap<>();
    static{
        
        operations.put('+',0);
        operations.put('-',0);
        operations.put('*',1);
        operations.put('/',1);
        operations.put('^',2);
        
    }
    //Some assistance from ChatGPT on this, mostly helping with the idea of using Stacks.
    //This isn't a major portion of the system, mainly something to help do some simple math whenever it's needed.
    public static double doMath(String message){
        try{
            String strippedMessage = Utils.removeText(message);
            Stack<Double> vals = new Stack<>();
            Stack<Character> ops = new Stack<>();

            for(int i=0; i<strippedMessage.length(); i++){
                char current = strippedMessage.charAt(i);
                //number
                if(isNumberPortion(current)){
                    String number = "";
                    while(i<strippedMessage.length()&&isNumberPortion(strippedMessage.charAt(i))){
                        number+=strippedMessage.charAt(i);
                        i++;
                    }
                    //not doing this skipped forward by 1
                    i--;
                    vals.push(Double.parseDouble(number));
                }else
                //parentheses, recurse
                if(current == '('){ 
                    int end = findSubstring(strippedMessage, i);
                    vals.push(doMath(strippedMessage.substring(i+1,end)));
                    i=end;
                }   
                else
                //op
                if(operations.containsKey(current)){
                    while(!ops.empty() && (priority(ops.peek())>=priority(current))) vals.push(solve(ops.pop(),vals.pop(),vals.pop()));
                    ops.push(current);
                }
                
            }
            //finish up
            while(!ops.empty()) vals.push(solve(ops.pop(),vals.pop(),vals.pop()));

            return vals.pop();
        }catch(Exception e){
            return -0.0;
        }
    }
    private static double solve(char op, double rhs, double lhs){
        switch(op){
            case '+': return lhs+rhs;
            case '-': return lhs-rhs;
            case '*': return lhs*rhs;
            case '/': return lhs/rhs;
            case '^': return Math.pow(lhs,rhs);
            default: return -0.0;
        }
    }
    private static double priority(char c){
        return operations.get(c);
    }
    private static boolean isNumberPortion(char c){
        return (Character.isDigit(c)|| c == '.');
    }
    private static int findSubstring(String s, int start){
        int end = start+1;
        int currentOpenParentheses = 1;
        while(currentOpenParentheses>0){
            char c = s.charAt(end);
            if(c == '(') currentOpenParentheses++;
            else if(c == ')') currentOpenParentheses--;
            end++;
        }
        return end-1;
    }



}
