package com.github.thepurityofchaos.utils.math;

public class SolutionException extends Exception {
    Object operation;
    Object left;
    Object right;
    public SolutionException(Object op, Object rhs, Object lhs){
        operation = op;
        left = lhs;
        right = rhs;
    }
}
