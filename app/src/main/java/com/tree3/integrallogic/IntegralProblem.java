package com.tree3.integrallogic;

public class IntegralProblem {

    final int amount;
    final IntegralSolve.FunctionType[] types;
    final int[][] data;
    final int[] bounds;




    public IntegralProblem(int amount, IntegralSolve.FunctionType[] types, int[][] data, int[] bounds){
        this.amount = amount;
        this.types = types;
        this.data = data;
        this.bounds = bounds;
    }

    public int getAmount(){
        return amount;
    }

    public IntegralSolve.FunctionType[] getTypes(){
        return types;
    }

    public int[][] getData(){
        return data;
    }

    public int[] getBounds(){
        return bounds;
    }
}
