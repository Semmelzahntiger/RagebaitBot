package com.tree3.Integralogic;

import static com.tree3.Integralogic.IntegralGenerate.Difficulty.*;

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

    public static String difficultyToString(IntegralGenerate.Difficulty difficulty){
        return switch (difficulty){
            case NONE -> "none";
            case EASY -> "easy";
            case MEDIUM -> "medium";
            case HARD -> "hard";
            case SPICY -> "spicy";
            case DICY -> "dicy";
            case ICY -> "icy";
            case RICY -> "ricy";
        };
    }

    public static IntegralGenerate.Difficulty stringToDifficulty(String difficulty){
        return switch (difficulty.toLowerCase()) {
            case "none" -> NONE;
            case "easy" -> EASY;
            case "medium" -> MEDIUM;
            case "hard" -> HARD;
            case "spicy" -> SPICY;
            case "dicy" -> DICY;
            case "icy" -> ICY;
            case "ricy" -> RICY;
            default -> NONE;
        };
    }

    public static boolean equalInMargin(double number1, double number2, double marginOfError) {
        if (marginOfError < 0) {
            throw new IllegalArgumentException("marginOfError darf nicht negativ sein.");
        }
        return Math.abs(number1 - number2) <= marginOfError;
    }


}
