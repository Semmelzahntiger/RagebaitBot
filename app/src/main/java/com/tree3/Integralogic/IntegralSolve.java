package com.tree3.Integralogic;

import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

public class IntegralSolve {

    public enum FunctionType {
        POLYNOMIAL,
        EXPONENTIAL,
        SINE,
        COSINE,
        XEXPONENTIAL,
        ARCTAN,
        SINCOS,
        LAURENT,
        PARTIAL,
        SQRT
    }
    public Map<FunctionType, Integer> functionMap = generateMap();

    final FunctionType type;
    final int[] bounds;
    final int[] data;
    FunctionType[] arrayType = new FunctionType[FunctionType.values().length];
    int a;
    int b;
    int c;

    public IntegralSolve(FunctionType functiontype, int[] bounds, int[] data){
        this.type = functiontype;
        this.bounds = bounds;
        this.data = data;
    }

    public double solve(){
        abcd();
        return switch (type) {
            case POLYNOMIAL -> bounds(this::polysolve);
            case EXPONENTIAL -> bounds(this::expsolve);
            case SINE -> bounds(this::sinsolve);
            case COSINE -> bounds(this::cossolve);
            case XEXPONENTIAL -> bounds(this::xexpsolve);
            case ARCTAN -> bounds(this::arctansolve);
            case SINCOS -> bounds(this::sincossolve);
            case SQRT -> bounds(this::sqrtsolve);
            case LAURENT -> bounds(this::laurentsolve);
            case PARTIAL -> bounds(this::partialsolve);
        };
    }

    private void abcd(){
        this.a = data[0];
        this.b = data[1];
        this.c = data[2];
    }

    private double bounds(Function<Integer, Double> function) {
        return function.apply(bounds[1]) - function.apply(bounds[0]);
    }

    private double polysolve(int x) {
        double answer = 0;
        for(int i = 0; i < 3; i++) {
            answer = answer + 1/(1.0 + i) * data[i] * Math.pow(x, i+1);

        }
        return answer;
    }

    private double expsolve(int x) {
        return (double) a/b * (Math.exp(b*x + c));
    }

    private double sinsolve(int x) {
        return (double) -a/b * (Math.cos(b*x + c));
    }

    private double cossolve(int x) {
        return (double) a/b * (Math.sin(b*x + c));
    }

    private double xexpsolve(int x) {
        return (double) a/b * Math.exp(b*x + c) * (x - (double) 1 /b);
    }

    private double arctansolve(int x) {
        //problem given by Integral of a/b²+(cx)²
        return (double) a /(b*c) * Math.atan((double) (c * x) /b);
    }

    private double sincossolve(int x) {
        return (double) a /(2*b) * Math.pow(Math.sin(b*x+c), 2);
    }

    private double laurentsolve(int x) {
        //ax^-1 + bx^-2 +cx^-3
        return a * Math.log(Math.abs(x)) - b * Math.pow(x, -1)- (double) 1 /2 * c * Math.pow(x, -2);
    }

    private double partial1solve(int x) {
        // a/[(x-b)*(x-c)] = a/(x^2-(b+c)x+bc)
        return (double) a /(b-c) * Math.log(Math.abs((x-b)/(x-c)));
    }

    private double partial2solve(int x) {
        return (double) a /(b-x);
    }

    private double partialsolve(int x){
        if (b==c){
            return partial2solve(x);
        }
        else {
            return partial1solve(x);
        }
    }

    private double sqrtsolve(int x) {
        return (double) a/b * 1/Math.sqrt(b*x + c);
    }

    private Map<FunctionType, Integer> generateMap(){
        Map<FunctionType, Integer> out = new HashMap<>();
        FunctionType[] functionArray = getFunctionTypesArray();
        for (int i = 0; i < functionArray.length; i++) {
            out.put(functionArray[i], i);
        }
        return out;
    }

    public static FunctionType[] getFunctionTypesArray() {
        return IntegralSolve.FunctionType.values();
    }

    public Map<FunctionType, Integer> getMapNonStatic(){
        return functionMap;
    }

    //public static Map<FunctionType, Integer> getMap(){
    //    return new IntegralSolve(new IntegralGenerate().getIntegralProblem()).getMapNonStatic();
    //}

}
