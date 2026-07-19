package com.tree3.Integralogic;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static com.tree3.Integralogic.IntegralGenerate.Difficulty.*;
import static com.tree3.Integralogic.IntegralSolve.FunctionType.*;


public class IntegralGenerate {

    IntegralProblem integralProblem;
    int amount;
    public enum Difficulty{
        NONE,
        EASY,
        MEDIUM,
        HARD,
        SPICY,
        DICY,
        ICY,
        RICY
    }


    public static final Map<IntegralSolve.FunctionType, Difficulty> difficultymap = new HashMap<>();
    public static final List<IntegralSolve.FunctionType> easytypes = List.of(POLYNOMIAL, EXPONENTIAL, SINE, COSINE);
    public static final List<IntegralSolve.FunctionType> mediumtypes = List.of(XEXPONENTIAL, ARCTAN, SINCOS, LAURENT, PARTIAL, SQRT);


    static{
        difficultymap.put(POLYNOMIAL, EASY);
        difficultymap.put(EXPONENTIAL, EASY);
        difficultymap.put(SINE, EASY);
        difficultymap.put(COSINE, EASY);
        difficultymap.put(XEXPONENTIAL, MEDIUM);
        difficultymap.put(ARCTAN, MEDIUM);
        difficultymap.put(SINCOS, MEDIUM);
        difficultymap.put(LAURENT, MEDIUM);
        difficultymap.put(PARTIAL, MEDIUM);
        difficultymap.put(SQRT, MEDIUM);
    }

    public IntegralGenerate(){
        int amount = 2;
        this.amount = amount;
        IntegralSolve.FunctionType[] types = typeGenerator();
        int[][] data = dataGenerator();
        int[] bounds = boundsGenerator();
        this.integralProblem = new IntegralProblem(amount, types, data, bounds);
    }

    public IntegralGenerate(int amount){
        if (amount < difficultymap.values().toArray().length){
            this.amount = amount;
        }
        else {
            amount = difficultymap.values().toArray().length;
        }
        IntegralSolve.FunctionType[] types = typeGenerator();
        int[][] data = dataGenerator();
        int[] bounds = boundsGenerator();
        this.integralProblem = new IntegralProblem(amount, types, data, bounds);
    }

    public IntegralGenerate(Difficulty difficulty){
        int amount = 2;
        this.amount = amount;
        IntegralSolve.FunctionType[] types = difficultyTypeGenerator(difficulty);
        int[][] data = dataGenerator();
        int[] bounds = boundsGenerator();
        this.integralProblem = new IntegralProblem(amount, types, data, bounds);
    }

    public IntegralGenerate(int amount, Difficulty difficulty){
        int maxamount;
        if (difficulty == EASY || difficulty == MEDIUM){
            maxamount = difficultyToList(difficulty).toArray().length;
        }
        else{
            maxamount = 0;
        }

        if (amount < maxamount + 1){
            this.amount = amount;
        }
        else {
            amount = maxamount + 1;
        }

        IntegralSolve.FunctionType[] types = difficultyTypeGenerator(difficulty);
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
                data[i][j] = (1 - 2*Helperclass.getRandomNumber(0, 2)) * Helperclass.getRandomNumber(1, 6);
            }
        }
        return data;
    }

    public int[] boundsGenerator(){
        int[] bounds = new int[2];
        int a = (1 - 2*Helperclass.getRandomNumber(0, 2)) * Helperclass.getRandomNumber(1, 6);
        int b = (1 - 2*Helperclass.getRandomNumber(0, 2)) * Helperclass.getRandomNumber(1, 6);
        while(a == b){
            b = (1 - 2*Helperclass.getRandomNumber(0, 2)) * Helperclass.getRandomNumber(1, 6);
        }
        if (a>b){
            bounds[0] = b;
            bounds[1] = a;
        }
        else {
            bounds[0] = a;
            bounds[1] = b;
        }

        return bounds;
    }

    public IntegralSolve.FunctionType[] typeGenerator(){
        List<IntegralSolve.FunctionType> usedTypes = new ArrayList<>();
        IntegralSolve.FunctionType[] types = new IntegralSolve.FunctionType[amount];
        IntegralSolve.FunctionType helpertype;
        for (int i = 0; i < amount; i++) {
            helpertype = Helperclass.IntToFunctiontype(Helperclass.getRandomNumber(0, IntegralSolve.FunctionType.values().length));
            while(usedTypes.contains(helpertype)){
                helpertype = Helperclass.IntToFunctiontype(Helperclass.getRandomNumber(0, IntegralSolve.FunctionType.values().length));
            }
            types[i] = helpertype;
            usedTypes.add(helpertype);
        }
        return types;
    }

    private List<IntegralSolve.FunctionType> difficultyToList(Difficulty difficulty){
        if (difficulty == EASY){
            return easytypes;
        } else if (difficulty == MEDIUM) {
            return mediumtypes;
        } else {
            return easytypes;
        }
    }

    public IntegralSolve.FunctionType[] difficultyTypeGenerator(Difficulty difficulty){
        List<IntegralSolve.FunctionType> usedTypes = new ArrayList<>();
        IntegralSolve.FunctionType[] types = new IntegralSolve.FunctionType[amount];
        IntegralSolve.FunctionType helpertype;
        for (int i = 0; i < amount; i++) {
            helpertype = difficultyToList(difficulty).get(Helperclass.getRandomNumber(0, difficultyToList(difficulty).toArray().length));
            while (usedTypes.contains(helpertype)){
                helpertype = Helperclass.IntToFunctiontype(Helperclass.getRandomNumber(0, IntegralSolve.FunctionType.values().length));
            }
            types[i] = helpertype;
            usedTypes.add(helpertype);
        }
        return types;
    }

    public IntegralProblem getIntegralProblem(){
        return  integralProblem;
    }
}
