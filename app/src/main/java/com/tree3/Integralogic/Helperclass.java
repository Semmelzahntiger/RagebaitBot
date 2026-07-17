package com.tree3.Integralogic;

public class Helperclass {

    public static int getRandomNumber(int min, int max) {
        return (int) ((Math.random() * (max - min)) + min);
    }

    public static int FunctiontypeToInt(IntegralSolve.FunctionType type){
        for (int i = 0; i < IntegralSolve.FunctionType.values().length; i++) {
            if(IntToFunctiontype(i) == type){
                return i;
            }
        }
        return -1;
    }

    public static IntegralSolve.FunctionType IntToFunctiontype(int a){
        IntegralSolve.FunctionType[] typearray =  IntegralSolve.FunctionType.values();
        return typearray[a];
    }


}
