package com.tree3.Integralogic;


public class IntegralGenerate {

    IntegralProblem integralProblem;
    int amount;

    public IntegralGenerate(){
        int amount = 2;
        this.amount = amount;
        IntegralSolve.FunctionType[] types = typeGenerator();
        int[][] data = dataGenerator();
        int[] bounds = boundsGenerator();
        this.integralProblem = new IntegralProblem(amount, types, data, bounds);
    }

    public IntegralGenerate(int amount){
        this.amount = amount;
        IntegralSolve.FunctionType[] types = typeGenerator();
        int[][] data = dataGenerator();
        int[] bounds = boundsGenerator();
        this.integralProblem = new IntegralProblem(amount, types, data, bounds);
    }

    public IntegralGenerate(IntegralSolve.FunctionType[] types){
        this.amount = types.length;
        int[][] data = dataGenerator();
        int[] bounds = boundsGenerator();
        this.integralProblem = new IntegralProblem(amount, types, data, bounds);
    }

    public int[][] dataGenerator(){
        int[][] data = new int[amount][3];
        for (int i = 0; i < amount; i++) {
            for (int j = 0; j < 3; j++) {
                data[i][j] = (1 - 2*Helperclass.getRandomNumber(0, 1)) * Helperclass.getRandomNumber(1, 5);
            }
        }
        return data;
    }

    public int[] boundsGenerator(){
        int[] bounds = new int[2];
        bounds[0] = (1 - 2*Helperclass.getRandomNumber(0, 1)) * Helperclass.getRandomNumber(1, 5);
        bounds[1] = (1 - 2*Helperclass.getRandomNumber(0, 1)) * Helperclass.getRandomNumber(1, 5);
        return bounds;
    }

    public IntegralSolve.FunctionType[] typeGenerator(){
        IntegralSolve.FunctionType[] types = new IntegralSolve.FunctionType[amount];
        for (int i = 0; i < amount; i++) {
            types[i] = Helperclass.IntToFunctiontype(Helperclass.getRandomNumber(0, IntegralSolve.FunctionType.values().length));
        }
        return types;
    }

    public IntegralProblem getIntegralProblem(){
        return  integralProblem;
    }
}
